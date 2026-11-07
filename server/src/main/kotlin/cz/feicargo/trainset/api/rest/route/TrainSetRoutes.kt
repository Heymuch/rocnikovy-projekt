package cz.feicargo.trainset.api.rest.route

import cz.feicargo.trainset.api.rest.TrainSets
import cz.feicargo.trainset.api.rest.controller.SearchTrainSetsRestController
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.server.routing.openapi.describe
import io.ktor.utils.io.ExperimentalKtorApi

@OptIn(ExperimentalKtorApi::class)
object TrainSetRoutes {
    object Search {
        fun Route.get() =
            get<TrainSets.Search> { request ->
                with(application.dependencies.resolve<SearchTrainSetsRestController>()) {
                    SearchTrainSetsRestController.Context(call, request.page, request.size).use()
                }
            }.describe {
                summary = "Overview of train sets with pagination"
                tag("TrainSets")
                responses {
                    HttpStatusCode.OK {
                        description = "Paginated list of train sets"
                    }
                }
            }
    }
}
