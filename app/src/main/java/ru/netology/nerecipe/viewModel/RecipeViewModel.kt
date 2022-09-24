package ru.netology.nerecipe.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
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
        dao = AppDb.getInstanse(application).recipeDao
    )
    val data by repository::data
    var orderSort = (data.value?.map
    {
        it.id
    } ?: mutableListOf())
    val sortedData = MutableLiveData(sortLiveData(data.value))

    private fun sortLiveData(recipes: List<Recipe>?): List<Recipe> {
        val result = mutableListOf<Recipe>()
        if (orderSort.isNotEmpty()) {
            orderSort.forEach {
                if (recipes != null) {
                    result.add((recipes[it]))
                }
            }
        } else {
            return result
        }
        return result
    }

    val navigateToNewRecipeFragment = SingleLiveEvent<String?>()
    val navigateToSingleRecipeFragment = SingleLiveEvent<Int>()

    private val currentRecipe = MutableLiveData<Recipe?>(null)

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

    fun moveTo(item: Int, itemTarget: Int) {
        Log.d("MoveTo", "перенос $item на позицию $itemTarget")
        orderSort = orderSort.mapNotNull {
            when (it) {
                itemTarget -> {
                    item
                }
                item -> {
                    null
                }
                else -> {
                    it
                }
            }
        }
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
}