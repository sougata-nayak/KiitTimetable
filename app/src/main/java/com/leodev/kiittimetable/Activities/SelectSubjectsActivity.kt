package com.leodev.kiittimetable.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.leodev.kiittimetable.Adapters.SubjectsAdapter
import com.leodev.kiittimetable.Models.Class
import com.leodev.kiittimetable.R
import com.leodev.kiittimetable.SubjectTeachers
import com.leodev.kiittimetable.Subjects
import com.leodev.kiittimetable.TimetableSpecs
import kotlinx.android.synthetic.main.activity_select_subjects.*
import kotlinx.android.synthetic.main.item_subjects.view.*

class SelectSubjectsActivity : AppCompatActivity() {

    val database = Firebase.database
    val myRef = database.getReference("constant")

    var subjects : ArrayList<SubjectTeachers> = arrayListOf()
    var teachers : ArrayList<String> = arrayListOf()

    val timetableSpecs: ArrayList<TimetableSpecs> = arrayListOf()

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
            for(i in 0 until subjects.size){
                val view = rv_subjects.getChildAt(i)
                val radioButton : RadioButton = findViewById(view.rg_group.checkedRadioButtonId)

                val sub = view.tv_subject_name.toString()
                val group = radioButton.text.toString()
                val teacher = view.sp_teacher.selectedItem.toString()
                timetableSpecs.add(TimetableSpecs(sub, group, teacher))
            }

            Log.d("TAG", "onCreate: $timetableSpecs")
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
                    subjects.add(SubjectTeachers(sp.child("subject_name").value.toString(), teachers))
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

    fun createTimetableFromDetails(){

        val timeTable = mutableListOf<Class>()

        val jsonString = Util.getJsonFromAssets(applicationContext, "cse2.json")
        val subjectsList : ArrayList<Subjects> = Gson().fromJson(
            jsonString,
            object : TypeToken<ArrayList<Subjects>>() {}.type
        )

//        for (subjects in subjectsList ){
//            Log.d("timetableAction", "SubjectName: ${subjects.subject_name}")
//            for (sub in subjects.academic_group.G1){
//                Log.d("timetableAction", "G1: $sub \n")
//            }
//            for (sub in subjects.academic_group.G2){
//                Log.d("timetableAction", "G2: $sub \n")
//            }
//        }
    }
}
