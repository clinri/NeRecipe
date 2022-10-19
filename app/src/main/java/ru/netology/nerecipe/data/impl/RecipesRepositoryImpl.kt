package ru.netology.nerecipe.data.impl

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import ru.netology.nerecipe.data.RecipesRepository
import ru.netology.nerecipe.db.RecipeDao
import ru.netology.nerecipe.db.RecipeEntity
import ru.netology.nerecipe.db.toEntity
import ru.netology.nerecipe.db.toModel
import ru.netology.nerecipe.dto.KitchenCategory
import ru.netology.nerecipe.dto.TabName
import ru.netology.nerecipe.dto.Recipe

class RecipesRepositoryImpl(
    private val dao: RecipeDao,
) : RecipesRepository {
    private var textForSearch: String = "%"
    private var modeData = TabName.ALL
    private var filterCategory = KitchenCategory.values().asList()
    override var data = when (modeData) {
        TabName.ALL -> getLiveData(dao.getAll(textForSearch, filterCategory))
        TabName.FAVORITE -> getLiveData(dao.getFavorite(textForSearch, filterCategory))
    }

    override fun changeDataByParams(
        textForSearch: String,
        tab: TabName,
        filterCategory: List<KitchenCategory>,
    ) {
        val text = textForSearch.ifBlank { "%" }
        when (tab) {
            TabName.ALL -> {
                modeData = TabName.ALL
                this.filterCategory = filterCategory
                data = getLiveData(dao.getAll(text, filterCategory))
            }
            TabName.FAVORITE -> {
                modeData = TabName.FAVORITE
                this.filterCategory = filterCategory
                data = getLiveData(dao.getFavorite(text, filterCategory))
            }
        }
    }

    private fun getLiveData(dataInput: LiveData<List<RecipeEntity>>): LiveData<List<Recipe>> =
        dataInput.map { entities ->
            entities.map { it.toModel() }
        }

    override fun insert(recipe: Recipe) {
        dao.insert(recipe.toEntity())
    }

    override fun updateRecipe(recipe: Recipe) {
        Log.d("recipe", recipe.toString())
        dao.updateContentById(recipe.id, recipe.kitchenCategory, recipe.author, recipe.title)
    }

    override fun swapOrdersByIds(id1: Int, order1: Int, id2: Int, order2: Int) {
        dao.swapOrdersByIds(id1, order1, id2, order2)
    }

    override fun favorite(recipeId: Int) {
        dao.favoriteById(recipeId)
    }

    override fun delete(recipeId: Int) {
        dao.removeById(recipeId)
    }
}