package com.leodev.kiittimetable.Models

import java.io.Serializable

data class Class(
    val name: String? = null,
    val prof: String? = null,
    val place: String? = null,
    val day: Int? = null,
    val startTime: Int? = null,
    val endTime: Int? = null,
    val length: Int = (endTime ?: 0) - (startTime ?: 0)) : Serializable
