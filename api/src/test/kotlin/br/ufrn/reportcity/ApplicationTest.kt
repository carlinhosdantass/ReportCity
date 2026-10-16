package br.ufrn.reportcity

import br.ufrn.reportcity.adapters.memory.InMemoryCategoryRepository
import br.ufrn.reportcity.adapters.memory.InMemoryIssueRepository
import br.ufrn.reportcity.application.ReportCityService
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun `health endpoint returns OK`() = testApplication {
        application {
            configure(ReportCityService(InMemoryCategoryRepository(), InMemoryIssueRepository()))
        }

        val response = client.get("/health")

        assertEquals(HttpStatusCode.OK, response.status)
    }
}
