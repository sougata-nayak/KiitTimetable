package com.leodev.kiittimetable.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.leodev.kiittimetable.R
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        setupUI(findViewById(R.id.loginView))

        bt_login.setOnClickListener {
            loginUser()
        }

        tv_register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
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
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    checkLoggedInState()
                }.addOnFailureListener {
                    Toast.makeText(this@LoginActivity, "Login Failed, Retry", Toast.LENGTH_LONG).show()
                }
            }
            catch (e: Exception) {
                Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkLoggedInState() {
        if (auth.currentUser != null) { // logged in
            val intent = Intent(this, MainActivity::class.java)
            intent.flags =  Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}