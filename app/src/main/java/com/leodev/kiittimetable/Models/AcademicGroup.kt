package com.leodev.kiittimetable.Models

import java.io.Serializable

class AcademicGroup : Serializable{
    public lateinit var G1: ArrayList<ClassDetails>
    public lateinit var G2: ArrayList<ClassDetails>

    constructor(){}
    constructor(G1: ArrayList<ClassDetails>, G2:ArrayList<ClassDetails>){
        this.G1 = G1
        this.G2 = G2
    }
}