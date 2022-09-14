package ru.netology.nerecipe.data.impl

import androidx.lifecycle.map
import ru.netology.nerecipe.dto.Recipe
import ru.netology.nerecipe.data.RecipesRepository
import ru.netology.nerecipe.db.RecipeDao
import ru.netology.nerecipe.db.toEntity
import ru.netology.nerecipe.db.toModel

class RecipesRepositoryImpl(
    private val dao: RecipeDao
) : RecipesRepository {

    override val data = dao.getAll().map { entities ->
        entities.map { it.toModel() }
    }

    override fun insert(recipe: Recipe) {
        dao.insert(recipe.toEntity())
    }

    override fun updateContentById(recipe: Recipe) {
        dao.updateContentById(recipe.id,recipe.title)
    }

    override fun favorite(recipeId: Int) {
        dao.favoriteById(recipeId)
    }

    override fun delete(recipeId: Int) {
        dao.removeById(recipeId)
    }
}