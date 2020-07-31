package com.leodev.kiittimetable.Models

import java.io.Serializable

class Class : Serializable {
    val name: String? = null
    val prof: String? = null
    val place: String? = null
    val day: Int? = null
    val startTime: Int? = null
    val endTime: Int? = null

    val length: Int
        get() = (endTime ?: 0) - (startTime ?: 0)
}