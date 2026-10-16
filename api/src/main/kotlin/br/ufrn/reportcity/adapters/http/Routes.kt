package br.ufrn.reportcity.adapters.http

import br.ufrn.reportcity.application.ReportCityService
import br.ufrn.reportcity.domain.Category
import br.ufrn.reportcity.domain.CategoryData
import br.ufrn.reportcity.domain.Issue
import br.ufrn.reportcity.domain.IssueData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.openapi.OpenApiInfo
import io.ktor.openapi.jsonSchema
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.request.receive
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.openapi.describe
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.utils.io.ExperimentalKtorApi
import kotlinx.serialization.Serializable

@Serializable
data class CategoryRequest(val name: String)

@Serializable
data class CategoryResponse(val id: Int, val name: String)

@Serializable
data class IssueRequest(
    val title: String,
    val description: String,
    val neighborhood: String,
    val categoryId: Int,
)

@Serializable
data class IssueResponse(
    val id: Int,
    val title: String,
    val description: String,
    val neighborhood: String,
    val categoryId: Int,
)

@OptIn(ExperimentalKtorApi::class)
fun Application.configureRoutes(service: ReportCityService) {
    routing {
        get("/health") { call.respondText("OK") }.describe {
            summary = "Checks if the API is running"
            responses { HttpStatusCode.OK { schema = jsonSchema<String>() } }
        }

        route("/categories") {
            get { call.respond(service.listCategories().map(Category::toResponse)) }.describe {
                summary = "Lists categories"
                responses { HttpStatusCode.OK { schema = jsonSchema<List<CategoryResponse>>() } }
            }

            get("/{id}") { call.respond(service.findCategory(call.pathId()).toResponse()) }.describe {
                summary = "Finds a category by id"
                parameters { path("id") { schema = jsonSchema<Int>() } }
                responses {
                    HttpStatusCode.OK { schema = jsonSchema<CategoryResponse>() }
                    HttpStatusCode.BadRequest { schema = jsonSchema<Problem>() }
                    HttpStatusCode.NotFound { schema = jsonSchema<Problem>() }
                    HttpStatusCode.UnprocessableEntity { schema = jsonSchema<Problem>() }
                }
            }

            post {
                val created = service.createCategory(call.receive<CategoryRequest>().toData())
                call.response.header(HttpHeaders.Location, "/categories/${created.id}")
                call.respond(HttpStatusCode.Created, created.toResponse())
            }.describe {
                summary = "Creates a category"
                requestBody { schema = jsonSchema<CategoryRequest>() }
                responses {
                    HttpStatusCode.Created { schema = jsonSchema<CategoryResponse>() }
                    HttpStatusCode.BadRequest { schema = jsonSchema<Problem>() }
                    HttpStatusCode.UnprocessableEntity { schema = jsonSchema<Problem>() }
                }
            }

            put("/{id}") {
                call.respond(service.updateCategory(call.pathId(), call.receive<CategoryRequest>().toData()).toResponse())
            }.describe {
                summary = "Updates a category"
                parameters { path("id") { schema = jsonSchema<Int>() } }
                requestBody { schema = jsonSchema<CategoryRequest>() }
                responses {
                    HttpStatusCode.OK { schema = jsonSchema<CategoryResponse>() }
                    HttpStatusCode.BadRequest { schema = jsonSchema<Problem>() }
                    HttpStatusCode.NotFound { schema = jsonSchema<Problem>() }
                    HttpStatusCode.UnprocessableEntity { schema = jsonSchema<Problem>() }
                }
            }

            delete("/{id}") {
                service.deleteCategory(call.pathId())
                call.respond(HttpStatusCode.NoContent)
            }.describe {
                summary = "Deletes a category"
                parameters { path("id") { schema = jsonSchema<Int>() } }
                responses {
                    HttpStatusCode.NoContent { }
                    HttpStatusCode.BadRequest { schema = jsonSchema<Problem>() }
                    HttpStatusCode.NotFound { schema = jsonSchema<Problem>() }
                    HttpStatusCode.Conflict { schema = jsonSchema<Problem>() }
                    HttpStatusCode.UnprocessableEntity { schema = jsonSchema<Problem>() }
                }
            }
        }

        route("/issues") {
            get {
                val page = call.queryInt("page", 0)
                val size = call.queryInt("size", 10)
                val categoryId = call.queryOptionalInt("categoryId")
                call.respond(service.listIssues(page, size, categoryId).map(Issue::toResponse))
            }.describe {
                summary = "Lists issues with pagination and category filter"
                parameters {
                    query("page") { schema = jsonSchema<Int>() }
                    query("size") { schema = jsonSchema<Int>() }
                    query("categoryId") { schema = jsonSchema<Int>() }
                }
                responses {
                    HttpStatusCode.OK { schema = jsonSchema<List<IssueResponse>>() }
                    HttpStatusCode.BadRequest { schema = jsonSchema<Problem>() }
                    HttpStatusCode.UnprocessableEntity { schema = jsonSchema<Problem>() }
                }
            }

            get("/{id}") { call.respond(service.findIssue(call.pathId()).toResponse()) }.describe {
                summary = "Finds an issue by id"
                parameters { path("id") { schema = jsonSchema<Int>() } }
                responses {
                    HttpStatusCode.OK { schema = jsonSchema<IssueResponse>() }
                    HttpStatusCode.BadRequest { schema = jsonSchema<Problem>() }
                    HttpStatusCode.NotFound { schema = jsonSchema<Problem>() }
                    HttpStatusCode.UnprocessableEntity { schema = jsonSchema<Problem>() }
                }
            }

            post {
                val created = service.createIssue(call.receive<IssueRequest>().toData())
                call.response.header(HttpHeaders.Location, "/issues/${created.id}")
                call.respond(HttpStatusCode.Created, created.toResponse())
            }.describe {
                summary = "Creates an issue"
                requestBody { schema = jsonSchema<IssueRequest>() }
                responses {
                    HttpStatusCode.Created { schema = jsonSchema<IssueResponse>() }
                    HttpStatusCode.BadRequest { schema = jsonSchema<Problem>() }
                    HttpStatusCode.UnprocessableEntity { schema = jsonSchema<Problem>() }
                }
            }

            put("/{id}") {
                call.respond(service.updateIssue(call.pathId(), call.receive<IssueRequest>().toData()).toResponse())
            }.describe {
                summary = "Updates an issue"
                parameters { path("id") { schema = jsonSchema<Int>() } }
                requestBody { schema = jsonSchema<IssueRequest>() }
                responses {
                    HttpStatusCode.OK { schema = jsonSchema<IssueResponse>() }
                    HttpStatusCode.BadRequest { schema = jsonSchema<Problem>() }
                    HttpStatusCode.NotFound { schema = jsonSchema<Problem>() }
                    HttpStatusCode.UnprocessableEntity { schema = jsonSchema<Problem>() }
                }
            }

            delete("/{id}") {
                service.deleteIssue(call.pathId())
                call.respond(HttpStatusCode.NoContent)
            }.describe {
                summary = "Deletes an issue"
                parameters { path("id") { schema = jsonSchema<Int>() } }
                responses {
                    HttpStatusCode.NoContent { }
                    HttpStatusCode.BadRequest { schema = jsonSchema<Problem>() }
                    HttpStatusCode.NotFound { schema = jsonSchema<Problem>() }
                }
            }
        }

        swaggerUI(path = "docs") {
            info = OpenApiInfo(title = "ReportCity API", version = "1.0.0")
        }
    }
}

private fun ApplicationCall.pathId(): Int =
    parameters["id"]?.toIntOrNull() ?: throw BadRequestException("id must be an integer")

private fun ApplicationCall.queryInt(name: String, default: Int): Int {
    val value = request.queryParameters[name] ?: return default
    return value.toIntOrNull() ?: throw BadRequestException("$name must be an integer")
}

private fun ApplicationCall.queryOptionalInt(name: String): Int? {
    val value = request.queryParameters[name] ?: return null
    return value.toIntOrNull() ?: throw BadRequestException("$name must be an integer")
}

private fun CategoryRequest.toData() = CategoryData(name)
private fun Category.toResponse() = CategoryResponse(id, name)
private fun IssueRequest.toData() = IssueData(title, description, neighborhood, categoryId)
private fun Issue.toResponse() = IssueResponse(id, title, description, neighborhood, categoryId)
