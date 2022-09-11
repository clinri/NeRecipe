package ru.netology.nerecipe.adapter

import ru.netology.nerecipe.dto.Recipe

interface RecipesInteractionListener {
    fun onAddClicked()
    fun onEditClicked(recipe: Recipe)
    fun onCancelEditClicked()
    fun onRemoveClicked(recipe: Recipe)
    fun onFavoriteClicked(recipe: Recipe)
    fun onRecipeClicked(recipe: Recipe)
}