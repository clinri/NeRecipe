package ru.netology.nerecipe.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.nerecipe.data.RecipesRepository
import ru.netology.nerecipe.data.impl.RecipesRepositoryImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RecipesRepositoryModule {

    @Singleton
    @Binds
    fun bindsRecipesRepository(impl: RecipesRepositoryImpl): RecipesRepository
}