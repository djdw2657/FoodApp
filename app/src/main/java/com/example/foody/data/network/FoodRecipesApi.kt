package com.example.foody.data.network

import com.example.foody.models.FoodRecipe
import com.example.foody.models.SimilarRecipe
import com.example.foody.models.SimilarRecipeItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface FoodRecipesApi {

    @GET("/recipes/complexSearch")
    suspend fun getRecipes(
        @QueryMap queries: Map<String, String>
    ): Response<FoodRecipe>

    @GET("/recipes/complexSearch")
    suspend fun searchRecipes(
        @QueryMap searchQuery: Map<String, String>
    ): Response<FoodRecipe>

    // https://api.spoonacular.com/recipes/715538/similar?apiKey=95a60713b4c94dc49610b2557a7bcb9a&number=2
    @GET("/recipes/{recipeId}/similar")
    suspend fun similarRecipes(
        @Path("recipeId") recipeId: Int,
        @QueryMap similarQuery: Map<String, String>
    ): Response<SimilarRecipe>

}