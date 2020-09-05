package com.leodev.kiittimetable.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.leodev.kiittimetable.R
import com.leodev.kiittimetable.Models.SubjectTeachers
import kotlinx.android.synthetic.main.item_subjects.view.*

class SubjectsAdapter(
    val subject_teachers: Array<SubjectTeachers?>,
    val context: Context
) : RecyclerView.Adapter<SubjectsAdapter.SubjectsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_subjects, parent, false)
        return SubjectsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return subject_teachers.size
    }

    override fun onBindViewHolder(holder: SubjectsViewHolder, position: Int) {
        val adapter = subject_teachers[position]?.teach?.let {
            ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item,
                it
            )
        }


        holder.itemView.apply {
            tv_subject_name.text = subject_teachers[position]?.sub
            sp_teacher.adapter = adapter
        }
    }

    inner class SubjectsViewHolder(view : View) : RecyclerView.ViewHolder(view)
}
