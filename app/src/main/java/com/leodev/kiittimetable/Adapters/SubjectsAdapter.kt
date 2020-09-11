package com.leodev.kiittimetable.Adapters

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.leodev.kiittimetable.Models.SubjectTeachers
import com.leodev.kiittimetable.R
import kotlinx.android.synthetic.main.item_subjects.view.*


class SubjectsAdapter(
    val subject_teachers: Array<SubjectTeachers?>,
    val context: Context,
    val groupSharedPrefs: SharedPreferences,
    val teacherSharedPrefs: SharedPreferences
) : RecyclerView.Adapter<SubjectsAdapter.SubjectsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_subjects,
            parent,
            false
        )
        return SubjectsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return subject_teachers.size
    }

    override fun onBindViewHolder(holder: SubjectsViewHolder, position: Int) {
        val adapter = subject_teachers[position]?.teach?.let {
            ArrayAdapter<String>(
                context, R.layout.support_simple_spinner_dropdown_item,
                it
            )
        }

        val sc = subject_teachers[position]?.sub?.let { getSubjectCode(it) }

        holder.itemView.apply {
            tv_subject_name.text = subject_teachers[position]?.sub
            sp_teacher.adapter = adapter
        }

        groupSharedPrefs.edit().apply{
            putInt(sc, holder.itemView.rg_group.checkedRadioButtonId)
            apply()
        }

        holder.itemView.rg_group.setOnCheckedChangeListener { radioGroup, i ->
            val group = radioGroup.checkedRadioButtonId
            groupSharedPrefs.edit().apply {
                putInt(sc, group)
                apply()
            }
            if (sc != null) {
                showDetails(sc)
            }
        }

        holder.itemView.sp_teacher.onItemSelectedListener = object: OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selectedItem = p0?.getItemAtPosition(p2).toString()
                teacherSharedPrefs.edit().apply {
                    putString(sc, selectedItem)
                    apply()
                }
                if (sc != null) {
                    showDetails(sc)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    private fun showDetails(sc: String){
        val g = groupSharedPrefs.getInt(sc, 0)
        val t = teacherSharedPrefs.getString(sc, null)
        Log.d("TAG", "showDetails: $sc -> $t and $g")
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

    inner class SubjectsViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
