package com.leodev.kiittimetable.Models

import com.leodev.kiittimetable.Models.AcademicGroup
import java.io.Serializable

class Subjects : Serializable{
    public lateinit var subject_name: String
    public lateinit var academic_group: AcademicGroup

    constructor(){}
    constructor(subject_name: String, academic_group: AcademicGroup){
        this.subject_name = subject_name
        this.academic_group = academic_group
    }
}