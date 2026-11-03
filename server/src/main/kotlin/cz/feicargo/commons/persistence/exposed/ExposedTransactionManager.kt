package cz.feicargo.commons.persistence.exposed

import cz.feicargo.commons.persistence.TransactionManager
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

class ExposedTransactionManager : TransactionManager {
    override suspend fun <T> newTransaction(body: suspend () -> T): T =
        suspendTransaction {
            body()
        }
}
