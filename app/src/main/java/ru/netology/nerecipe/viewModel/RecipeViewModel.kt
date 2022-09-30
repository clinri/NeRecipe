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
    private var orderSort: MutableList<Int>
    private var savedOrderSortData: MutableList<Int> = mutableListOf()
    private var savedOrderSortFavoriteData: MutableList<Int> = mutableListOf()
    private var modeFilter = ALL
    var sortedData: List<Recipe> = mutableListOf()
    private var lastSizeData = 0

    val navigateToNewRecipeFragment = SingleLiveEvent<String?>()
    val navigateToSingleRecipeFragment = SingleLiveEvent<Int>()

    private val currentRecipe = MutableLiveData<Recipe?>(null)

    init {
        lastSizeData = data.value?.size ?: 0
        //заполняем очередь сортировки
        orderSort = data.value?.map {
            it.id
        }?.toMutableList() ?: mutableListOf()
        //заполняем очередь сортирвоки для favorite
        savedOrderSortFavoriteData = data.value?.mapNotNull {
            if (it.favorite)
                it.id
            else
                null
        }?.toMutableList() ?: mutableListOf()
    }

    fun updateSortData() {
        //этот код аналогичен тому что в init, но init как буд-то не исполянется
        if (data.value?.isEmpty() == false) {
            if (orderSort.isEmpty()) {
                orderSort = data.value?.map {
                    it.id
                }?.toMutableList() ?: mutableListOf()
            }
            savedOrderSortFavoriteData = data.value?.mapNotNull {
                if (it.favorite)
                    it.id
                else
                    null
            }?.toMutableList() ?: mutableListOf()
        }
        println(data.value)
        println()
        println(orderSort)
        println()
        sortedData = data.value?.let { sortByOrder(it, orderSort) }!!
    }

    // сортировка списка рецептов по id из списка сортировки
    private fun sortByOrder(listRecipes: List<Recipe>, order: List<Int>): List<Recipe> {
        return listRecipes.map { recipe ->
            order.indexOf(recipe.id)
        }.sorted().map { id ->
            listRecipes[id]
        }
    }

    fun onSaveButtonClicked(title: String) {
        if (title.isBlank()) {
            return
        }
        currentRecipe.value?.also {
            repository.updateContentById(
                it.copy(title = title)
            )
        } ?: addRecipe(title)
        tryAddNewIdInSortOrder()
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
    }

    private fun tryAddNewIdInSortOrder() {
        if (data.value?.size == lastSizeData + 1) {
            val recipe = data.value!!.first()
            orderSort.add(0, recipe.id)
            when (modeFilter) {
                ALL -> if (recipe.favorite) savedOrderSortFavoriteData.add(0, recipe.id)
                FAVORITE -> savedOrderSortData.add(0, recipe.id)
            }
            lastSizeData++
        }
    }

//region RecipeInteractionListener

    override fun onFavoriteClicked(recipe: Recipe) {
        when (modeFilter) {
            // находясь на вкладке ALL, удаляем или добавляем в сортировку вкладки FAVORITE
            ALL -> {
                if (recipe.favorite)
                    savedOrderSortFavoriteData.remove(recipe.id)
                else
                    savedOrderSortFavoriteData.add(0, recipe.id)
            }
            // находясь на вкладке FAVORITE, удаляем если сняли звездочку
            FAVORITE -> {
                if (recipe.favorite)
                    orderSort.remove(recipe.id)
            }
        }
        repository.favorite(recipe.id)
    }

    override fun onRecipeClicked(recipe: Recipe) {
        navigateToSingleRecipeFragment.value = recipe.id
    }

    override fun onRemoveClicked(recipe: Recipe) {
        orderSort.remove(recipe.id)
        when (modeFilter) {
            ALL -> if (recipe.favorite) savedOrderSortFavoriteData.remove(recipe.id)
            FAVORITE -> savedOrderSortData.remove(recipe.id)
        }
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
        return data.value?.find { it.id == recipeId } ?: throw ItemNotFoundExceptions()
    }

    fun moveTo(item: Int, itemTarget: Int) {
        val newList = mutableListOf<Int>()
        newList.addAll(orderSort)
        newList.add(itemTarget, orderSort[item])
        newList.remove(orderSort[item])
        orderSort = newList
    }

    fun onFavoriteTabClicked() {
        savedOrderSortData = orderSort
        orderSort = savedOrderSortFavoriteData
        repository.changeDataByFilter(FAVORITE)
        modeFilter = FAVORITE
    }

    fun onAllTabClicked() {
        savedOrderSortFavoriteData = orderSort
        orderSort = savedOrderSortData
        repository.changeDataByFilter(ALL)
        modeFilter = ALL
    }

    companion object {
        const val ALL = 0
        const val FAVORITE = 1
    }
}