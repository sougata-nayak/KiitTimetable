package com.leodev.kiittimetable.Activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.leodev.kiittimetable.Adapters.DaysPagerAdapter
import com.leodev.kiittimetable.Models.Class
import com.leodev.kiittimetable.R
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        auth = FirebaseAuth.getInstance()

        createTimetable()
    }

    private fun createTimetable(){
        val sharedPref = getSharedPreferences("timetable", Context.MODE_PRIVATE)
        val jsonString = sharedPref.getString("classes", null)

        val timetable : ArrayList<Class> = Gson().fromJson(
            jsonString,
            object : TypeToken<ArrayList<Class>>() {}.type
        )

        if (timetable != null && timetable.isNotEmpty()) {
            pager.adapter = DaysPagerAdapter(
                timetable,
                supportFragmentManager
            )
            tabs.setupWithViewPager(pager)

            // Normalize day value - our adapter works with five days, the first day (0) being Monday.
            val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2
            pager.currentItem = if (today in 0..4) today else 0
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.mn_subjects -> startActivity(Intent(this, SelectionActivity::class.java))
            R.id.mn_signout -> {
                val sharedPref = getSharedPreferences("timetable", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.apply{
                    putString("classes", null)
                    apply()
                }
                auth.signOut()
                startActivity(Intent(this, LoginActivity::class.java).also {
                    it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                })
                finish()
            }
        }
        return true
    }


}
