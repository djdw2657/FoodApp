package com.example.foody.adapters

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.foody.R
import com.example.foody.models.PageItem
import kotlinx.android.synthetic.main.layout_intro_pager_item.view.*

class MyIntroPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val itemImage = itemView.pager_item_image
    private val itemContent = itemView.pager_item_text
    private val itemBg = itemView.pager_item_bg
    private val itemContent2 = itemView.pager_item_text2

    fun bindWithView(pageItem: PageItem){
        itemImage.setImageResource(pageItem.imageSrc)
        itemContent.text = pageItem.content
        itemContent2.text = pageItem.content2

        if(pageItem.bgColor != R.color.white){
            itemContent.setTextColor(Color.WHITE)
            itemContent2.setTextColor(Color.WHITE)
        }

        itemBg.setBackgroundResource(pageItem.bgColor)
    }

}
