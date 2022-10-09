package ru.netology.nerecipe.viewModel

import android.app.Application
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
                title = title,
                orderManual = 0
            )
        )
    }

//region RecipeInteractionListener

    override fun onFavoriteClicked(recipe: Recipe) {
        repository.favorite(recipe.id)
    }

    override fun onRecipeClicked(recipe: Recipe) {
        navigateToSingleRecipeFragment.value = recipe.id
    }

    override fun onRemoveClicked(recipe: Recipe) {
        repository.delete(recipe.id)
    }

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
        val foundedRecipe = data.value?.find { it.id == recipeId }
        return foundedRecipe ?: throw ItemNotFoundExceptions()
    }

    fun moveTo(item: Int, itemTarget: Int) {
        val sortedList = data.value!!.sortedBy {
            it.orderManual
        }
        val (id1,order1) = sortedList[item]
        val (id2,order2) = sortedList[itemTarget]
        repository.swapOrdersByIds(id1,order1,id2,order2)
    }

    /**
     * Moves the given item at the `oldIndex` to the `newIndex`
     */
    private fun <T> MutableList<T>.moveAt(oldIndex: Int, newIndex: Int) {
        val item = this[oldIndex]
        this.removeAt(oldIndex)
        if (oldIndex > newIndex)
            add(newIndex, item)
        else
            add(newIndex - 1, item)
    }

    fun onFavoriteTabClicked() {
        repository.changeDataByFilter(FAVORITE)
    }

    fun onAllTabClicked() {
        repository.changeDataByFilter(ALL)
    }

    companion object {
        const val ALL = 0
        const val FAVORITE = 1
    }
}