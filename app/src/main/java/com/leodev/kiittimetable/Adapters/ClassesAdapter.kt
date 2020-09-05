package com.leodev.kiittimetable.Adapters

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.leodev.kiittimetable.Models.Class
import com.leodev.kiittimetable.R
import com.leodev.kiittimetable.Utils.TimeUtils
import kotlinx.android.synthetic.main.item_class.view.*

class ClassesAdapter(val day: Int, classes: List<Class>,val sharedPrefs: SharedPreferences?, val context: Context?) : RecyclerView.Adapter<ClassesAdapter.ViewHolder>() {

    val dataset: List<Class> = classes.filter { it.day == day }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_class, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataset[position]
        val subject = item.name.toString()
        Log.d("TAG", "onBindViewHolder: $subject")
        val code = getSubjectCode(subject)
        Log.d("TAG", "onBindViewHolder: $code")
        val link = sharedPrefs?.getString(code, null)
        Log.d("TAG", "onBindViewHolder: $link")

        holder.view.name.text = item.name ?: "?"
        holder.view.prof.text = item.prof ?: "?"
        holder.view.place.text = item.place ?: "?"
        holder.view.startTime.text =
            TimeUtils.getDisplayableTime(
                item.startTime ?: -1
            )
        holder.view.endTime.text =
            TimeUtils.getDisplayableTime(
                item.endTime ?: -1
            )

        holder.view.directZoom.setOnClickListener {
            if(link != null){
                try {
                    val intent = Intent().setAction(Intent.ACTION_VIEW)
                    intent.addCategory(Intent.CATEGORY_BROWSABLE)
                    intent.setData(Uri.parse(link))
                    it.context.startActivity(intent)
                }
                catch (e: Exception){
                    Toast.makeText(context, "Please re-check your link", Toast.LENGTH_LONG).show()
                }
            }
        }

        // Increase item height for longer classes
        if (item.length > 1) {
            holder.view.layoutParams.height =
                (holder.view.context.resources.getDimension(R.dimen.class_item_height) * 2).toInt()
        }
    }

    override fun getItemCount(): Int = dataset.size

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

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