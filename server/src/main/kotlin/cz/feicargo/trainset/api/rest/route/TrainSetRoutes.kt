package cz.feicargo.trainset.api.rest.route

import cz.feicargo.trainset.api.rest.TrainSets
import cz.feicargo.trainset.api.rest.controller.GetTrainSetDetailRestController
import cz.feicargo.trainset.api.rest.controller.SearchTrainSetsRestController
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.server.routing.openapi.describe
import io.ktor.utils.io.ExperimentalKtorApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalKtorApi::class)
object TrainSetRoutes {
    object Search {
        fun Route.get() =
            get<TrainSets.Search> { request ->
                with(application.dependencies.resolve<SearchTrainSetsRestController>()) {
                    val xInclude = call.request.headers["X-Include"]
                    val includes = xInclude?.split(",")?.map { it.trim() } ?: emptyList()
                    SearchTrainSetsRestController.Context(call, request.page, request.size, includes).use()
                }
            }.describe {
                summary = "Search train sets with pagination"
                tag("TrainSets")
                parameters {
                    header("X-Include") {
                        description = "For example: station"
                    }
                }
                responses {
                    HttpStatusCode.OK { description = "Paginated list of train sets" }
                }
            }
    }

    object GetDetail {
        fun Route.get() =
            get<TrainSets.Detail> { request ->
                with(application.dependencies.resolve<GetTrainSetDetailRestController>()) {
                    val uuid = Uuid.parse(request.id)
                    val xInclude = call.request.headers["X-Include"]
                    val includes = xInclude?.split(",")?.map { it.trim() } ?: emptyList()
                    GetTrainSetDetailRestController.Context(call, uuid, includes).use()
                }
            }.describe {
                summary = "Detail of a train set with orders (for Admin)"
                tag("TrainSets")

                parameters {
                    header("X-Include") {
                        description = "For example: locomotive,wagon,station"
                    }
                }

                responses {
                    HttpStatusCode.OK { description = "Detail of train set with orders" }
                    HttpStatusCode.NotFound { description = "Train set not found" }
                }
            }
    }
}
