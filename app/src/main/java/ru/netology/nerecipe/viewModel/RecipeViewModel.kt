package ru.netology.nerecipe.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.netology.nerecipe.adapter.RecipesInteractionListener
import ru.netology.nerecipe.data.RecipesRepository
import ru.netology.nerecipe.data.impl.RecipesRepositoryImpl
import ru.netology.nerecipe.db.AppDb
import ru.netology.nerecipe.dto.TabName
import ru.netology.nerecipe.dto.KitchenCategory
import ru.netology.nerecipe.dto.Recipe
import ru.netology.nerecipe.util.ItemNotFoundExceptions
import ru.netology.nerecipe.util.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    application: Application,
) : AndroidViewModel(application), RecipesInteractionListener {
    private val repository: RecipesRepository = RecipesRepositoryImpl(
        dao = AppDb.getInstance(application).recipeDao
    )
    val data by repository::data
    val filter: MutableList<Boolean> =
        KitchenCategory.values().map {
            true
        } as MutableList<Boolean>


    val navigateToNewRecipeFragment = SingleLiveEvent<Int>()
    val navigateToSingleRecipeFragment = SingleLiveEvent<Int>()
    val activateFilterFragment = SingleLiveEvent<Unit>()
    val hideOptionMenu = SingleLiveEvent<Boolean>()
    val activateUpdateDataObserver = SingleLiveEvent<Unit>()

    private var textForSearch = ""

    fun optionMenuIsHidden(state: Boolean) {
        hideOptionMenu.value = state
    }

    fun onFilterButtonBarClicked() {
        activateFilterFragment.value = Unit
    }

    private val currentRecipe = MutableLiveData<Recipe?>(null)

    fun onSaveButtonClicked(recipe: Recipe) {
        Log.d("Saved recipe", recipe.toString())
        if (recipe.title.isBlank()) {
            return
        }
        currentRecipe.value?.also {
            repository.updateRecipe(
                it.copy(
                    title = recipe.title,
                    kitchenCategory = recipe.kitchenCategory,
                    author = recipe.author))
        } ?: addRecipe(recipe)
        currentRecipe.value = null
    }

    private fun addRecipe(recipe: Recipe) {
        repository.insert(
            Recipe( // new
                id = RecipesRepository.NEW_RECIPE_ID,
                kitchenCategory = recipe.kitchenCategory,
                author = recipe.author,
                title = recipe.title,
                orderManual = RecipesRepository.NEW_ORDER
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
        navigateToNewRecipeFragment.value = 0
    }

    override fun onEditClicked(recipe: Recipe) {
        navigateToNewRecipeFragment.value = recipe.id
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
        val (id1, order1) = sortedList[item]
        val (id2, order2) = sortedList[itemTarget]
        repository.swapOrdersByIds(id1, order1, id2, order2)
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
        repository.changeDataByParams(
            textForSearch,
            TabName.FAVORITE,
            getListFilterKitchenCategory()
        )
    }

    fun onAllTabClicked() {
        repository.changeDataByParams(
            textForSearch,
            TabName.ALL,
            getListFilterKitchenCategory())
    }

    fun updateDatabaseByParams(searchQuery: String, tab: TabName) {
        textForSearch = searchQuery
        repository.changeDataByParams(
            searchQuery,
            tab,
            getListFilterKitchenCategory()
        )
        activateUpdateDataObserver.value = Unit
    }

    private fun getListFilterKitchenCategory(): List<KitchenCategory> {
        val filterCategory = mutableListOf<KitchenCategory>()
        KitchenCategory.values().forEachIndexed { index, kitchenCategory ->
            if (filter[index]) filterCategory.add(kitchenCategory)
        }
        return filterCategory
    }

    fun onUpdateButtonClicked() {
        activateUpdateDataObserver.value = Unit
    }

    fun onItemFilterCategoryClicked(index: Int) {
        // action then click on filter item
        filter[index] = !filter[index]
    }
}