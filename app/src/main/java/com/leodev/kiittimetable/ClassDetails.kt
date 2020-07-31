package com.leodev.kiittimetable

import java.io.Serializable

class ClassDetails : Serializable {
    public lateinit var day_of_week : Any
    public lateinit var time : Any
    public lateinit var type: String

    constructor(){}

    constructor(day_of_week: Int, time: Int, type: String){
        this.day_of_week = day_of_week
        this.time = time
        this.type = type
    }
}