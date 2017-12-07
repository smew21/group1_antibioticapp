package com.example.antibioticbymewnew.Question

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.antibioticbymewnew.Conclude.ConcludeActivity
import com.example.antibioticbymewnew.Domain.Question
import com.example.antibioticbymewnew.Domain.QuestionList
import com.example.antibioticbymewnew.Domain.GetQFromFireBase
import com.example.antibioticbymewnew.R
import com.example.antibioticbymewnew.Regis_Login.LoginActivity
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_question.*

class QuestionActivity : AppCompatActivity() {

    var index = 1
    var caseResult = 0
    var username = ""
    var type = ""
    var name = ""

    var resultArray: ArrayList<String> = arrayListOf()
    private var question_list: ArrayList<String> = arrayListOf()
    private var yes_point_list: ArrayList<Int> = arrayListOf()
    private var no_point_list: ArrayList<Int> = arrayListOf()
    private var message: GetQFromFireBase? = GetQFromFireBase()
    lateinit var dataReference: DatabaseReference
    lateinit var msgList: MutableList<GetQFromFireBase>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        username = intent.getStringExtra("username")
        type = intent.getStringExtra("type")
        if (type == "patient") {
            namePatient.setText("")
        } else {
            name = intent.getStringExtra("namePatient")
            namePatient.setText("การวินิจฉัยครั้งนี้เป็นของ คุณ : $name")
        }

        setSupportActionBar(toolbar_Q)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //set color
        if (type == "patient") {
            dataReference = FirebaseDatabase.getInstance().getReference("questionPatientTable")
            typeText.setText("สำหรับผู้ป่วย-ประชาชน")
            namePatient.setVisibility(View.GONE)
        } else {
            dataReference = FirebaseDatabase.getInstance().getReference("questionProviderTable")
            linearLayout.setBackgroundColor(Color.rgb(64, 110, 143))
            toolbar_Q.setBackgroundColor(Color.rgb(102, 142, 179))
            typeText.setBackgroundColor(Color.rgb(102, 142, 179))
            namePatient.setBackgroundColor(Color.rgb(255, 255, 255))
            namePatient.setBackgroundColor(Color.alpha(3))
            typeText.setText("สำหรับบุคลากรทางการแพทย์")
        }

        // get question from firebase
        msgList = mutableListOf()
        dataReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }
            override fun onDataChange(p0: DataSnapshot?) {
                if (p0!!.exists()) {
                    msgList.clear()
                    for (i in p0.children) {
                        message = i.getValue(GetQFromFireBase::class.java)
                        msgList.add(message!!)
                        question_list.add(message!!.question)
                        yes_point_list.add(message!!.point_yes)
                        no_point_list.add(message!!.point_no)
                        reloadData(question_list[0])
                    }
                }
            }
        })

        yesButton.setOnClickListener {
            if (index < question_list.size) {
                resultArray.add("yes")
                caseResult = caseResult + yes_point_list[index - 1]
                val resId = resources.getIdentifier("n" + (index + 1).toString(),
                        "drawable",
                        packageName)
                numberImage.setImageResource(resId)
                resultsText.text = question_list[index]
                index++
            } else {
                resultArray.add("yes")
                caseResult = caseResult + yes_point_list[index - 1]
                for (i in 0..question_list.size - 1) {
                    val question = Question(question_list[i], resultArray[i])
                    QuestionList.getQuestion().add(question)
                }
                val intent = Intent(this, ConcludeActivity::class.java)
                intent.putExtra("username", username)
                intent.putExtra("type", type)
                intent.putExtra("namePatient", name)
                intent.putExtra("resultNumber", caseResult.toString())
                finish()
                startActivity(intent)
            }
        }
        noButton.setOnClickListener {
            if (index < question_list.size) {
                resultArray.add("no")
                caseResult = caseResult + no_point_list[index]
                val resId = resources.getIdentifier("n" + (index + 1).toString(),
                        "drawable",
                        packageName)
                numberImage.setImageResource(resId)
                resultsText.text = question_list[index]
                index++
            } else {
                resultArray.add("no")
                caseResult = caseResult + no_point_list[index - 1]

                if (!QuestionList.getQuestion().isEmpty()) {
                    QuestionList.getQuestion().clear()
                }

                for (i in 0..question_list.size - 1) {
                    val question = Question(question_list[i], resultArray[i])
                    QuestionList.getQuestion().add(question)
                }

                val intent = Intent(this, ConcludeActivity::class.java)
                intent.putExtra("username", username)
                intent.putExtra("type", type)
                intent.putExtra("namePatient", name)
                intent.putExtra("resultNumber", caseResult.toString())
                finish()
                startActivity(intent)
            }
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        if (id == R.id.action_logout) {
            return true
        } else {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun actionResourceClickHandler(item: MenuItem?) {
        Toast.makeText(this, "Signed out!", Toast.LENGTH_LONG).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    // set for reload data
    fun reloadData(question: String) {
        resultsText.text = question
    }

}

