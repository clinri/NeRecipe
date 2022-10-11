package ru.netology.nerecipe.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface RecipeDao {
    @Query(
        """
        SELECT * FROM recipes 
        WHERE title LIKE :text
    """
    )
    fun getAll(text: String): LiveData<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE title LIKE :text AND favorite = 1 ORDER BY orderManual DESC")
    fun getFavorite(text: String): LiveData<List<RecipeEntity>>

    @Insert
    fun insertRecipeWithoutId(recipe: RecipeEntity): Long

    @Transaction
    fun insert(recipe: RecipeEntity) {
        val id = insertRecipeWithoutId(recipe).toInt()
        updateOrderById(id, id)
    }

    @Query("UPDATE recipes SET title = :title WHERE id = :id")
    fun updateContentById(id: Int, title: String)

    @Query("UPDATE recipes SET orderManual = :order WHERE id = :id")
    fun updateOrderById(id: Int, order: Int)

    @Transaction
    fun swapOrdersByIds(id1: Int, order1: Int, id2: Int, order2: Int) {
        updateOrderById(id1, order2)
        updateOrderById(id2, order1)
    }

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