package com.example.antibioticbymewnew.Conclude

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.antibioticbymewnew.Detail.DetailActivity
import com.example.antibioticbymewnew.Domain.QuestionList
import com.example.antibioticbymewnew.Domain.GetCutPoint
import com.example.antibioticbymewnew.Provider_Patient.HomePatientActivity
import com.example.antibioticbymewnew.Provider_Patient.HomeProviderActivity
import com.example.antibioticbymewnew.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_conclude.*
import java.text.SimpleDateFormat
import java.util.*

class ConcludeActivity : AppCompatActivity() {

    var username = ""
    var result = ""
    var name = ""
    var type = ""
    var date_his = ""

    lateinit var dataReference: DatabaseReference
    private var result_list: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conclude)

        username = intent.getStringExtra("username")
        result = intent.getStringExtra("resultNumber")
        type = intent.getStringExtra("type")

        val date = Date()
        val dt = SimpleDateFormat("dd-MM-yyyy_kk:mm:ss")
        date_his = dt.format(date).toString()


        if (type.equals("provider")) {
            name = intent.getStringExtra("namePatient")
            nameText.setText("การวินิจฉัยครั้งนี้เป็นของ คุณ : $name")
        } else {
            resultTypeText.text = "สำหรับผู้ป่วย - ประชาชน"
            nameText.setVisibility(View.GONE)
        }


        dataReference = FirebaseDatabase.getInstance().getReference("history").child(username).child(date_his)
        SaveData()
        numberResultText.text = intent.getStringExtra("resultNumber")

        // get cutpoint from firebase
        if (type == "patient" || type == "staff(patient)") {
            dataReference = FirebaseDatabase.getInstance().getReference("cutPointPatient")
            dataReference.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                }

                override fun onDataChange(p0: DataSnapshot?) {
                    if (p0!!.exists()) {
                        result_list.clear()
                        for (i in p0.children) {
                            val message = i.getValue(GetCutPoint::class.java)
                            result_list.add(message!!.answer)
                        }
                        if (result.toInt() <= 2) {
                            detailResult.text = result_list[0]
                        } else {
                            detailResult.text = result_list[1]
                        }

                    }
                }
            })
        }

        // get cutpoint from firebase
        if (type == "provider" || type == "staff(provider)") {
            dataReference = FirebaseDatabase.getInstance().getReference("cutPointProvider")
            dataReference.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                }

                override fun onDataChange(p0: DataSnapshot?) {
                    if (p0!!.exists()) {
                        result_list.clear()
                        for (i in p0.children) {
                            val message = i.getValue(GetCutPoint::class.java)
                            result_list.add(message!!.answer)
                        }
                        if (result.toInt() > 3) {
                            detailResult.text = result_list[1]
                        } else {
                            detailResult.text = result_list[0]
                        }

                    }
                }
            })
        }
        tv_date.text = dt.format(date).toString()

        homeButton.setOnClickListener {
            if (!QuestionList.getQuestion().isEmpty()) {
                QuestionList.getQuestion().clear()
            }
            if (type.equals("provider")) {
                val intent1 = Intent(this, HomeProviderActivity::class.java)
                startActivity(intent1)
                finish()
            }
            if (type.equals("patient")) {
                val intent2 = Intent(this, HomePatientActivity::class.java)
                startActivity(intent2)
                finish()
            }

        }

        // intent for show detail
        detailButton.setOnClickListener {
            val intent3 = Intent(this, DetailActivity::class.java)
            intent3.putExtra("username", username)
            intent3.putExtra("namePatient", name)
            intent3.putExtra("type", type)
            intent3.putExtra("resultNumber", result)
            startActivity(intent3)
        }

    }

    // save data to firebase
    private fun SaveData() {
        val questionlist = QuestionList.getQuestion()
        dataReference.child("result").setValue(result)
        dataReference.child("date_his").setValue(date_his)
        if (type == "provider") {
            dataReference.child("patient_name").setValue(name)
        }
        dataReference.child("question").setValue(questionlist).addOnCompleteListener {
            // tell user if it complete
            Toast.makeText(applicationContext, "successfully", Toast.LENGTH_SHORT)
        }
    }

}