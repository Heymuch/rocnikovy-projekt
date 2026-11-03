package cz.feicargo.commons.usecase

// Pozn.: nedědit z `suspend (C) -> R` – Ktor DI při procházení nadtypů padá
// na suspend funkčních typech (Function2 vs. 2 typové argumenty).
interface UseCase<C : UseCase.Context, R : UseCase.Result> {
    interface Context

    interface Result

    suspend operator fun invoke(context: C): R
}
