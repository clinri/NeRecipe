package ru.netology.nerecipe.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY id DESC")
    fun getAll(): LiveData<List<RecipeEntity>>

    @Insert
    fun insert(recipe: RecipeEntity)

    @Query("UPDATE recipes SET title = :title WHERE id = :id")
    fun updateContentById(id: Int, title: String)

    @Query(
        """
        UPDATE recipes SET 
        favorite = CASE WHEN favorite THEN 0 ELSE 1 END
        WHERE id = :id
        """
    )
    fun favoriteById(id: Int)

    @Query("DELETE FROM recipes WHERE id = :id")
    fun removeById(id: Int)
}