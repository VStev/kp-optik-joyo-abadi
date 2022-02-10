package com.kp.optikjoyoabadi.ui.detailtutor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.kp.optikjoyoabadi.R

class TutorialRecipeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial_recipe)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Panduan Pengisian Detail"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}