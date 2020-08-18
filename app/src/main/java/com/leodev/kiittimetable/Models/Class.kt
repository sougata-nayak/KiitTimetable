package com.leodev.kiittimetable.Models

import java.io.Serializable

data class Class(
    val name: String? = null,
    val prof: String? = null,
    val place: String? = null,
    val day: Int? = null,
    val startTime: Int? = null,
    val endTime: Int? = null,
    val length: Int = (endTime ?: 0) - (startTime ?: 0)) : Serializable, Comparator<Class> {

    override fun compare(p0: Class?, p1: Class?): Int {
        val t1 = p0?.startTime
        val t2 = p1?.startTime
        val d = t1?.minus(t2!!)
        return d!!
    }
}
