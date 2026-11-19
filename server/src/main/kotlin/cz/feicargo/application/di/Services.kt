package cz.feicargo.application.di

import cz.feicargo.route.service.RouteService
import cz.feicargo.route.service.implementation.RoutePostgresService
import io.ktor.server.plugins.di.DependencyRegistry

fun DependencyRegistry.services() {
    provide<RouteService> {
        RoutePostgresService()
    }
}
