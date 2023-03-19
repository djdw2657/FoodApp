package com.example.foody.ui.fragments.overview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.foody.R
import com.example.foody.adapters.IngredientsAdapter
import com.example.foody.adapters.RecipesAdapter
import com.example.foody.adapters.SimilarAdapter
import com.example.foody.models.Result
import com.example.foody.models.SimilarRecipeItem
import com.example.foody.util.Constants.Companion.RECIPE_RESULT_KEY
import com.example.foody.util.NetworkResult
import com.example.foody.viewmodels.MainViewModel
import com.example.foody.viewmodels.RecipesViewModel
import kotlinx.android.synthetic.main.fragment_overview.view.*
import org.jsoup.Jsoup

class OverviewFragment : Fragment() {

    private val mAdapter: IngredientsAdapter by lazy { IngredientsAdapter() }
    private val mSimilarAdapter: SimilarAdapter by lazy { SimilarAdapter() }

    private lateinit var mainViewModel: MainViewModel
    private lateinit var recipesViewModel: RecipesViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        recipesViewModel = ViewModelProvider(requireActivity()).get(RecipesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_overview, container, false)
        val args = arguments
        val myBundle : Result? = args?.getParcelable(RECIPE_RESULT_KEY)
        myBundle?.extendedIngredients?.let { mAdapter.setData(it) }
        setupRecyclerView(view)

        val recipeId = myBundle?.recipeId

        if (recipeId != null) {
            similarApiData(recipeId)
        }

        var numberOfServing = myBundle?.servings?.toString()
        var numberOfServingInButton = numberOfServing
        if (numberOfServing == null) {
            numberOfServing = "No Info about"
            numberOfServingInButton = "0"
        } else {
            myBundle?.servings.toString()
        }

        view.main_imageView.load(myBundle?.image)
        view.title_textView.text = myBundle?.title
        view.likes_textView.text = myBundle?.aggregateLikes.toString()
        view.time_textView.text = myBundle?.readyInMinutes.toString()
        view.serving_Textview.text = "$numberOfServing Servings"
        view.servings_inButton_textview.text = numberOfServingInButton
        myBundle?.summary.let {
            val summary = Jsoup.parse(it).text()
            view.summary_textView.text = summary
        }

        if(myBundle?.vegetarian == true){
            view.vegetarian_imageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
            view.vegetarian_textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        }

        if(myBundle?.vegan == true){
            view.vegan_imageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
            view.vegan_textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        }

        if(myBundle?.glutenFree == true){
            view.gluten_free_imageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
            view.gluten_free_textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        }

        if(myBundle?.dairyFree == true){
            view.dairy_free_imageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
            view.dairy_free_textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        }

        if(myBundle?.veryHealthy == true){
            view.healthy_imageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
            view.healthy_textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        }

        if(myBundle?.cheap == true){
            view.cheap_imageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
            view.cheap_textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        }

        return view
    }

    private fun similarApiData(recipeId: Int) {

        mainViewModel.similarRecipes(recipeId, recipesViewModel.applySimilarQuery())
        mainViewModel.similarRecipesResponse.observe(viewLifecycleOwner) { response ->

            when (response) {
                is NetworkResult.Success -> {
                    Log.d("TAGss", "Success: ${response.data}")
                    response.data?.let { mSimilarAdapter.setData(it) }
                }
                is NetworkResult.Error -> {
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("TAGss", "Error: ${response}")
                }
                is NetworkResult.Loading -> {
                    Log.d("TAGss", "Loading: ${response}")
                }
            }
        }
    }

    private fun setupRecyclerView(view: View) {
        view.ingredients_recyclerview.adapter = mAdapter
        view.ingredients_recyclerview.layoutManager = LinearLayoutManager(requireContext())
        view.similarRecipe_recyclerview.adapter = mSimilarAdapter
        view.similarRecipe_recyclerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL , false)
    }

}