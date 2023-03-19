package com.example.foody.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.foody.data.Repository
import com.example.foody.data.database.entities.FavoritesEntity
import com.example.foody.data.database.entities.HealthyEntity
import com.example.foody.data.database.entities.PopularEntity
import com.example.foody.data.database.entities.RecipesEntity
import com.example.foody.models.FoodRecipe
import com.example.foody.models.SimilarRecipe
import com.example.foody.models.SimilarRecipeItem
import com.example.foody.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception

class MainViewModel @ViewModelInject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {


    /** ROOM DATABASE */

    val readRecipes: LiveData<List<RecipesEntity>> = repository.local.readDatabase().asLiveData()
    val readHealthyRecipes: LiveData<List<HealthyEntity>> = repository.local.readHealthyDatabase().asLiveData()
    val readPopularRecipes: LiveData<List<PopularEntity>> = repository.local.readPopularDatabase().asLiveData()
    val readFavoriteRecipes: LiveData<List<FavoritesEntity>> = repository.local.readFavoriteRecipes().asLiveData()

    private fun insertRecipes(recipesEntity: RecipesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertRecipes(recipesEntity)
        }

    private fun insertHealthyRecipes(healthyEntity: HealthyEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertHealthyRecipes(healthyEntity)
        }

    private fun insertPopularRecipes(popularEntity: PopularEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertPopularRecipes(popularEntity)
        }

    fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertFavoriteRecipes(favoritesEntity)
        }

    fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteFavoriteRecipe(favoritesEntity)
        }

    fun deleteAllFavoriteRecipes() =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteAllFavoriteRecipes()
        }

    /** RETROFIT */
    var recipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    var healthyResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    var popularResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    var selectedResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    var searchedRecipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    var similarRecipesResponse: MutableLiveData<NetworkResult<SimilarRecipe>> = MutableLiveData()


    fun getRecipes(queries: Map<String, String>) = viewModelScope.launch { getRecipesSafeCall(queries) }
    fun getHealthy(queries: Map<String, String>) = viewModelScope.launch { getHealthySafeCall(queries) }
    fun getPopular(queries: Map<String, String>) = viewModelScope.launch { getPopularSafeCall(queries) }
    fun getSelectedRecipes(queries: Map<String, String>) = viewModelScope.launch { getSelectedRecipesSafeCall(queries) }

    fun searchRecipes(searchQuery: Map<String, String>) = viewModelScope.launch {
        searchRecipesSafeCall(searchQuery)
    }

    fun similarRecipes(recipeId: Int, similarQuery: Map<String, String>) = viewModelScope.launch {
        similarRecipesSafeCall(recipeId, similarQuery)
    }

    private suspend fun similarRecipesSafeCall(recipeId: Int, similarQuery: Map<String, String>) {
        similarRecipesResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.similarRecipes(recipeId,similarQuery)
                similarRecipesResponse.value = handleSimilarRecipesResponse(response)
            } catch (e: Exception) {
                similarRecipesResponse.value = NetworkResult.Error("Recipes not found.")
            }
        } else {
            searchedRecipesResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private suspend fun searchRecipesSafeCall(searchQuery: Map<String, String>) {
        searchedRecipesResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.searchRecipes(searchQuery)
                searchedRecipesResponse.value = handleFoodRecipesResponse(response)
            } catch (e: Exception) {
                searchedRecipesResponse.value = NetworkResult.Error("Recipes not found.")
            }
        } else {
            searchedRecipesResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private suspend fun getSelectedRecipesSafeCall(queries: Map<String, String>) {
        selectedResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getRecipes(queries)
                selectedResponse.value = handleFoodRecipesResponse(response)

            } catch (e: Exception) {
                selectedResponse.value = NetworkResult.Error("Recipes not found.")
            }
        } else {
            selectedResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private suspend fun getPopularSafeCall(queries: Map<String, String>) {
        popularResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getPopular(queries)
                popularResponse.value = handleFoodRecipesResponse(response)

                val foodRecipe = popularResponse.value!!.data
                if(foodRecipe != null) {

                    offlineCachePopularRecipes(foodRecipe)
                }

            } catch (e: Exception) {
                popularResponse.value = NetworkResult.Error("Recipes not found.")
            }
        } else {
            popularResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }
    private suspend fun getHealthySafeCall(queries: Map<String, String>) {
        healthyResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getHealthy(queries)
                healthyResponse.value = handleFoodRecipesResponse(response)

                val foodRecipe = healthyResponse.value!!.data
                if(foodRecipe != null) {

                        offlineCacheHealthyRecipes(foodRecipe)
                    }

            } catch (e: Exception) {
                healthyResponse.value = NetworkResult.Error("Recipes not found.")
            }
        } else {
            healthyResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private suspend fun getRecipesSafeCall(queries: Map<String, String>) {
        recipesResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getRecipes(queries)
                recipesResponse.value = handleFoodRecipesResponse(response)

                val foodRecipe = recipesResponse.value!!.data
                if(foodRecipe != null) {
                        offlineCacheRecipes(foodRecipe)
                    }

            } catch (e: Exception) {
                recipesResponse.value = NetworkResult.Error("Recipes not found.")
            }
        } else {
            recipesResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private fun offlineCacheRecipes(foodRecipe: FoodRecipe) {
        val recipesEntity = RecipesEntity(foodRecipe)
        insertRecipes(recipesEntity)
    }

    private fun offlineCacheHealthyRecipes(foodRecipe: FoodRecipe) {
        val recipesHealthyEntity = HealthyEntity(foodRecipe)
        insertHealthyRecipes(recipesHealthyEntity)
    }

    private fun offlineCachePopularRecipes(foodRecipe: FoodRecipe) {
        val recipesPopularEntity = PopularEntity(foodRecipe)
        insertPopularRecipes(recipesPopularEntity)
    }

    private fun handleSimilarRecipesResponse(response: Response<SimilarRecipe>): NetworkResult<SimilarRecipe>? {
        when {
            response.message().toString().contains("timeout") -> {
                Log.d("TAGs", "handleSimilarRecipesResponse: Timeout")
                return NetworkResult.Error("Timeout")

            }
            response.code() == 402 -> {
                Log.d("TAGs", "handleSimilarRecipesResponse: apikey")
                return NetworkResult.Error("API Key Limited.")

            }
            response.body()!!.isEmpty() -> {
                Log.d("TAGs", "handleSimilarRecipesResponse: recipes not found")
                return NetworkResult.Error("Recipes not found.")

            }
            response.isSuccessful -> {
                val foodRecipes = response.body()
                return NetworkResult.Success(foodRecipes!!)
            }
            else -> {
                Log.d("TAGs", "handleSimilarRecipesResponse: ${response.message()}")
                return NetworkResult.Error(response.message())

            }
        }
    }

    private fun handleFoodRecipesResponse(response: Response<FoodRecipe>): NetworkResult<FoodRecipe>? {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                return NetworkResult.Error("API Key Limited.")
            }
            response.body()!!.results.isNullOrEmpty() -> {
                return NetworkResult.Error("Recipes not found.")
            }
            response.isSuccessful -> {
                val foodRecipes = response.body()
                return NetworkResult.Success(foodRecipes!!)
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}