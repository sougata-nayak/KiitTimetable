package com.leodev.kiittimetable

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import com.leodev.kiittimetable.Adapters.ZoomAdapter
import kotlinx.android.synthetic.main.activity_zoom_link.*
import kotlinx.android.synthetic.main.item_zoom_links.view.*

class ZoomLinkActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoom_link)

        val sharedPrefs = getSharedPreferences("zoom", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()

        val adapter = ZoomAdapter(arrayListOf("Data Algorithm and Analysis",
            "Data Mining and Data Warehouse/Big Data",
            "Software Engineering",
            "Artificial Intelligence/Cryptography",
            "Computer Networking",
            "High Performance Computing"),
        sharedPrefs)

        rv_zoom_links.adapter = adapter
        rv_zoom_links.layoutManager = LinearLayoutManager(this)

        btn_zoom_links.setOnClickListener {
            rv_zoom_links.children.iterator().forEach {
                Log.d("TAG", "onCreate: ${it.tv_zoom_subject_name.text.toString()} -> ${it.zoom_link.text.toString()}")

                editor.apply{
                    putString(getSubjectCode(it.tv_zoom_subject_name.text.toString()), it.zoom_link.text.toString())
                    apply()
                }
            }
            finish()
        }
    }

    private fun getSubjectCode(sub: String): String {
        return when(sub){
            "Data Algorithm and Analysis" -> "CSE301"
            "Computer Networking" -> "CSE302"
            "High Performance Computing" -> "CSE303"
            "Software Engineering" -> "CSE304"
            "Artificial Intelligence/Cryptography" -> "CSE305"
            "Data Mining and Data Warehouse/Big Data" -> "CSE306"
            else -> ""
        }
    }
}