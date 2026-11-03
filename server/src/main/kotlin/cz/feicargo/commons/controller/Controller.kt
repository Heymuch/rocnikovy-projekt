package cz.feicargo.commons.controller

// Pozn.: nedědit z `suspend (C) -> Unit` – viz UseCase.
interface Controller<C : Controller.Context> {
    interface Context

    suspend operator fun invoke(ctx: C)

    suspend fun C.use() = invoke(this)
}
