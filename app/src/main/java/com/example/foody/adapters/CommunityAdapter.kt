package com.example.foody.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foody.R
import com.example.foody.models.Community
import com.google.android.material.imageview.ShapeableImageView

class CommunityAdapter(private val communityList: ArrayList<Community>):
    RecyclerView.Adapter<CommunityAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recipes_community_layout, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = communityList[position]
        holder.foodImage.setImageResource(currentItem.foodImage)
        holder.personImage.setImageResource(currentItem.personImage)
        holder.personName.text = currentItem.personName
        holder.foodName.text = currentItem.foodName
        holder.explanation.text = currentItem.explanation

    }

    override fun getItemCount(): Int {
        return communityList.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val foodImage: ShapeableImageView = itemView.findViewById(R.id.food_image)
        val personImage: ShapeableImageView = itemView.findViewById(R.id.person_image)
        val personName: TextView = itemView.findViewById(R.id.name_textView)
        val foodName: TextView = itemView.findViewById(R.id.foodname_textView)
        val explanation: TextView = itemView.findViewById(R.id.explain_textView)
    }



}