package com.example.foody.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foody.models.FoodRecipe
import com.example.foody.util.Constants

@Entity(tableName = Constants.POPULAR_TABLE)
class PopularEntity(
    var foodRecipe: FoodRecipe,

    ) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0

}