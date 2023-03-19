package com.example.foody.ui.fragments.selected


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.foody.R
import com.example.foody.adapters.SearchedAdapter
import com.example.foody.adapters.SelectedAdapter
import com.example.foody.util.NetworkResult
import com.example.foody.viewmodels.MainViewModel
import com.example.foody.viewmodels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_selected.view.*

@AndroidEntryPoint
class SelectedFragment : Fragment(), SearchView.OnQueryTextListener  {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var recipesViewModel: RecipesViewModel
    private val mSelectedAdapter by lazy { SelectedAdapter() }
    private lateinit var mSelectedView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        recipesViewModel = ViewModelProvider(requireActivity()).get(RecipesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mSelectedView = inflater.inflate(R.layout.fragment_selected, container, false)
        mSelectedView.selectedSearchView.setOnClickListener {
            findNavController().navigate(R.id.action_selectedFragment_to_recipesFragment)
        }

        setupGridRecyclerView()
        requestSelectedApiData()
        searchByText()
        return mSelectedView
    }

    private fun searchByText() {
        mSelectedView.selectedSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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

    private fun requestSelectedApiData() {
        mainViewModel.getSelectedRecipes(recipesViewModel.applySelectedQueries())
        Log.d("oncreateviewd", recipesViewModel.applySelectedQueries().toString())
        mainViewModel.selectedResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { mSelectedAdapter.setData(it) }
                    mSelectedView.selectedProgressbar.visibility = View.INVISIBLE
                    mSelectedView.selectedSearchView.visibility = View.VISIBLE
                    mSelectedView.selectedScrollView.visibility = View.VISIBLE
                }
                is NetworkResult.Error -> {
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    mSelectedView.selectedProgressbar.visibility = View.INVISIBLE
                    mSelectedView.selectedSearchView.visibility = View.VISIBLE
                    mSelectedView.selectedScrollView.visibility = View.VISIBLE
                }
                is NetworkResult.Loading -> {
                    mSelectedView.selectedProgressbar.visibility = View.VISIBLE
                    mSelectedView.selectedSearchView.visibility = View.INVISIBLE
                    mSelectedView.selectedScrollView.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun searchApiData(searchQuery: String) {

        mainViewModel.searchRecipes(recipesViewModel.applySearchQuery(searchQuery))
        Log.d("TAGg", recipesViewModel.applySearchQuery(searchQuery).toString())
        mainViewModel.searchedRecipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { mSelectedAdapter.setData(it) }
                    mSelectedView.selectedProgressbar.visibility = View.INVISIBLE
                    mSelectedView.selectedSearchView.visibility = View.VISIBLE
                    mSelectedView.selectedScrollView.visibility = View.VISIBLE
                }
                is NetworkResult.Error -> {
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    mSelectedView.selectedProgressbar.visibility = View.INVISIBLE
                    mSelectedView.selectedSearchView.visibility = View.VISIBLE
                    mSelectedView.selectedScrollView.visibility = View.VISIBLE
                }
                is NetworkResult.Loading -> {
                    mSelectedView.selectedProgressbar.visibility = View.VISIBLE
                    mSelectedView.selectedSearchView.visibility = View.INVISIBLE
                    mSelectedView.selectedScrollView.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun setupGridRecyclerView() {
        mSelectedView.selectedRecyclerview.adapter = mSelectedAdapter
        mSelectedView.selectedRecyclerview.layoutManager = GridLayoutManager(requireContext(), 2)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // navigate back to previous screen
                requireActivity().onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}