package cz.feicargo.route.service

import kotlin.uuid.Uuid

interface RouteService {
    suspend fun findRoute(from: Uuid, to: Uuid): List<Uuid>
    suspend fun calculateRouteLengthKm(routeNodes: List<Uuid>): Double
}
