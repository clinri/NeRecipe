package ru.netology.nerecipe.db.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.netology.nerecipe.db.AppDbRoom
import ru.netology.nerecipe.db.RecipeDaoRoom
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppDbRoomModule { // с Interface не собиралось

    @Singleton
    @Provides
    fun provideDb(
        @ApplicationContext
        context: Context,
    ): AppDbRoom = Room.databaseBuilder(context, AppDbRoom::class.java, "app.db")
        .allowMainThreadQueries()
        .build()

    @Provides
    fun provideRecipeDao(
        appDb: AppDbRoom,
    ): RecipeDaoRoom = appDb.recipeDaoRoom()
}