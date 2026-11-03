package cz.feicargo.commons.persistence

interface TransactionManager {
    suspend fun <T> newTransaction(body: suspend () -> T): T
}
