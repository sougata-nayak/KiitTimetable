package com.leodev.kiittimetable.Activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.leodev.kiittimetable.Adapters.ZoomAdapter
import com.leodev.kiittimetable.Models.Class
import com.leodev.kiittimetable.R
import kotlinx.android.synthetic.main.activity_zoom_link.*

class ZoomLinkActivity : AppCompatActivity() {

    val db = Firebase.firestore
    val auth = FirebaseAuth.getInstance()
    val subjects: MutableSet<String> = mutableSetOf()
    val linksArray: MutableMap<String, String?> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoom_link)

        val zoomSharedPrefs = getSharedPreferences("zoom", Context.MODE_PRIVATE)
        for(sub in subjects){
            val sc = getSubjectCode(sub)
            val t = zoomSharedPrefs.getString(sc, null)
            linksArray[sc] = t
        }

        val sharedPref = getSharedPreferences("timetable", Context.MODE_PRIVATE)
        val jsonString = sharedPref.getString("classes", null)

        val timetable : java.util.ArrayList<Class> = Gson().fromJson(
            jsonString,
            object : TypeToken<java.util.ArrayList<Class>>() {}.type
        )

        for(t in timetable){
            t.name?.let { subjects.add(it) }
        }
        val sub = subjects.toList()
        Log.d("TAG", "onCreate: $sub")

        val adapter = ZoomAdapter(sub, zoomSharedPrefs, linksArray)

        rv_zoom_links.adapter = adapter
        rv_zoom_links.layoutManager = LinearLayoutManager(this)

        btn_zoom_links.setOnClickListener {
            for(sub in subjects){
                val subjectCode = getSubjectCode(sub)
                val link = zoomSharedPrefs.getString(subjectCode, null)

                val uid = auth.currentUser?.uid!!
                val info = hashMapOf("link" to link)
                val timetableDB = db.collection("users").document(uid).collection("timetable")
                timetableDB.document(subjectCode).set(info, SetOptions.merge())
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