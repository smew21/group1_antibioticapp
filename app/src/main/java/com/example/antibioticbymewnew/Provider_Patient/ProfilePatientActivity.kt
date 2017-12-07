package com.example.antibioticbymewnew.Provider_Patient

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.antibioticbymewnew.Question.QuestionActivity
import com.example.antibioticbymewnew.R
import kotlinx.android.synthetic.main.activity_profile_patient.*

class ProfilePatientActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_patient)

        val type = intent.getStringExtra("type").toLowerCase()
        val username = intent.getStringExtra("username")

        // set name of patient
        nameButton.setOnClickListener{
            if (edt_profile.text.toString().isEmpty())
            {
                Toast.makeText(baseContext,"Please fill the name patient",Toast.LENGTH_SHORT).show()
            }
            else{
                val intent = Intent(this, QuestionActivity::class.java)
                intent.putExtra("namePatient", edt_profile.text.toString())
                intent.putExtra("type", type)
                intent.putExtra("username", username)
                startActivity(intent)
                finish()
            }
        }
    }
}
