package com.example.foody.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.foody.R
import com.example.foody.adapters.MyIntroPagerRecyclerAdapter
import com.example.foody.models.PageItem
import kotlinx.android.synthetic.main.activity_intro.*


class IntroActivity : AppCompatActivity() {

    private lateinit var mSharedPreferences: SharedPreferences
    private val INTRO_PREFERENCE_KEY = "intro_shown"

    private var pageItemList = ArrayList<PageItem>()
    private lateinit var myIntroPagerRecyclerAdapter: MyIntroPagerRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_intro)


        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)


        val introShown = mSharedPreferences.getBoolean(INTRO_PREFERENCE_KEY, false)

        if (!introShown) {
            showIntroPage()

        } else {
            val i = Intent(this@IntroActivity, MainActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    private fun showIntroPage() {
        previous_btn.setOnClickListener {
            my_intro_view_pager.currentItem = my_intro_view_pager.currentItem - 1
        }

        next_btn.setOnClickListener {
            my_intro_view_pager.currentItem = my_intro_view_pager.currentItem + 1
        }

        skip_btn.setOnClickListener {
            val editor = mSharedPreferences.edit()
            editor.putBoolean(INTRO_PREFERENCE_KEY, true)
            editor.apply()

            val i = Intent(this@IntroActivity, MainActivity::class.java)
            startActivity(i)
            finish()
        }

        pageItemList.add(
            PageItem(
                R.color.white,
                R.drawable.f,
                "Discover new recipes!",
                "Find delicious new recipes to try and share with your friends and family."
            )
        )
        pageItemList.add(
            PageItem(
                R.color.white,
                R.drawable.g,
                "Organize your favorites",
                " Save your favorite recipes and organize them into collections for easy access."
            )
        )
        pageItemList.add(
            PageItem(
                R.color.white,
                R.drawable.h, "Get cooking!",
                "Start exploring new recipes and cooking up a storm with our app.\""
            )
        )

        myIntroPagerRecyclerAdapter = MyIntroPagerRecyclerAdapter(pageItemList)

        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        // 뷰페이저에 설정
        my_intro_view_pager.apply {

            adapter = myIntroPagerRecyclerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            dots_indicator.setViewPager2(this)
        }
    }
}


