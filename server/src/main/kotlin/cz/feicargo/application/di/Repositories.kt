package cz.feicargo.application.di

import cz.feicargo.cargotype.persistence.CargoTypeRepository
import cz.feicargo.cargotype.persistence.exposed.CargoTypeExposedRepository
import cz.feicargo.commons.persistence.TransactionManager
import cz.feicargo.commons.persistence.exposed.ExposedTransactionManager
import cz.feicargo.station.persistence.StationRepository
import cz.feicargo.station.persistence.exposed.StationExposedRepository
import cz.feicargo.trainset.persistence.TrainSetRepository
import cz.feicargo.trainset.persistence.exposed.TrainSetExposedRepository
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
}
