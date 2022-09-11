package ru.netology.nmedia.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nerecipe.adapter.RecipesInteractionListener
import ru.netology.nerecipe.dto.Recipe
import ru.netology.nmedia.data.RecipesRepository
import ru.netology.nmedia.data.impl.RecipesRepositoryImpl
import ru.netology.nerecipe.db.AppDb
import ru.netology.nmedia.util.ItemNotFoundExceptions
import ru.netology.nmedia.util.SingleLiveEvent

open class RecipeViewModel(
    application: Application
) : AndroidViewModel(application), RecipesInteractionListener {
    private val repository: RecipesRepository = RecipesRepositoryImpl(
        dao = AppDb.getInstanse(application).recipeDao
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
        } ?: repository.insert(
            Recipe( // new
                id = RecipesRepository.NEW_RECIPE_ID,
                category = "Russian",
                author = "Me",
                title = title
            )
        )
        currentRecipe.value = null
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
}