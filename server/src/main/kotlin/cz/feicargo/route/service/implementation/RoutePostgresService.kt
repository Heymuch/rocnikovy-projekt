package cz.feicargo.route.service.implementation

import cz.feicargo.route.persistence.RailwayGraphRepository
import cz.feicargo.route.service.RouteService
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.UuidColumnType
import org.jetbrains.exposed.v1.core.statements.StatementType
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import java.util.UUID
import kotlin.math.round
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid

/**
 * Hledání trasy přes databázovou funkci `find_shortest_path`, která volá `pgr_dijkstra`
 * z rozšíření pgRouting (viz migrace `V01_01_00__shortest_path_function.sql`).
 */
class RoutePostgresService (
    private val railwayGraphRepository: RailwayGraphRepository,
) : RouteService {
    /**
     * Vrací uzly nejkratší cesty od [from] po [to] (včetně obou konců),
     * nebo prázdný seznam, pokud cesta neexistuje.
     */
    override suspend fun findRoute(
        from: Uuid,
        to: Uuid,
    ): List<Uuid> =
        suspendTransaction {
            exec(
                stmt = """select "node_id" from "find_shortest_path"(?, ?) order by "seq"""",
                args = listOf(UuidColumnType() to from, UuidColumnType() to to),
                explicitStatementType = StatementType.SELECT,
            ) { row ->
                row.get("node_id", UUID::class.java)?.toKotlinUuid()
            }?.filterNotNull()?.toList().orEmpty()
        }

    override suspend fun calculateRouteLengthKm(routeNodes: List<Uuid>): Double {
        if (routeNodes.size < 2) return 0.0

        val edges = suspendTransaction {
            railwayGraphRepository.selectEdgesBetweenNodes(routeNodes.distinct())
        }

        val distanceMap = buildMap {
            edges.forEach { edge ->
                put(edge.nodeAId to edge.nodeBId, edge.distanceMeters)
                put(edge.nodeBId to edge.nodeAId, edge.distanceMeters)
            }
        }

        val totalMeters = routeNodes.zipWithNext().sumOf { (from, to) ->
            distanceMap[from to to] ?: 0
        }

        return round(totalMeters / 10.0) / 100.0
    }
}
