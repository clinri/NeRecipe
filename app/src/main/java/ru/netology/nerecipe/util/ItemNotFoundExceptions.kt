package ru.netology.nerecipe.util

import java.lang.Exception

class ItemNotFoundExceptions(
    textException: String = "Recipe not found"
) : Exception(textException)