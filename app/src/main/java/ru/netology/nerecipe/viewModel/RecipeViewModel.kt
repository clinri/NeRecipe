package ru.netology.nerecipe.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nerecipe.adapter.RecipesInteractionListener
import ru.netology.nerecipe.data.RecipesRepository
import ru.netology.nerecipe.data.impl.RecipesRepositoryImpl
import ru.netology.nerecipe.db.AppDb
import ru.netology.nerecipe.dto.Recipe
import ru.netology.nerecipe.util.ItemNotFoundExceptions
import ru.netology.nerecipe.util.SingleLiveEvent

open class RecipeViewModel(
    application: Application
) : AndroidViewModel(application), RecipesInteractionListener {
    private val repository: RecipesRepository = RecipesRepositoryImpl(
        dao = AppDb.getInstance(application).recipeDao
    )
    val data by repository::data
    private var orderSort: List<Int> = emptyList()
    var sortedData: List<Recipe> = emptyList()

    val navigateToNewRecipeFragment = SingleLiveEvent<String?>()
    val navigateToSingleRecipeFragment = SingleLiveEvent<Int>()

    private val currentRecipe = MutableLiveData<Recipe?>(null)

    fun updateSortData() {
        if (orderSort.isEmpty()) {
            orderSort = data.value?.map {
                it.id
            } ?: emptyList()
        }
        sortedData = data.value?.let { sortByOrder(it, orderSort) }!!
    }

    private fun sortByOrder(listRecipes: List<Recipe>, order: List<Int>): List<Recipe> =
        listRecipes.map { order.indexOf(it.id) }.sorted().map { listRecipes[it] }

    fun onSaveButtonClicked(title: String) {
        if (title.isBlank()) {
            return
        }
        currentRecipe.value?.also {
            repository.updateContentById(
                it.copy(title = title)
            )
        } ?: addRecipe(title)
        currentRecipe.value = null
    }

    private fun addRecipe(title: String) {
        repository.insert(
            Recipe( // new
                id = RecipesRepository.NEW_RECIPE_ID,
                category = "Russian",
                author = "Me",
                title = title
            )
        )
        orderSort = mutableListOf(
            checkNotNull(
                data.value?.last()?.id
            )
        ) + orderSort
    }

//region RecipeInteractionListener

    override fun onFavoriteClicked(recipe: Recipe) =
        repository.favorite(recipe.id)

    override fun onRecipeClicked(recipe: Recipe) {
        navigateToSingleRecipeFragment.value = recipe.id
    }

    override fun onRemoveClicked(recipe: Recipe) =
        repository.delete(recipe.id)

    override fun onAddClicked() {
        navigateToNewRecipeFragment.value = null
    }

    override fun onEditClicked(recipe: Recipe) {
        navigateToNewRecipeFragment.value = recipe.title
        currentRecipe.value = recipe
    }

    override fun onCancelEditClicked() {
        currentRecipe.value = null
    }
//endregion InteractionListener

    fun getRecipeById(recipeId: Int): Recipe {
        return data.value?.find { it.id == recipeId } ?: throw ItemNotFoundExceptions()
    }

    fun moveTo(item: Int, itemTarget: Int) {
        Log.d("MoveTo", "перенос $item на позицию $itemTarget")
        val indexItem = orderSort.indexOf(item)
        val indexItemTarget = orderSort.indexOf(itemTarget)
        val newList = mutableListOf<Int>()
        newList.addAll(orderSort)
        newList.add(indexItemTarget, item)
        newList.remove(indexItem + 1)
    }
}