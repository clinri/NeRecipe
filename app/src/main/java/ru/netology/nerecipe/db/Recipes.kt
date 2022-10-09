package ru.netology.nerecipe.db

import ru.netology.nerecipe.db.RecipeEntity
import ru.netology.nerecipe.dto.Recipe

internal fun RecipeEntity.toModel() = Recipe(
    id = id,
    category = category,
    author = author,
    title = title,
    favorite = favorite,
    orderManual = orderManual
)

internal fun Recipe.toEntity() = RecipeEntity(
    id = id,
    category = category,
    author = author,
    title = title,
    favorite = favorite,
    orderManual = orderManual
)