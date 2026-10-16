package br.ufrn.reportcity.application

import br.ufrn.reportcity.domain.Category
import br.ufrn.reportcity.domain.CategoryData
import br.ufrn.reportcity.domain.CategoryRepository
import br.ufrn.reportcity.domain.Issue
import br.ufrn.reportcity.domain.IssueData
import br.ufrn.reportcity.domain.IssueRepository

class InvalidInput(val violations: List<String>) : RuntimeException(violations.joinToString("; "))
class ResourceNotFound(message: String) : RuntimeException(message)
class ResourceConflict(message: String) : RuntimeException(message)

class ReportCityService(
    private val categories: CategoryRepository,
    private val issues: IssueRepository,
) {
    suspend fun listCategories(): List<Category> = categories.list()

    suspend fun findCategory(id: Int): Category =
        categories.find(validId(id)) ?: throw ResourceNotFound("Category $id does not exist")

    suspend fun createCategory(data: CategoryData): Category {
        validate(data)
        return categories.create(data.copy(name = data.name.trim()))
    }

    suspend fun updateCategory(id: Int, data: CategoryData): Category {
        validate(data)
        return categories.update(validId(id), data.copy(name = data.name.trim()))
            ?: throw ResourceNotFound("Category $id does not exist")
    }

    suspend fun deleteCategory(id: Int) {
        val validId = validId(id)
        if (categories.find(validId) == null) throw ResourceNotFound("Category $id does not exist")
        if (issues.existsByCategory(validId)) {
            throw ResourceConflict("Category $id has issues and cannot be deleted")
        }
        categories.delete(validId)
    }

    suspend fun listIssues(page: Int, size: Int, categoryId: Int?): List<Issue> {
        if (page < 0) throw InvalidInput(listOf("page must be zero or greater"))
        if (size !in 1..100) throw InvalidInput(listOf("size must be between 1 and 100"))
        categoryId?.let { validId(it) }
        return issues.list(page, size, categoryId)
    }

    suspend fun findIssue(id: Int): Issue =
        issues.find(validId(id)) ?: throw ResourceNotFound("Issue $id does not exist")

    suspend fun createIssue(data: IssueData): Issue {
        validate(data)
        requireCategory(data.categoryId)
        return issues.create(data.trimmed())
    }

    suspend fun updateIssue(id: Int, data: IssueData): Issue {
        validate(data)
        requireCategory(data.categoryId)
        return issues.update(validId(id), data.trimmed())
            ?: throw ResourceNotFound("Issue $id does not exist")
    }

    suspend fun deleteIssue(id: Int) {
        if (!issues.delete(validId(id))) throw ResourceNotFound("Issue $id does not exist")
    }

    private suspend fun requireCategory(id: Int) {
        validId(id)
        if (categories.find(id) == null) throw InvalidInput(listOf("categoryId does not reference a category"))
    }

    private fun validate(data: CategoryData) {
        val violations = buildList {
            if (data.name.isBlank()) add("name must not be blank")
            if (data.name.length > 100) add("name must have at most 100 characters")
        }
        if (violations.isNotEmpty()) throw InvalidInput(violations)
    }

    private fun validate(data: IssueData) {
        val violations = buildList {
            if (data.title.isBlank()) add("title must not be blank")
            if (data.title.length > 200) add("title must have at most 200 characters")
            if (data.description.isBlank()) add("description must not be blank")
            if (data.neighborhood.isBlank()) add("neighborhood must not be blank")
            if (data.neighborhood.length > 120) add("neighborhood must have at most 120 characters")
            if (data.categoryId <= 0) add("categoryId must be positive")
        }
        if (violations.isNotEmpty()) throw InvalidInput(violations)
    }

    private fun validId(id: Int): Int {
        if (id <= 0) throw InvalidInput(listOf("id must be positive"))
        return id
    }

    private fun IssueData.trimmed() = copy(
        title = title.trim(),
        description = description.trim(),
        neighborhood = neighborhood.trim(),
    )
}
