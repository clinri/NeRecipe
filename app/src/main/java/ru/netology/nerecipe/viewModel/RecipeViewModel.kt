package ru.netology.nerecipe.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nerecipe.adapter.RecipesInteractionListener
import ru.netology.nerecipe.data.OrderRepository
import ru.netology.nerecipe.data.RecipesRepository
import ru.netology.nerecipe.data.impl.InMemoryManualOrderRepository
import ru.netology.nerecipe.data.impl.RecipesRepositoryImpl
import ru.netology.nerecipe.db.AppDb
import ru.netology.nerecipe.dto.Order
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
    var sortedData: List<Recipe> = mutableListOf()
    private var orderRepository: OrderRepository = InMemoryManualOrderRepository(data.value!!)
    private var order: List<Order> = orderRepository.order

    val navigateToNewRecipeFragment = SingleLiveEvent<String?>()
    val navigateToSingleRecipeFragment = SingleLiveEvent<Int>()

    private val currentRecipe = MutableLiveData<Recipe?>(null)

    init {
        data.observeForever {
            updateSortData()
        }
    }

    private fun updateSortData() {
        println(data.value)
        println(order)
        sortedData = data.value?.let { sortByOrder(it) }!!
        println(sortedData)
    }

    // сортировка списка рецептов по id из списка сортировки
    private fun sortByOrder(listRecipes: List<Recipe>): List<Recipe> {
        orderRepository.sort()
        return order.filter { itOrder ->
            listRecipes.any { itRecipe ->
                itOrder.id == itRecipe.id
            }
        }.map {
            it.id
        }.map { itId ->
            lateinit var recipe: Recipe
            listRecipes.forEach { itRecipe ->
                if (itId == itRecipe.id) recipe = itRecipe
            }
            recipe
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
        currentRecipe.value = null
    }

    private fun addRecipe(title: String) {
        val id = repository.insert(
            Recipe( // new
                id = RecipesRepository.NEW_RECIPE_ID,
                category = "Russian",
                author = "Me",
                title = title
            )
        )
        orderRepository.insert(id)
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
        orderRepository.delete(recipe.id)
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
        val (id1, order1) = order[itemTarget]
        val (id2, order2) = order[itemTarget]
        orderRepository.swapOrdersByIds(id1, order1, id2, order2)
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