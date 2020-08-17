package com.leodev.kiittimetable.Activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.leodev.kiittimetable.R
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        setupUI(findViewById(R.id.registerView))

        bt_register.setOnClickListener {
            et_email_register.visibility = View.INVISIBLE
            et_password_register.visibility = View.INVISIBLE
            bt_register.visibility = View.INVISIBLE
            progressBar2.visibility = View.VISIBLE
            registerUser()
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

    private fun registerUser() {
        val email = et_email_register.text.toString()
        val password = et_password_register.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            try {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    checkLoggedInState()
                }
            }
            catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkLoggedInState() {
        if (auth.currentUser != null) { // logged in
            startActivity(Intent(this, SelectionActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        checkLoggedInState()
    }
}