package ru.netology.nerecipe.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@TypeConverters(Converters::class)
@Database(
    entities = [RecipeEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDbRoom : RoomDatabase() {
    abstract fun recipeDaoRoom(): RecipeDaoRoom
}