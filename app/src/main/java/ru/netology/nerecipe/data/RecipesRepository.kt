package ru.netology.nerecipe.data

import androidx.lifecycle.LiveData
import ru.netology.nerecipe.dto.Recipe

interface RecipesRepository {
    val data: LiveData<List<Recipe>>
    fun favorite(recipeId: Int)
    fun delete(recipeId: Int)
    fun insert(recipe: Recipe)
    fun updateContentById(recipe: Recipe)

    companion object {
        const val NEW_RECIPE_ID = 0
    }
}