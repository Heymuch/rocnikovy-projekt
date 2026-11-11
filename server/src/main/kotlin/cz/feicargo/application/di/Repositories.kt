package cz.feicargo.application.di

import cz.feicargo.cargotype.persistence.CargoTypeRepository
import cz.feicargo.cargotype.persistence.exposed.CargoTypeExposedRepository
import cz.feicargo.commons.persistence.TransactionManager
import cz.feicargo.commons.persistence.exposed.ExposedTransactionManager
import cz.feicargo.locomotive.persistence.LocomotiveRepository
import cz.feicargo.locomotive.persistence.exposed.LocomotiveExposedRepository
import cz.feicargo.order.persistence.OrderRepository
import cz.feicargo.order.persistence.exposed.OrderExposedRepository
import cz.feicargo.station.persistence.StationRepository
import cz.feicargo.station.persistence.exposed.StationExposedRepository
import cz.feicargo.trainset.persistence.TrainSetRepository
import cz.feicargo.trainset.persistence.exposed.TrainSetExposedRepository
import cz.feicargo.user.persistence.UserRepository
import cz.feicargo.user.persistence.exposed.UserExposedRepository
import cz.feicargo.wagon.persistence.WagonRepository
import cz.feicargo.wagon.persistence.exposed.WagonExposedRepository
import io.ktor.server.plugins.di.DependencyRegistry

fun DependencyRegistry.repositories() {
    provide<TransactionManager> {
        ExposedTransactionManager()
    }

    provide<StationRepository> {
        StationExposedRepository()
    }

    provide<CargoTypeRepository> {
        CargoTypeExposedRepository()
    }

    provide<TrainSetRepository> {
        TrainSetExposedRepository()
    }

    provide<OrderRepository> {
        OrderExposedRepository()
    }

    provide<UserRepository> {
        UserExposedRepository()
    }

    provide<LocomotiveRepository> {
        LocomotiveExposedRepository()
    }

    provide<WagonRepository> {
        WagonExposedRepository()
    }
}
