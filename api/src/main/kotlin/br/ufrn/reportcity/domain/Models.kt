package br.ufrn.reportcity.domain

data class Category(val id: Int, val name: String)

data class Issue(
    val id: Int,
    val title: String,
    val description: String,
    val neighborhood: String,
    val categoryId: Int,
)

data class CategoryData(val name: String)

data class IssueData(
    val title: String,
    val description: String,
    val neighborhood: String,
    val categoryId: Int,
)

interface CategoryRepository {
    suspend fun list(): List<Category>
    suspend fun find(id: Int): Category?
    suspend fun create(data: CategoryData): Category
    suspend fun update(id: Int, data: CategoryData): Category?
    suspend fun delete(id: Int): Boolean
}

interface IssueRepository {
    suspend fun list(page: Int, size: Int, categoryId: Int?): List<Issue>
    suspend fun find(id: Int): Issue?
    suspend fun create(data: IssueData): Issue
    suspend fun update(id: Int, data: IssueData): Issue?
    suspend fun delete(id: Int): Boolean
    suspend fun existsByCategory(categoryId: Int): Boolean
}
