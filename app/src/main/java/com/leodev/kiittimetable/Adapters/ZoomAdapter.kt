package com.leodev.kiittimetable.Adapters

import android.content.SharedPreferences
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.leodev.kiittimetable.R
import kotlinx.android.synthetic.main.item_zoom_links.view.*

class ZoomAdapter(val subjects: ArrayList<String>, val zoomSharedPrefs: SharedPreferences, val linksArray: MutableMap<String, String?>)
    : RecyclerView.Adapter<ZoomAdapter.ZoomViewHolder>()
{

    inner class ZoomViewHolder(view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ZoomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_zoom_links, parent, false)
        return ZoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: ZoomViewHolder, position: Int) {

        val sc = getSubjectCode(subjects[position])
        val t = zoomSharedPrefs.getString(sc, null)

        holder.itemView.tv_zoom_subject_name.text = subjects[position]
        if(t != null){
            holder.itemView.zoom_link.setText(t)
            linksArray[sc] = t
        }

        holder.itemView.zoom_link.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if(p0 != null){
                    linksArray[sc] = p0.toString()
                    zoomSharedPrefs.edit().apply{
                        putString(sc, linksArray[sc])
                        apply()
                    }
                }
                Log.d("TAG", "afterTextChanged: $linksArray")
            }
        })

    }

    override fun getItemCount(): Int {
        return subjects.size
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