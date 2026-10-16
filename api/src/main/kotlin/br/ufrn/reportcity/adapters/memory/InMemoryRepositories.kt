package br.ufrn.reportcity.adapters.memory

import br.ufrn.reportcity.domain.Category
import br.ufrn.reportcity.domain.CategoryData
import br.ufrn.reportcity.domain.CategoryRepository
import br.ufrn.reportcity.domain.Issue
import br.ufrn.reportcity.domain.IssueData
import br.ufrn.reportcity.domain.IssueRepository

class InMemoryCategoryRepository : CategoryRepository {
    private val items = mutableListOf<Category>()
    private var nextId = 1

    override suspend fun list() = items.toList()
    override suspend fun find(id: Int) = items.find { it.id == id }

    override suspend fun create(data: CategoryData) =
        Category(nextId++, data.name).also { items += it }

    override suspend fun update(id: Int, data: CategoryData): Category? {
        val index = items.indexOfFirst { it.id == id }
        if (index == -1) return null
        return Category(id, data.name).also { items[index] = it }
    }

    override suspend fun delete(id: Int) = items.removeIf { it.id == id }
}

class InMemoryIssueRepository : IssueRepository {
    private val items = mutableListOf<Issue>()
    private var nextId = 1

    override suspend fun list(page: Int, size: Int, categoryId: Int?): List<Issue> = items
        .asSequence()
        .filter { categoryId == null || it.categoryId == categoryId }
        .drop(page * size)
        .take(size)
        .toList()

    override suspend fun find(id: Int) = items.find { it.id == id }

    override suspend fun create(data: IssueData) =
        Issue(nextId++, data.title, data.description, data.neighborhood, data.categoryId).also { items += it }

    override suspend fun update(id: Int, data: IssueData): Issue? {
        val index = items.indexOfFirst { it.id == id }
        if (index == -1) return null
        return Issue(id, data.title, data.description, data.neighborhood, data.categoryId)
            .also { items[index] = it }
    }

    override suspend fun delete(id: Int) = items.removeIf { it.id == id }
    override suspend fun existsByCategory(categoryId: Int) = items.any { it.categoryId == categoryId }
}
