package com.leodev.kiittimetable.Activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.leodev.kiittimetable.Adapters.SubjectsAdapter
import com.leodev.kiittimetable.Models.Class
import com.leodev.kiittimetable.R
import com.leodev.kiittimetable.Models.SubjectTeachers
import com.leodev.kiittimetable.Models.TimetableSpecs
import kotlinx.android.synthetic.main.activity_select_subjects.*
import kotlinx.android.synthetic.main.item_subjects.view.*

class SelectSubjectsActivity : AppCompatActivity() {

    val database = Firebase.database
    val myRef = database.getReference("constant")

    var subjects : MutableList<SubjectTeachers> = arrayListOf()
    var teachers : MutableList<String> = arrayListOf()

    val timetableSpecs: MutableList<TimetableSpecs> = arrayListOf()
    val classDetails: MutableList<Class> = arrayListOf()


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

        getSubjectsList(year, branch)

        bt_make_timetable.setOnClickListener {

            rv_subjects.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
            bt_make_timetable.visibility = View.INVISIBLE

            Log.d("TAG", "onCreate:size $subjects")
            // element ${rv_subjects[7].tv_subject_name.text.toString()}

            for(i in 0 until subjects.size-1){
                val view = rv_subjects[i]
                val radioButton : RadioButton = findViewById(view.rg_group.checkedRadioButtonId)

                val sub = view.tv_subject_name.text.toString()
                val group = radioButton.text.toString()
                val teacher = view.sp_teacher.selectedItem.toString()
                Log.d("TAG", "onCreate: $sub")
                val g = TimetableSpecs(
                    sub,
                    group,
                    teacher
                )
                timetableSpecs.add(g)
                }

            createTimetableFromDetails(branch, year)
        }
    }

    private fun getSubjectsList(year: String, branch: String) {

        myRef.child(year).child(branch).addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (sp in snapshot.children){
                    teachers.clear()
                    for(item in sp.child("teachers").children){
                        teachers.add(item.child("teacher_name").value.toString())
                    }
                    val t = teachers
                    subjects.add(
                        SubjectTeachers(
                            sp.child("subject_name").value.toString(),
                            t
                        )
                    )
                }
                setViews(subjects)
            }
        })
    }

    private fun setViews(subjects: MutableList<SubjectTeachers>) {

        if (subjects.isNotEmpty()){
            val adapter = SubjectsAdapter(subjects, this)
            rv_subjects.adapter = adapter
            rv_subjects.layoutManager = LinearLayoutManager(this)

            rv_subjects.visibility = View.VISIBLE
            progressBar.visibility = View.INVISIBLE
            bt_make_timetable.visibility = View.VISIBLE
        }
    }

    private fun createTimetableFromDetails(branch: String, year: String){
        var i=0

        myRef.child(year).child(branch).addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snaps in snapshot.children){
                    val prof = timetableSpecs[i].teacher
                    val sub = timetableSpecs[i].sub

                    for (sp in snaps.child("academic_group").child(timetableSpecs[i].group).children){
                        val day = sp.child("day_of_week").value.toString().toInt()
                        val startTime = sp.child("time").value.toString().toInt()
                        val type = sp.child("type").value.toString()
                        val endTime = if (type == "theory") startTime+1 else startTime+2

                        val a = Class(sub, prof, type, day, startTime, endTime)
                        //Log.d("TAG", "onDataChange: $a")
                        classDetails.add(a)
                    }
                    if(i < timetableSpecs.size-1){i++}
                }

                val timeTable = Gson().toJson(classDetails)
                Log.d("TAG", "onDataChange: $timeTable")
                val intent = Intent(applicationContext, MainActivity::class.java)
                val sharedPref = getSharedPreferences("timetable", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.apply{
                    putString("classes", timeTable)
                    apply()
                }
                startActivity(intent)
                finish()
            }
        })
    }
}