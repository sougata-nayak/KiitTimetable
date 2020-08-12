package com.leodev.kiittimetable.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.leodev.kiittimetable.R
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        bt_register.setOnClickListener {
            et_email_register.visibility = View.INVISIBLE
            et_password_register.visibility = View.INVISIBLE
            bt_register.visibility = View.INVISIBLE
            progressBar2.visibility = View.VISIBLE
            registerUser()
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