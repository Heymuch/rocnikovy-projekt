package cz.feicargo.application

import cz.feicargo.application.di.controllers
import cz.feicargo.application.di.repositories
import cz.feicargo.application.di.useCases
import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies

fun Application.dependencyInjectionModule() =
    dependencies {
        repositories()
        useCases()
        controllers()
    }
