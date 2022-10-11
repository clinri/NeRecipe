package ru.netology.nerecipe.data.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nerecipe.data.RecipesRepository
import ru.netology.nerecipe.db.RecipeDao
import ru.netology.nerecipe.db.RecipeEntity
import ru.netology.nerecipe.db.toEntity
import ru.netology.nerecipe.db.toModel
import ru.netology.nerecipe.dto.Recipe

class RecipesRepositoryImpl(
    private val dao: RecipeDao
) : RecipesRepository {
    private var text: String = "%"
    private var modeData = 0
    override var data = when (modeData) {
        0 -> getLiveData(dao.getAll(text))
        1 -> getLiveData(dao.getFavorite(text))
        else -> getLiveData(dao.getAll(text))
    }

    override fun changeDataByFilter(filter: Int, textForSearch: String) {
        text = "%"
        when (filter) {
            0 -> {
                modeData = 0
                data = getLiveData(dao.getAll(text))
            }
            1 -> {
                modeData = 1
                data = getLiveData(dao.getFavorite(text))
            }
        }
    }

    override fun changeSearchText(textForSearch: String) {
        text = if (textForSearch == "") {
            "%"
        } else {
            textForSearch
        }
        println(text)
        data = getLiveData(dao.getAll(text))
    }

    private fun getLiveData(dataInput: LiveData<List<RecipeEntity>>): LiveData<List<Recipe>> =
        dataInput.map { entities ->
            entities.map { it.toModel() }
        }

    override fun insert(recipe: Recipe) {
        dao.insert(recipe.toEntity())
    }

    override fun updateContentById(recipe: Recipe) {
        dao.updateContentById(recipe.id, recipe.title)
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