package ru.netology.nerecipe.data

import androidx.lifecycle.LiveData
import ru.netology.nerecipe.dto.FilterName
import ru.netology.nerecipe.dto.Recipe

interface RecipesRepository {
    val data: LiveData<List<Recipe>>
    fun changeDataByFilter(textForSearch: String, filter: FilterName)
    fun favorite(recipeId: Int)
    fun delete(recipeId: Int)
    fun insert(recipe: Recipe)
    fun updateContentById(recipe: Recipe)
    fun swapOrdersByIds(id1: Int, order1: Int, id2: Int, order2: Int)

    companion object {
        const val NEW_RECIPE_ID = 0
        const val NEW_ORDER = 0
    }
}