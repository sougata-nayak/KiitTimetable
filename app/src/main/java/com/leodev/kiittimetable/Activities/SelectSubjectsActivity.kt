package com.leodev.kiittimetable.Activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.view.get
import androidx.core.view.iterator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.leodev.kiittimetable.Adapters.SubjectsAdapter
import com.leodev.kiittimetable.Models.Class
import com.leodev.kiittimetable.R
import com.leodev.kiittimetable.Models.SubjectTeachers
import com.leodev.kiittimetable.Models.TimetableSpecs
import kotlinx.android.synthetic.main.activity_select_subjects.*
import kotlinx.android.synthetic.main.item_subjects.view.*
import kotlin.collections.ArrayList

class SelectSubjectsActivity : AppCompatActivity() {

    val database = Firebase.database
    val myRef = database.getReference("const")
    lateinit var auth: FirebaseAuth

    val db = Firebase.firestore

    var subjects = arrayOfNulls<SubjectTeachers>(6)
    var teachers : ArrayList<String> = arrayListOf()

    val timetableSpecs: MutableList<TimetableSpecs> = arrayListOf()
    val classDetails: MutableList<Class> = arrayListOf()
    var t : ArrayList<String> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_subjects)

        val y = intent.getIntExtra("year", 0)
        var branch = intent.getStringExtra("branch")
        if (branch == null){
            branch = "CSE"
        }
        val year = when(y){
            1 -> "first"
            2 -> "second"
            3 -> "third"
            4 -> "fourth"
            else -> ""
        }

        auth = FirebaseAuth.getInstance()
        val email = auth.currentUser?.email!!

        getSubjectsList(year, branch)
        val groupsSharedPref = getSharedPreferences("group", Context.MODE_PRIVATE)
        val teacherSharedPref = getSharedPreferences("teacher", Context.MODE_PRIVATE)

        bt_make_timetable.setOnClickListener {

            rv_subjects.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
            bt_make_timetable.visibility = View.INVISIBLE

            for(t in subjects){
                val subjectName= t?.sub
                if (subjectName != null){

                    val sc = getSubjectCode(subjectName)
                    val g = groupsSharedPref.getInt(sc, 0)
                    val t = teacherSharedPref.getString(sc, null)
                    val group = findViewById<RadioButton>(g).text.toString()

                    val new = t?.let { it1 -> TimetableSpecs(subjectName, group, it1) }
                    if (new != null) {
                        timetableSpecs.add(new)
                    }
                }
            }

            Log.d("TAG", "onCreate: $timetableSpecs")

            storeUserData(timetableSpecs, branch, year, email)

            createTimetableFromDetails(branch, year)
        }
    }

    private fun getSubjectsList(year: String, branch: String) {

        myRef.child(year).child(branch).addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for((i, sp) in snapshot.children.withIndex()){
                    teachers.clear()
                    for(item in sp.child("teachers").children){
                        teachers.add(item.child("teacher_name").value.toString())
                    }
                    t = teachers
                    subjects[i] = when(t.size){
                        0 -> SubjectTeachers(sp.child("subject_name").value.toString(), arrayListOf())
                        1 -> SubjectTeachers(sp.child("subject_name").value.toString(), arrayListOf(t[0]))
                        2 -> SubjectTeachers(sp.child("subject_name").value.toString(), arrayListOf(t[0], t[1]))
                        3 -> SubjectTeachers(sp.child("subject_name").value.toString(), arrayListOf(t[0], t[1], t[2]))
                        4 -> SubjectTeachers(sp.child("subject_name").value.toString(), arrayListOf(t[0], t[1], t[2], t[3]))
                        5 -> SubjectTeachers(sp.child("subject_name").value.toString(), arrayListOf(t[0], t[1], t[2], t[3], t[4]))
                        6 -> SubjectTeachers(sp.child("subject_name").value.toString(), arrayListOf(t[0], t[1], t[2], t[3], t[4], t[5]))
                        7 -> SubjectTeachers(sp.child("subject_name").value.toString(), arrayListOf(t[0], t[1], t[2], t[3], t[4], t[5], t[6]))
                        8 -> SubjectTeachers(sp.child("subject_name").value.toString(), arrayListOf(t[0], t[1], t[2], t[3], t[4], t[5], t[6], t[7]))
                        9 -> SubjectTeachers(sp.child("subject_name").value.toString(), arrayListOf(t[0], t[1], t[2], t[3], t[4], t[5], t[6], t[7], t[8]))
                        else -> SubjectTeachers(sp.child("subject_name").value.toString(), arrayListOf(t[0], t[1], t[2], t[3], t[4], t[5], t[6], t[7], t[8], t[9]))
                    }
                }
                setViews(subjects)
            }
        })
    }

    private fun setViews(subjects: Array<SubjectTeachers?>) {
        val groupsSharedPref = getSharedPreferences("group", Context.MODE_PRIVATE)
        val teacherSharedPref = getSharedPreferences("teacher", Context.MODE_PRIVATE)

        for(subject in subjects){
            if(subject != null){
                val sc = getSubjectCode(subject.sub)
                val teacher = teacherSharedPref.getString(sc, null)
                if(teacher == null){
                    teacherSharedPref.edit().apply {
                        putString(sc, subject.teach[0])
                        apply()
                    }
                }
            }
        }

        if (subjects.isNotEmpty()){
            val adapter = SubjectsAdapter(subjects, this, groupsSharedPref, teacherSharedPref)
            rv_subjects.adapter = adapter
            rv_subjects.layoutManager = LinearLayoutManager(this)

            rv_subjects.visibility = View.VISIBLE
            progressBar.visibility = View.INVISIBLE
            bt_make_timetable.visibility = View.VISIBLE
        }
    }

    private fun storeUserData(
        timetableSpecs: MutableList<TimetableSpecs>,
        branch: String,
        year: String,
        email: String
    ) {
        val uid = auth.currentUser?.uid!!
        try {
            val user = hashMapOf(
                "email" to email,
                "year" to year,
                "branch" to branch
            )
            db.collection("users").document(uid).set(user)
            val timetableDB = db.collection("users").document(uid).collection("timetable")
            for(items in timetableSpecs){
                val timetable = hashMapOf(
                    "subject" to items.sub,
                    "group" to items.group,
                    "teacher" to items.teacher
                )
                val code = getSubjectCode(items.sub)
                timetableDB.document(code).set(timetable)
            }
        }
        catch (e : Exception){
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            Log.d("TAG", "storeUserData: ${e.message}")
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

    private fun createTimetableFromDetails(branch: String, year: String){
        var i=0

        myRef.child(year).child(branch).addValueEventListener(object : ValueEventListener{
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
                val intent = Intent(this@SelectSubjectsActivity, MainActivity::class.java)
                intent.flags =  Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        })
    }


}