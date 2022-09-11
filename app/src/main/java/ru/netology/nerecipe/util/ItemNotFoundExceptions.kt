package ru.netology.nmedia.util

import java.lang.Exception

class ItemNotFoundExceptions(
    textException: String = "Recipe not found"
) : Exception(textException)