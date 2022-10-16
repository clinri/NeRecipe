package ru.netology.nerecipe.data.impl

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nerecipe.data.RecipesRepository
import ru.netology.nerecipe.db.RecipeDao
import ru.netology.nerecipe.db.RecipeEntity
import ru.netology.nerecipe.db.toEntity
import ru.netology.nerecipe.db.toModel
import ru.netology.nerecipe.dto.FilterName
import ru.netology.nerecipe.dto.Recipe

class RecipesRepositoryImpl(
    private val dao: RecipeDao,
) : RecipesRepository {
    private var text: String = "%"
    private var modeData = FilterName.ALL
    override var data = when (modeData) {
        FilterName.ALL -> getLiveData(dao.getAll(text))
        FilterName.FAVORITE -> getLiveData(dao.getFavorite(text))
    }

    override fun changeDataByFilter(textForSearch: String, filter: FilterName) {
        val text = textForSearch.ifBlank { "%" }
        println(text)
        when (filter) {
            FilterName.ALL -> {
                modeData = FilterName.ALL
                data = getLiveData(dao.getAll(text))
            }
            FilterName.FAVORITE -> {
                modeData = FilterName.FAVORITE
                data = getLiveData(dao.getFavorite(text))
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

    override fun updateContentById(recipe: Recipe) {
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