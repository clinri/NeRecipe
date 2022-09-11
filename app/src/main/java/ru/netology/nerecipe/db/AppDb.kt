package ru.netology.nerecipe.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [RecipeEntity::class],
    version = 1
)
abstract class AppDb : RoomDatabase() {
    abstract val recipeDao: RecipeDao

    companion object {
        @Volatile
        private var instance: AppDb? = null
        fun getInstanse(context: Context): AppDb {
            return instance ?: synchronized(this) {
                instance ?: buildDataBase(context)
                .also { instance = it }
            }
        }

        private fun buildDataBase(context: Context) =
            Room.databaseBuilder(
                context, AppDb::class.java, "app.db"
            ).allowMainThreadQueries().build()
    }
}