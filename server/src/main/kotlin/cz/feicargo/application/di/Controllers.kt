package cz.feicargo.application.di

import cz.feicargo.cargotype.api.rest.controller.GetCargoTypesRestController
import cz.feicargo.order.api.rest.controller.CalculateOrderRestController
import cz.feicargo.order.api.rest.controller.CreateOrderRestController
import cz.feicargo.order.api.rest.controller.DeleteOrderRestController
import cz.feicargo.order.api.rest.controller.GetOrderDetailRestController
import cz.feicargo.order.api.rest.controller.SearchOrdersRestController
import cz.feicargo.order.api.rest.controller.UpdateOrderRestController
import cz.feicargo.station.api.rest.controller.SearchForStationsRestController
import cz.feicargo.trainset.api.rest.controller.GetTrainSetDetailRestController
import cz.feicargo.trainset.api.rest.controller.SearchTrainSetsRestController
import cz.feicargo.user.api.rest.controller.BlockUserRestController
import cz.feicargo.user.api.rest.controller.SearchUsersRestController
import io.ktor.server.plugins.di.DependencyRegistry

fun DependencyRegistry.controllers() {
    provide<SearchForStationsRestController> {
        SearchForStationsRestController(
            useCase = resolve(),
        )
    }

    provide<GetCargoTypesRestController> {
        GetCargoTypesRestController(
            useCase = resolve(),
        )
    }

    provide<SearchTrainSetsRestController> {
        SearchTrainSetsRestController(
            useCase = resolve(),
        )
    }

    provide<SearchOrdersRestController> {
        SearchOrdersRestController(
            useCase = resolve(),
        )
    }

    provide<GetTrainSetDetailRestController> {
        GetTrainSetDetailRestController(
            useCase = resolve(),
        )
    }

    provide<GetOrderDetailRestController> {
        GetOrderDetailRestController(
            useCase = resolve(),
            )
    }

    provide<CreateOrderRestController> {
        CreateOrderRestController(
            useCase = resolve(),
            )
    }

    provide<CalculateOrderRestController> {
        CalculateOrderRestController(
            useCase = resolve(),
        )
    }

    provide<UpdateOrderRestController> {
        UpdateOrderRestController(
            useCase = resolve(),
        )
    }

    provide<DeleteOrderRestController> {
        DeleteOrderRestController(
            useCase = resolve(),
        )
    }

    provide<SearchUsersRestController> {
        SearchUsersRestController(
            useCase = resolve(),
        )
    }

    provide<BlockUserRestController> {
        BlockUserRestController(
            useCase = resolve(),
        )
    }
}
