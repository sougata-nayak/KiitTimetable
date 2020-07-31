package com.leodev.kiittimetable.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.leodev.kiittimetable.R
import kotlinx.android.synthetic.main.activity_login.*
class LoginActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        bt_login.setOnClickListener {
            loginUser()
        }

        tv_register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser() {
        val email = et_email_login.text.toString()
        val password = et_password_login.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            try {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    checkLoggedInState()
                }
            }
            catch (e: Exception) {
                Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkLoggedInState() {
        if (auth.currentUser != null) { // logged in
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        checkLoggedInState()
    }
}