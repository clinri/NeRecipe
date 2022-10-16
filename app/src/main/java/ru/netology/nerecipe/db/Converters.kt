package ru.netology.nerecipe.db

import androidx.room.TypeConverter
import ru.netology.nerecipe.dto.KitchenCategory

class Converters {
    @TypeConverter
    fun toCategory(value: String) = enumValueOf<KitchenCategory>(value) //restore Enum

    @TypeConverter
    fun fromCategory(value: KitchenCategory) = value.name //save Enum To String
}