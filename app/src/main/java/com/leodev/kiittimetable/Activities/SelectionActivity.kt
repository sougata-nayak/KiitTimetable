package com.leodev.kiittimetable.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
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
            startActivity(Intent(this, SelectSubjectsActivity::class.java).apply {
                putExtra("year", radioButton.text.subSequence(0, 1).toString().toInt())
                putExtra("branch", sp_branch.selectedItem.toString())
            })
        }
    }
}