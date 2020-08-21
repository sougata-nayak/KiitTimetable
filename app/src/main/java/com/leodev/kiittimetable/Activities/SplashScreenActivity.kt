package com.leodev.kiittimetable.Activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.leodev.kiittimetable.Models.Class
import com.leodev.kiittimetable.Models.TimetableSpecs
import com.leodev.kiittimetable.R

class SplashScreenActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    val database = Firebase.database
    val myRef = database.getReference("const")
    val db = Firebase.firestore

    val timetableSpecs: MutableList<TimetableSpecs> = arrayListOf()
    val classDetails: MutableList<Class> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        auth = FirebaseAuth.getInstance()
        checkLoggedInState()
    }

    private fun checkLoggedInState() {

        if (auth.currentUser != null) { // logged in

            val uid = auth.currentUser?.uid!!
            loginWhenUserNotNull(uid)
        }
        else{
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags =  Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun loginWhenUserNotNull(uid: String) {

        val sharedPref = getSharedPreferences("timetable", Context.MODE_PRIVATE)
        val jsonString = sharedPref.getString("classes", null)

        if(jsonString != null){

            val intent = Intent(this, MainActivity::class.java)
            intent.flags =  Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()

        }else{

            var branch: String = ""
            var year: String = ""

            db.collection("users").document(uid)
                .get()
                .addOnSuccessListener {
                    branch = it.data?.get("branch").toString()
                    year = it.data?.get("year").toString()
                }

            db.collection("users").document(uid).collection("timetable")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val teacher = document.data["teacher"].toString()
                        val sub = document.data["subject"].toString()
                        val group = document.data["group"].toString()
                        timetableSpecs.add(TimetableSpecs(sub, group, teacher))
                    }
                    if (timetableSpecs.isEmpty()) {
                        val intent = Intent(this@SplashScreenActivity, SelectionActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        createTimetable(branch, year, timetableSpecs)
                    }
                }
        }
    }

    private fun createTimetable(
        branch: String,
        year: String,
        timetableSpecs: MutableList<TimetableSpecs>
    ){
        var i=0
        myRef.child(year).child(branch).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                classDetails.clear()
                for (snaps in snapshot.children){
                    val prof = timetableSpecs[i].teacher
                    val sub = timetableSpecs[i].sub

                    for (sp in snaps.child("academic_group").child(timetableSpecs[i].group).children){
                        val day = sp.child("day_of_week").value.toString().toInt()
                        val startTime = sp.child("time").value.toString().toInt()
                        val type = sp.child("type").value.toString().capitalize()
                        val endTime = if (type == "Theory") startTime+1 else startTime+2

                        val a = Class(sub, prof, type, day, startTime, endTime)
                        classDetails.add(a)
                    }
                    if(i < timetableSpecs.size-1){i++}
                }

                classDetails.sortWith(kotlin.Comparator { p0, p1 -> (p0?.startTime?.minus((p1?.startTime)!!))!! })

                val timeTable = Gson().toJson(classDetails)
                val sharedPref = getSharedPreferences("timetable", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.apply{
                    putString("classes", timeTable)
                    apply()
                }
                val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                intent.flags =  Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        })
    }
}