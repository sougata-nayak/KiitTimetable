package com.leodev.kiittimetable.Fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.leodev.kiittimetable.Adapters.ClassesAdapter
import com.leodev.kiittimetable.Models.Class
import com.leodev.kiittimetable.R
import kotlinx.android.synthetic.main.fragment_day.*


class DayFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_day, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val day = arguments?.getInt(ARG_DAY)
        val timetable = arguments?.getSerializable(ARG_TIMETABLE)
        val sharedPrefs = this.activity?.getSharedPreferences("zoom", Context.MODE_PRIVATE)

        if (day == null || timetable == null) return

        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = ClassesAdapter(
            day,
            timetable as List<Class>,
            sharedPrefs,
            activity?.applicationContext
        )
    }

    override fun onStart() {
        super.onStart()
        val day = arguments?.getInt(ARG_DAY)
        val timetable = arguments?.getSerializable(ARG_TIMETABLE)
        val sharedPrefs = this.activity?.getSharedPreferences("zoom", Context.MODE_PRIVATE)

        if (day == null || timetable == null) return

        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = ClassesAdapter(
            day,
            timetable as List<Class>,
            sharedPrefs,
            activity?.applicationContext
        )
    }

    companion object {
        val ARG_DAY = "day"
        val ARG_TIMETABLE = "timetable"

        fun new(day: Int, timetable: ArrayList<Class>): DayFragment {
            val fragment = DayFragment()
            val arguments = Bundle()
            arguments.putInt(ARG_DAY, day)
            arguments.putSerializable(ARG_TIMETABLE, timetable)
            fragment.arguments = arguments
            return fragment
        }
    }
}