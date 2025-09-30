package com.saveetha.fempulse

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

open class BaseActivity : AppCompatActivity() {
    open fun getCurrentNavId(): Int = R.id.nav_home
    override fun setContentView(layoutResID: Int) {
        val baseLayout = layoutInflater.inflate(R.layout.activity_base, null) as LinearLayout
        val container = baseLayout.findViewById<FrameLayout>(R.id.Container)
        layoutInflater.inflate(layoutResID, container, true)
        super.setContentView(baseLayout)
        setupBottomNav()
    }

    private fun setupBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Highlight the current item
        bottomNav.selectedItemId = getCurrentNavId()

        bottomNav.setOnItemSelectedListener { item ->
            // Play bounce animation on clicked icon
            val view = bottomNav.findViewById<android.view.View>(item.itemId)
            val anim = AnimationUtils.loadAnimation(this, R.anim.bounce)
            view?.startAnimation(anim)

            when (item.itemId) {
                R.id.nav_home -> {
                    if (this !is HomeActivity) {
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                    true
                }
                R.id.nav_calendar -> {
                    if (this !is CalendarActivity) {
                        val intent = Intent(this, CalendarActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                    true
                }
                R.id.nav_add -> {
                    if (this !is AddlogsActivity) {
                        val intent = Intent(this, AddlogsActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                    true
                }
                R.id.nav_stats -> {
                    if (this !is InsightsActivity) {
                        val intent = Intent(this, InsightsActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                    true
                }
                else -> false
            }

        }
    }


}
