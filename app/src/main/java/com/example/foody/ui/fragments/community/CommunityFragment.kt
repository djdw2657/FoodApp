package com.example.foody.ui.fragments.community

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foody.R
import com.example.foody.adapters.CommunityAdapter
import com.example.foody.adapters.RecipesAdapter
import com.example.foody.databinding.FragmentRecipesBinding
import com.example.foody.models.Community
import com.example.foody.ui.fragments.recipes.RecipesFragmentArgs
import com.example.foody.viewmodels.MainViewModel
import com.example.foody.viewmodels.RecipesViewModel
import kotlinx.android.synthetic.main.fragment_community.view.*

class CommunityFragment : Fragment() {

    private lateinit var communityRecyclerView: RecyclerView
    private lateinit var newArrayList: ArrayList<Community>
    lateinit var foodImage : Array<Int>
    lateinit var personImage : Array<Int>
    lateinit var personName : Array<String>
    lateinit var foodName : Array<String>
    lateinit var explanation : Array<String>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_community, container, false)

        foodImage = arrayOf(
            R.drawable.f,
            R.drawable.g,
            R.drawable.h,
            R.drawable.j,
            R.drawable.k,
        )

        personImage = arrayOf(
            R.drawable.a,
            R.drawable.b,
            R.drawable.c,
            R.drawable.d,
            R.drawable.e,

        )
        personName = arrayOf(
            "michellez",
            "Jiffy J",
            "cvyas1983",
            "Ashika L",
            "Dongjin"

        )
        foodName = arrayOf(
            "Lemon Chicken Breast with Vegetables",
            "StrawBerry Pancakes",
            "Egg Pancakes",
            "Rocky Mountain Beefs",
            "A jellied and compressed loaf"

        )
        explanation = arrayOf(
            "Absolutely amazing food and the best dessert me and my husband have ever eaten. Highly recommend, plesent staff very clean, and I think the price is reasonable and decent size portions. Will definitely be returning with more family",
            "I had breakfast with my family a few days ago and my experience wasn't great. The food was good and portions were very good but the service was not good at all. The girls working there were friendly and professional but clearly understaffed and overworked. We were given a seat outside in the cold and with no outdoor heater available, we had to eat our breakfast as fast as we could to escape the cold. Three meals arrived, leaving my 10 year old waiting for his pancakes for over 10 minutes." +
                    "I called the owner over to voice my concern that my options were to either eat my food while my young son waits for his or let my food get cold, however I was cut short ( I found this to be rude and inconsiderate) and she went to kitchen and returned a few minutes later with his meal, offering an explanation that the meals come from different places, whatever that means." +
                    "There are lots of options in the area and there is no reason for me to choose this cafe again.",
            "I have visited this restaurant many times but unfortunately it’s not what it use to be… food dishes are bulked up with veg rather than meat so very disappointing considering the London prices…",
            "Went out of our way for a so called great pizza... unfortunately what we ate wasn't great. Very ordinary in fact... ordered meat lovers and a Hawaiian .. meat lovers was bacon (pre chopped bulk ham) and what looked n tasted like old fashioned bully beef) ..Hawaiian was same with 6 withered pieces of pineapple... both had minimal cheese... garlic bread was... ho hum." +
                    "Would we go out of our way again... NO... was it great.. NO.. was it edible.. yes.. 1 slice of each... the rest got binned.",
            "Brilliant service from Michael. Made everyone feel at ease and was extremely attentive towards allergies and intolerances. Every little detail matters and Micheal definitely went out of his way to ensure we were all catered for me served well. Thank you so much, we will definitely be back in the near future due to this! Such a warm and friendly welcome every time here, will continue to recommend."
        )

        communityRecyclerView = view.communityRecyclerview
        communityRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        newArrayList = arrayListOf()
        for (i in foodImage.indices) {
            val community =
                Community(foodImage[i], personImage[i], personName[i], foodName[i], explanation[i])
            newArrayList.add(community)
        }
        communityRecyclerView.adapter = CommunityAdapter(newArrayList)

        return view
    }
}