package com.leodev.kiittimetable.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.leodev.kiittimetable.Fragments.DayFragment
import com.leodev.kiittimetable.Models.Class

class DaysPagerAdapter(val timetable: ArrayList<Class>, fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment = DayFragment.new(position + 1, timetable)

    override fun getCount(): Int = 5


    override fun getPageTitle(position: Int): CharSequence {
        when (position) {
            0 -> return "Monday"
            1 -> return "Tuesday"
            2 -> return "Wednesday"
            3 -> return "Thursday"
            4 -> return "Friday"
            else -> return "?"
        }
    }
}