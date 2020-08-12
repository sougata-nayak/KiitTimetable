package com.leodev.kiittimetable.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.leodev.kiittimetable.R
import kotlinx.android.synthetic.main.activity_selection.*

class SelectionActivity : AppCompatActivity() {

    lateinit var radioButton: RadioButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection)

        bt_save_specs.setOnClickListener {
            radioButton = findViewById(rg_year.checkedRadioButtonId)

            if(radioButton.text.subSequence(0, 1).toString().toInt() == 3 && sp_branch.selectedItem.toString() == "CSE") {

                startActivity(Intent(this, SelectSubjectsActivity::class.java).apply {
                    putExtra("year", radioButton.text.subSequence(0, 1).toString().toInt())
                    putExtra("branch", sp_branch.selectedItem.toString())
                })
            }
            else{
                Toast.makeText(this, "Only for 3rd Year CSE", Toast.LENGTH_LONG).show()
            }
        }
    }
}