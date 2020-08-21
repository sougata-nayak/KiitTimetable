package com.leodev.kiittimetable.Activities

import android.app.Activity
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
            hideUI()
            registerUser()
        }

        goToLoginPageText.setOnClickListener {
            finish()
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
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            val intent = Intent(this, SelectionActivity::class.java)
                            intent.flags =  Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
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
                Toast.makeText(this@RegisterActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun hideUI(){
        et_email_register.visibility = View.INVISIBLE
        et_password_register.visibility = View.INVISIBLE
        bt_register.visibility = View.INVISIBLE
        goToLoginPageText.visibility = View.INVISIBLE
        registerProgressBar.visibility = View.VISIBLE

    }

    fun showUI(){
        et_email_register.visibility = View.VISIBLE
        et_password_register.visibility = View.VISIBLE
        bt_register.visibility = View.VISIBLE
        goToLoginPageText.visibility = View.VISIBLE
        registerProgressBar.visibility = View.INVISIBLE
    }
}