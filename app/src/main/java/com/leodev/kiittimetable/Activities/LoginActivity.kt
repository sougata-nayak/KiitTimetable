package com.leodev.kiittimetable.Activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.leodev.kiittimetable.ForgotPasswordActivity
import com.leodev.kiittimetable.Models.Class
import com.leodev.kiittimetable.Models.TimetableSpecs
import com.leodev.kiittimetable.R
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    val database = Firebase.database
    val myRef = database.getReference("const")
    val db = database.getReference("users")

    val timetableSpecs: MutableList<TimetableSpecs> = arrayListOf()
    val classDetails: MutableList<Class> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        setupUI(findViewById(R.id.loginView))

        bt_login.setOnClickListener {
            hideUI()
            loginUser()
        }

        tv_register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        tv_forgot_password.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    fun setupUI(view: View) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener(fun(_: View, _: MotionEvent): Boolean {
                hideSoftKeyboard()
                return false
            })
        }

        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(innerView)
            }
        }
    }


    private fun Activity.hideSoftKeyboard() {
        currentFocus?.let {
            val inputMethodManager = ContextCompat.getSystemService(this, InputMethodManager::class.java)!!
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }


    private fun loginUser() {
        val email = et_email_login.text.toString()
        val password = et_password_login.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {

                            val em = email.substring(0, email.length-4)
                            db.child(em).addValueEventListener(object : ValueEventListener{
                                override fun onCancelled(error: DatabaseError) {}

                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val branch = snapshot.child("branch").value.toString()
                                    val year = snapshot.child("year").value.toString()
                                    for(items in snapshot.child("timetable").children){
                                        val group = items.child("group").value.toString()
                                        val sub = items.child("sub").value.toString()
                                        val teacher = items.child("teacher").value.toString()
                                        timetableSpecs.add(TimetableSpecs(sub, group, teacher))
                                    }
                                    if(timetableSpecs.isEmpty()){
                                        val intent = Intent(this@LoginActivity, SelectionActivity::class.java)
                                        intent.flags =  Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                        finish()
                                    }
                                    else{
                                        createTimetable(branch, year, timetableSpecs)
                                    }
                                }

                            })

                        } else {
                            // If sign in fails, display a message to the user.
                            showUI()
                            Toast.makeText(baseContext, "${task.exception?.message}",
                                Toast.LENGTH_LONG).show()
                        }
                    }
            }
            catch (e: Exception) {
                showUI()
                Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun createTimetable(
        branch: String,
        year: String,
        timetableSpecs: MutableList<TimetableSpecs>
    ){
        var i=0
        myRef.child(year).child(branch).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                classDetails.clear()
                for (snaps in snapshot.children){
                    val prof = timetableSpecs[i].teacher
                    val sub = timetableSpecs[i].sub

                    for (sp in snaps.child("academic_group").child(timetableSpecs[i].group).children){
                        val day = sp.child("day_of_week").value.toString().toInt()
                        val startTime = sp.child("time").value.toString().toInt()
                        val type = sp.child("type").value.toString().capitalize()
                        val endTime = if (type == "Theory") startTime+1 else startTime+2

                        val a = Class(sub, prof, type, day, startTime, endTime)
                        classDetails.add(a)
                    }
                    if(i < timetableSpecs.size-1){i++}
                }

                classDetails.sortWith(kotlin.Comparator { p0, p1 -> (p0?.startTime?.minus((p1?.startTime)!!))!! })

                val timeTable = Gson().toJson(classDetails)
                val sharedPref = getSharedPreferences("timetable", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.apply{
                    putString("classes", timeTable)
                    apply()
                }
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.flags =  Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        })
    }

    fun hideUI(){
        bt_login.visibility = View.INVISIBLE
        et_email_login.visibility = View.INVISIBLE
        et_password_login.visibility = View.INVISIBLE
        tv_forgot_password.visibility = View.INVISIBLE
        tv_register.visibility = View.INVISIBLE
        loginProgressBar.visibility = View.VISIBLE
    }

    fun showUI(){
        bt_login.visibility = View.VISIBLE
        et_email_login.visibility = View.VISIBLE
        et_password_login.visibility = View.VISIBLE
        tv_forgot_password.visibility = View.VISIBLE
        tv_register.visibility = View.VISIBLE
        loginProgressBar.visibility = View.GONE
    }
}