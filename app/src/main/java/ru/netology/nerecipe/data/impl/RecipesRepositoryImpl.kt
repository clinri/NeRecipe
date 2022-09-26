package ru.netology.nerecipe.data.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nerecipe.data.RecipesRepository
import ru.netology.nerecipe.db.RecipeDao
import ru.netology.nerecipe.db.RecipeEntity
import ru.netology.nerecipe.db.toEntity
import ru.netology.nerecipe.db.toModel
import ru.netology.nerecipe.dto.Recipe

class RecipesRepositoryImpl(
    private val dao: RecipeDao,
    private val favoriteMode: Boolean
) : RecipesRepository {


    override val data = if (favoriteMode) {
        getLiveData(dao.getFavorite())
    } else {
        getLiveData(dao.getAll())
    }

    private fun getLiveData(dataInput: LiveData<List<RecipeEntity>>): LiveData<List<Recipe>> =
        dataInput.map { entities ->
            entities.map { it.toModel() }
        }

    override fun insert(recipe: Recipe) {
        dao.insert(recipe.toEntity())
    }

    override fun updateContentById(recipe: Recipe) {
        dao.updateContentById(recipe.id, recipe.title)
    }

    override fun favorite(recipeId: Int) {
        dao.favoriteById(recipeId)
    }

    override fun delete(recipeId: Int) {
        dao.removeById(recipeId)
    }
}