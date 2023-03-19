package com.example.foody.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.foody.data.database.entities.FavoritesEntity
import com.example.foody.data.database.entities.RecipesEntity
import com.example.foody.data.database.entities.HealthyEntity
import com.example.foody.data.database.entities.PopularEntity

@Database(
    entities = [RecipesEntity::class, HealthyEntity::class, PopularEntity::class, FavoritesEntity::class],
    version = 9,
    exportSchema = false
)
@TypeConverters(RecipesTypeConverter::class)
abstract class RecipesDatabase: RoomDatabase() {

    abstract fun recipesDao(): RecipesDao
}