package ru.netology.nerecipe.data

import androidx.lifecycle.LiveData
import ru.netology.nerecipe.dto.KitchenCategory
import ru.netology.nerecipe.dto.TabName
import ru.netology.nerecipe.dto.Recipe

interface RecipesRepository {
    val data: LiveData<List<Recipe>>
    fun changeDataByParams(
        textForSearch: String,
        tab: TabName,
        filterCategory: List<KitchenCategory>,
    )
    fun favorite(recipeId: Int)
    fun delete(recipeId: Int)
    fun insert(recipe: Recipe)
    fun updateRecipe(recipe: Recipe)
    fun swapOrdersByIds(id1: Int, order1: Int, id2: Int, order2: Int)

    companion object {
        const val NEW_RECIPE_ID = 0
        const val NEW_ORDER = 0
    }
}