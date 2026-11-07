package cz.feicargo.trainset.persistence.exposed

import cz.feicargo.trainset.entity.TrainSet
import cz.feicargo.trainset.persistence.TrainSetRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.Expression
import org.jetbrains.exposed.v1.core.QueryBuilder
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.r2dbc.select

class OrderBySql(private val sql: String) : Expression<String>() {
    override fun toQueryBuilder(queryBuilder: QueryBuilder) {
        queryBuilder.append(sql)
    }
}

class TrainSetExposedRepository : TrainSetRepository {
    private fun selectQuery() = TrainSetTable.select(TrainSetTable.columns)

    override suspend fun search(page: Int, size: Int): Pair<List<TrainSet>, Long> {
        val query = selectQuery()
        val totalCount = query.count()

        val items = query
            .orderBy(
                OrderBySql("CASE WHEN departure_date >= CURRENT_TIMESTAMP THEN 0 ELSE 1 END") to SortOrder.ASC,
                OrderBySql("CASE WHEN departure_date >= CURRENT_TIMESTAMP THEN departure_date END") to SortOrder.ASC,
                OrderBySql("CASE WHEN departure_date < CURRENT_TIMESTAMP THEN departure_date END") to SortOrder.DESC
            )
            .limit(size)
            .offset((page * size).toLong())
            .map(ResultRow::toTrainSet)
            .toList()

        return items to totalCount
    }
}
