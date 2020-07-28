package com.leodev.kiittimetable.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import com.leodev.kiittimetable.Adapters.SubjectsAdapter
import com.leodev.kiittimetable.R
import kotlinx.android.synthetic.main.activity_selection.*
import com.leodev.kiittimetable.SubjectTeachers


class SelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection)

//        sp_branch.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//            }
//
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//            }
//
//        }

        showSubjectsList()
    }

    fun showSubjectsList(){
        val subjectTeachersList = listOf<SubjectTeachers>(
            SubjectTeachers("SE", listOf("varsha1", "varsha2", "varsha3")),
            SubjectTeachers("CN", listOf("varsha4", "varsha5", "varsha6")),
            SubjectTeachers("AI", listOf("varsha1", "varsha2", "varsha3")),
            SubjectTeachers("DMDW", listOf("varsha4", "varsha5", "varsha6")),
            SubjectTeachers("DMDW", listOf("varsha4", "varsha5", "varsha6")),
            SubjectTeachers("DMDW", listOf("varsha4", "varsha5", "varsha6")),
            SubjectTeachers("DMDW", listOf("varsha4", "varsha5", "varsha6"))
        )

        val adapter = SubjectsAdapter(subjectTeachersList, this)
        rv_subjects.adapter = adapter
        rv_subjects.layoutManager = LinearLayoutManager(this)
    }
}