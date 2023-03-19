package com.example.foody.ui.fragments.recipes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foody.R
import com.example.foody.adapters.HealthyAdapter
import com.example.foody.adapters.PopularAdapter
import com.example.foody.adapters.RecipesAdapter
import com.example.foody.adapters.SearchedAdapter
import com.example.foody.databinding.FragmentRecipesBinding
import com.example.foody.util.NetworkListener
import com.example.foody.util.NetworkResult
import com.example.foody.util.observeOnce
import com.example.foody.viewmodels.MainViewModel
import com.example.foody.viewmodels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RecipesFragment : Fragment(), SearchView.OnQueryTextListener {

    private val args by navArgs<RecipesFragmentArgs>()

    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel
    private lateinit var recipesViewModel: RecipesViewModel
    private val mAdapter by lazy { RecipesAdapter() }
    private val mHealthyAdapter by lazy { HealthyAdapter() }
    private val mPopularAdapter by lazy { PopularAdapter() }
    private val mSearchedAdapter by lazy { SearchedAdapter() }

    private lateinit var networkListener: NetworkListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        recipesViewModel = ViewModelProvider(requireActivity()).get(RecipesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel
        setupRecyclerView()

        recipesViewModel.readBackOnline.observe(viewLifecycleOwner) {
            recipesViewModel.backOnline = it
        }

        lifecycleScope.launch {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext())
                .collect { status ->
                    Log.d("NetworkListener", status.toString())
                    recipesViewModel.networkStatus = status
                    recipesViewModel.showNetworkStatus()
                    readDatabase()
                    readHealthyDatabase()
                    readPopularDatabase()
                    moveToBottomSheet()
                    searchByText()
                }
        }
        searchByText()

        return binding.root
    }

    private fun moveToBottomSheet() {
        binding.recipesFab.setOnClickListener {
            if (recipesViewModel.networkStatus) {
                findNavController().navigate(R.id.action_recipesFragment_to_recipesBottomSheet)

            } else {
                recipesViewModel.showNetworkStatus()
            }
        }
    }

    private fun searchByText() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query != null) {
                    searchApiData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // handle search query text changes here
                Log.d("TAGg", "Search query: $newText")
                return true
            }
        })
    }

    private fun searchApiData(searchQuery: String) {

        mainViewModel.searchRecipes(recipesViewModel.applySearchQuery(searchQuery))
        Log.d("TAGg", recipesViewModel.applySearchQuery(searchQuery).toString())
        mainViewModel.searchedRecipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    val foodRecipe = response.data
                    foodRecipe?.let { mSearchedAdapter.setData(it) }
                    binding.progressbar.visibility = View.INVISIBLE
                    binding.searchView.visibility = View.VISIBLE
                    binding.searchedScrollView.visibility = View.VISIBLE
                    binding.scrollView.visibility = View.INVISIBLE

                }
                is NetworkResult.Error -> {
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.progressbar.visibility = View.INVISIBLE
                    binding.searchView.visibility = View.VISIBLE
                    binding.searchedScrollView.visibility = View.VISIBLE
                    binding.scrollView.visibility = View.INVISIBLE
                }
                is NetworkResult.Loading -> {
                    binding.progressbar.visibility = View.VISIBLE
                    binding.searchView.visibility = View.INVISIBLE
                    binding.scrollView.visibility = View.INVISIBLE
                    binding.searchedScrollView.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun readDatabase() {
        lifecycleScope.launch {
            mainViewModel.readRecipes.observeOnce(viewLifecycleOwner) { database ->
                if (database.isNotEmpty() && !args.backFromBottomSheet) {
                    Log.d("RecipesFragment", "readDatabase called!")
                    mAdapter.setData(database[0].foodRecipe)
                } else {
                    requestApiData()
                }
            }
        }
    }
    private fun readHealthyDatabase() {
        lifecycleScope.launch {
            mainViewModel.readHealthyRecipes.observeOnce(viewLifecycleOwner) { database ->
                if (database.isNotEmpty() && !args.backFromBottomSheet) {
                    Log.d("RecipesFragment", "readDatabase called!")
                    mHealthyAdapter.setData(database[0].foodRecipe)
                } else {
                    requestHealthyApiData()
                }
            }
        }
    }

    private fun readPopularDatabase() {
        lifecycleScope.launch {
            mainViewModel.readPopularRecipes.observeOnce(viewLifecycleOwner) { database ->
                if (database.isNotEmpty() && !args.backFromBottomSheet) {
                    Log.d("RecipesFragment", "readDatabase called!")
                    mPopularAdapter.setData(database[0].foodRecipe)
                } else {
                    requestPopularApiData()
                }
            }
        }
    }

    private fun requestPopularApiData() {
        mainViewModel.getPopular(recipesViewModel.applyPopularQueries())
        mainViewModel.popularResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { mPopularAdapter.setData(it) }
                    binding.progressbar.visibility = View.INVISIBLE
                    binding.searchView.visibility = View.VISIBLE
                    binding.scrollView.visibility = View.VISIBLE
                }
                is NetworkResult.Error -> {
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.progressbar.visibility = View.INVISIBLE
                    binding.searchView.visibility = View.VISIBLE
                    binding.scrollView.visibility = View.VISIBLE
                }
                is NetworkResult.Loading -> {
                    binding.progressbar.visibility = View.VISIBLE
                    binding.searchView.visibility = View.INVISIBLE
                    binding.scrollView.visibility = View.INVISIBLE
                }
            }
        }
    }


    private fun requestHealthyApiData() {
        mainViewModel.getHealthy(recipesViewModel.applyHealthyQueries())
        mainViewModel.healthyResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { mHealthyAdapter.setData(it) }
                    binding.progressbar.visibility = View.INVISIBLE
                    binding.searchView.visibility = View.VISIBLE
                    binding.scrollView.visibility = View.VISIBLE
                }
                is NetworkResult.Error -> {
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.progressbar.visibility = View.INVISIBLE
                    binding.searchView.visibility = View.VISIBLE
                    binding.scrollView.visibility = View.VISIBLE
                }
                is NetworkResult.Loading -> {
                    binding.progressbar.visibility = View.VISIBLE
                    binding.searchView.visibility = View.INVISIBLE
                    binding.scrollView.visibility = View.INVISIBLE
                }
            }
        }
    }
    private fun requestApiData() {
        mainViewModel.getRecipes(recipesViewModel.applyQueries())
        mainViewModel.recipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { mAdapter.setData(it) }
                    binding.progressbar.visibility = View.INVISIBLE
                    binding.searchView.visibility = View.VISIBLE
                    binding.scrollView.visibility = View.VISIBLE
                }
                is NetworkResult.Error -> {
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.progressbar.visibility = View.INVISIBLE
                    binding.searchView.visibility = View.VISIBLE
                    binding.scrollView.visibility = View.VISIBLE
                }
                is NetworkResult.Loading -> {
                    binding.progressbar.visibility = View.VISIBLE
                    binding.searchView.visibility = View.INVISIBLE
                    binding.scrollView.visibility = View.INVISIBLE
                }
            }
        }
    }


    private fun setupRecyclerView() {
        binding.recommendedRecyclerView.adapter = mAdapter
        binding.recommendedRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL , false)

        binding.healthyRecyclerview.adapter = mHealthyAdapter
        binding.healthyRecyclerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL , false)

        binding.popularRecyclerview.adapter = mPopularAdapter
        binding.popularRecyclerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL , false)

        binding.searchedRecyclerview.adapter = mSearchedAdapter
        binding.searchedRecyclerview.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if(query != null) {
            searchApiData(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        TODO("Not yet implemented")
    }
}