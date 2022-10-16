package ru.netology.nerecipe.dto

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val id: Int,
    val orderManual: Int,
    val kitchenCategory: KitchenCategory,
    val author: String = "-",
    val title: String,
    val favorite: Boolean = false
)