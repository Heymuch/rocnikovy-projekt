package cz.feicargo.application

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.config.ApplicationConfig
import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.core.vendors.PostgreSQLDialect
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabaseConfig
import java.time.Duration

fun Application.databaseModule() {
    val config = environment.config
    migrateDatabase(config)

    val pool = createConnectionPool(config)
    configureExposed(pool)

    // Při vypnutí aplikace uzavřeme všechna spojení v poolu
    monitor.subscribe(ApplicationStopped) {
        pool.dispose()
    }
}

// Flyway nepodporuje R2DBC, migrace proto běží přes JDBC pod rolí flyway
private fun migrateDatabase(config: ApplicationConfig) {
    val server = config.property("postgresql.server").getString()
    val port = config.property("postgresql.port").getString()
    val database = config.property("postgresql.database").getString()
    val username = config.property("flyway.username").getString()
    val password = config.property("flyway.password").getString()
    val locations = config.property("flyway.locations").getList().toTypedArray()

    Flyway
        .configure()
        .dataSource(
            "jdbc:postgresql://$server:$port/$database",
            username,
            password,
        ).locations(*locations)
        .load()
        .migrate()
}

// Aplikace se připojuje pod rolí app, ne pod rolí flyway
private fun createConnectionPool(config: ApplicationConfig): ConnectionPool {
    val connectionFactory =
        PostgresqlConnectionFactory(
            PostgresqlConnectionConfiguration
                .builder()
                .host(config.property("postgresql.server").getString())
                .port(config.property("postgresql.port").getString().toInt())
                .database(config.property("postgresql.database").getString())
                .username(config.property("postgresql.username").getString())
                .password(config.property("postgresql.password").getString())
                .build(),
        )

    return ConnectionPool(
        ConnectionPoolConfiguration
            .builder(connectionFactory)
            .initialSize(2)
            .maxSize(10)
            .maxIdleTime(Duration.ofMinutes(30))
            .validationQuery("select 1")
            .build(),
    )
}

private fun configureExposed(pool: ConnectionPool): R2dbcDatabase =
    R2dbcDatabase.connect(
        connectionFactory = pool,
        databaseConfig =
            R2dbcDatabaseConfig {
                explicitDialect = PostgreSQLDialect()
            },
    )
