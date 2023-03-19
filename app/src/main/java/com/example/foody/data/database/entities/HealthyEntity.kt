package com.example.foody.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foody.models.FoodRecipe
import com.example.foody.util.Constants.Companion.HEALTHY_TABLE

@Entity(tableName = HEALTHY_TABLE)
class HealthyEntity(
    var foodRecipe: FoodRecipe,

) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0

}