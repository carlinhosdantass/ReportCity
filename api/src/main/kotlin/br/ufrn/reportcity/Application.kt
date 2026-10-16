package br.ufrn.reportcity

import br.ufrn.reportcity.adapters.database.DatabaseConfig
import br.ufrn.reportcity.adapters.database.PostgresCategoryRepository
import br.ufrn.reportcity.adapters.database.PostgresIssueRepository
import br.ufrn.reportcity.adapters.database.createDataSource
import br.ufrn.reportcity.adapters.database.migrate
import br.ufrn.reportcity.adapters.http.configureErrors
import br.ufrn.reportcity.adapters.http.configureRoutes
import br.ufrn.reportcity.application.ReportCityService
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import org.jetbrains.exposed.v1.jdbc.Database

fun main() {
    embeddedServer(
        factory = Netty,
        port = (System.getenv("PORT") ?: "8080").toInt(),
        host = "0.0.0.0",
        module = Application::module,
    ).start(wait = true)
}

fun Application.module() {
    val dataSource = createDataSource(DatabaseConfig.fromEnvironment())
    migrate(dataSource)
    monitor.subscribe(ApplicationStopped) { dataSource.close() }

    val database = Database.connect(dataSource)
    configure(
        ReportCityService(
            PostgresCategoryRepository(database),
            PostgresIssueRepository(database),
        ),
    )
}

fun Application.configure(service: ReportCityService) {
    install(ContentNegotiation) { json() }
    configureErrors()
    configureRoutes(service)
}
