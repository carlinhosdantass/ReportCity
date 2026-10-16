package br.ufrn.reportcity.adapters.http

import br.ufrn.reportcity.application.InvalidInput
import br.ufrn.reportcity.application.ResourceConflict
import br.ufrn.reportcity.application.ResourceNotFound
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Problem(
    val type: String,
    val title: String,
    val status: Int,
    val detail: String,
)

private val problemJson = Json { encodeDefaults = true }

private suspend fun ApplicationCall.respondProblem(
    status: HttpStatusCode,
    type: String,
    title: String,
    detail: String,
) {
    val problem = Problem("/problems/$type", title, status.value, detail)
    respondText(problemJson.encodeToString(problem), ContentType.Application.ProblemJson, status)
}

fun Application.configureErrors() {
    install(StatusPages) {
        exception<BadRequestException> { call, error ->
            call.respondProblem(
                HttpStatusCode.BadRequest,
                "bad-request",
                "Bad request",
                error.cause?.message ?: error.message ?: "The request is invalid",
            )
        }
        exception<InvalidInput> { call, error ->
            call.respondProblem(
                HttpStatusCode.UnprocessableEntity,
                "invalid-input",
                "Invalid input",
                error.violations.joinToString("; "),
            )
        }
        exception<ResourceNotFound> { call, error ->
            call.respondProblem(HttpStatusCode.NotFound, "not-found", "Not found", error.message ?: "Not found")
        }
        exception<ResourceConflict> { call, error ->
            call.respondProblem(HttpStatusCode.Conflict, "conflict", "Conflict", error.message ?: "Conflict")
        }
        exception<Throwable> { call, error ->
            call.application.environment.log.error("Unhandled error", error)
            call.respondProblem(HttpStatusCode.InternalServerError, "internal", "Internal error", "Unexpected error")
        }
    }
}
