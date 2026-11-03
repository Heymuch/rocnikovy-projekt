package cz.feicargo.commons.controller.rest

import cz.feicargo.commons.controller.Controller
import io.ktor.server.application.ApplicationCall

interface RestController<C : RestController.Context> : Controller<C> {
    interface Context : Controller.Context {
        val call: ApplicationCall
    }
}
