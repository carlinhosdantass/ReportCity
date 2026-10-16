package br.ufrn.reportcity.adapters.database

import br.ufrn.reportcity.application.ResourceConflict
import br.ufrn.reportcity.domain.Category
import br.ufrn.reportcity.domain.CategoryData
import br.ufrn.reportcity.domain.CategoryRepository
import br.ufrn.reportcity.domain.Issue
import br.ufrn.reportcity.domain.IssueData
import br.ufrn.reportcity.domain.IssueRepository
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.update
import org.postgresql.util.PSQLException
import javax.sql.DataSource

data class DatabaseConfig(val url: String, val user: String, val password: String) {
    companion object {
        fun fromEnvironment() = DatabaseConfig(
            url = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/reportcity",
            user = System.getenv("DB_USER") ?: "reportcity",
            password = System.getenv("DB_PASSWORD") ?: "reportcity",
        )
    }
}

fun createDataSource(config: DatabaseConfig): HikariDataSource = HikariDataSource(
    HikariConfig().apply {
        jdbcUrl = config.url
        username = config.user
        password = config.password
        maximumPoolSize = 5
    },
)

fun migrate(dataSource: DataSource) {
    Flyway.configure().dataSource(dataSource).load().migrate()
}

object Categories : Table("categories") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)
    override val primaryKey = PrimaryKey(id)
}

object Issues : Table("issues") {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 200)
    val description = text("description")
    val neighborhood = varchar("neighborhood", 120)
    val categoryId = reference("category_id", Categories.id, onDelete = ReferenceOption.RESTRICT)
    override val primaryKey = PrimaryKey(id)
}

class PostgresCategoryRepository(private val database: Database) : CategoryRepository {
    override suspend fun list(): List<Category> = suspendTransaction(database) {
        Categories.selectAll().orderBy(Categories.id to SortOrder.ASC).map { it.toCategory() }
    }

    override suspend fun find(id: Int): Category? = suspendTransaction(database) {
        Categories.selectAll().where { Categories.id eq id }.singleOrNull()?.toCategory()
    }

    override suspend fun create(data: CategoryData): Category = suspendTransaction(database) {
        val id = Categories.insert { it[name] = data.name } get Categories.id
        Category(id, data.name)
    }

    override suspend fun update(id: Int, data: CategoryData): Category? = suspendTransaction(database) {
        val changed = Categories.update({ Categories.id eq id }) { it[name] = data.name }
        if (changed == 0) null else Category(id, data.name)
    }

    override suspend fun delete(id: Int): Boolean = try {
        suspendTransaction(database) { Categories.deleteWhere { Categories.id eq id } > 0 }
    } catch (error: PSQLException) {
        if (error.sqlState == "23503") throw ResourceConflict("Category $id has issues and cannot be deleted")
        throw error
    }

    private fun ResultRow.toCategory() = Category(this[Categories.id], this[Categories.name])
}

class PostgresIssueRepository(private val database: Database) : IssueRepository {
    override suspend fun list(page: Int, size: Int, categoryId: Int?): List<Issue> = suspendTransaction(database) {
        val query = Issues.selectAll()
        if (categoryId != null) query.andWhere { Issues.categoryId eq categoryId }
        query.orderBy(Issues.id to SortOrder.ASC)
            .limit(size).offset(page.toLong() * size)
            .map { it.toIssue() }
    }

    override suspend fun find(id: Int): Issue? = suspendTransaction(database) {
        Issues.selectAll().where { Issues.id eq id }.singleOrNull()?.toIssue()
    }

    override suspend fun create(data: IssueData): Issue = suspendTransaction(database) {
        val id = Issues.insert {
            it[title] = data.title
            it[description] = data.description
            it[neighborhood] = data.neighborhood
            it[categoryId] = data.categoryId
        } get Issues.id
        data.toIssue(id)
    }

    override suspend fun update(id: Int, data: IssueData): Issue? = suspendTransaction(database) {
        val changed = Issues.update({ Issues.id eq id }) {
            it[title] = data.title
            it[description] = data.description
            it[neighborhood] = data.neighborhood
            it[categoryId] = data.categoryId
        }
        if (changed == 0) null else data.toIssue(id)
    }

    override suspend fun delete(id: Int): Boolean = suspendTransaction(database) {
        Issues.deleteWhere { Issues.id eq id } > 0
    }

    override suspend fun existsByCategory(categoryId: Int): Boolean = suspendTransaction(database) {
        Issues.selectAll().where { Issues.categoryId eq categoryId }.limit(1).any()
    }

    private fun ResultRow.toIssue() = Issue(
        id = this[Issues.id],
        title = this[Issues.title],
        description = this[Issues.description],
        neighborhood = this[Issues.neighborhood],
        categoryId = this[Issues.categoryId],
    )

    private fun IssueData.toIssue(id: Int) = Issue(id, title, description, neighborhood, categoryId)
}
