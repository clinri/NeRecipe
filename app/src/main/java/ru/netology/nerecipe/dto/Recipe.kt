package ru.netology.nerecipe.dto

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val id: Int,
    val category: String = "-",
    val author: String = "-",
    val title: String,
    val favorite: Boolean = false
)