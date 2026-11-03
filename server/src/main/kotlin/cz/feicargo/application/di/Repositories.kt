package cz.feicargo.application.di

import cz.feicargo.commons.persistence.TransactionManager
import cz.feicargo.commons.persistence.exposed.ExposedTransactionManager
import cz.feicargo.station.persistence.StationRepository
import cz.feicargo.station.persistence.exposed.StationExposedRepository
import io.ktor.server.plugins.di.DependencyRegistry

fun DependencyRegistry.repositories() {
    provide<TransactionManager> {
        ExposedTransactionManager()
    }

    provide<StationRepository> {
        StationExposedRepository()
    }
}
