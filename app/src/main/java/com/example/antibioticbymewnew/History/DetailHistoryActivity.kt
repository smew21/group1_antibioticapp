package com.example.antibioticbymewnew.History

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.antibioticbymewnew.Domain.GetCutPoint
import com.example.antibioticbymewnew.Domain.GetResultFromFirebase
import com.example.antibioticbymewnew.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_detail_history.*

class DetailHistoryActivity : AppCompatActivity() {

    lateinit var dataReference: DatabaseReference
    lateinit var msgList: MutableList<GetResultFromFirebase>
    private var result_list: MutableList<String> = mutableListOf()

    private var username: String? = ""
    private var date_his: String? = ""
    private var score: String? = ""
    private var type: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_history)

        // set toolbar
        setSupportActionBar(Title2_detail_his)
        patient_conclude_toolbar_detail_his.setText("ประวัติย้อนหลัง")
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        username = intent.getStringExtra("username")
        date_his = intent.getStringExtra("date_his")
        score = intent.getStringExtra("score")
        type = intent.getStringExtra("type")

        tv_score.text = "Total: $score"

        // get history from firebase by date
        dataReference = FirebaseDatabase.getInstance().getReference("history").child(username).child(date_his)
                .child("question")
        msgList = mutableListOf()
        msgList.clear()
        dataReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }
            override fun onDataChange(p0: DataSnapshot?) {
                if (p0!!.exists()) {
                    msgList.clear()
                    for (i in p0.children) {
                        val message = i.getValue(GetResultFromFirebase::class.java)
                        msgList.add(message!!)
                    }
                    val adapter = MessageAdapter2(applicationContext, R.layout.row_his_detail, msgList)
                    lv_detail_his.adapter = null
                    lv_detail_his.adapter = adapter
                }
            }
        })

        // get cutpoint from firebase
        if (type=="patient"||type=="staff(patient)")
        {
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
                        if (score.toString().toInt() <= 2) {
                            tv_result.text = result_list[0]
                        } else {
                            tv_result.text = result_list[1]
                        }

                    }
                }
            })
        }

        // get cutpoint from firebase
        if (type=="provider"||type=="staff(provider)")
        {
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
                        if (score.toString().toInt() >3) {
                            tv_result.text = result_list[1]
                        } else {
                            tv_result.text = result_list[0]
                        }
                    }
                }
            })
        }



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

    class MessageAdapter2(val mContext: Context, val layoutResId: Int, val messageList: MutableList<GetResultFromFirebase>) : ArrayAdapter<GetResultFromFirebase>(mContext, layoutResId, messageList) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            val layoutInflator: LayoutInflater = LayoutInflater.from(mContext)
            val view: View = layoutInflator.inflate(layoutResId, null)

            val tv_question = view.findViewById<TextView>(R.id.question_textView_hisde)
            val img = view.findViewById<ImageView>(R.id.answerImageView_hisde)
            val msg = messageList[position]

            tv_question.text = msg.question
            if (msg.ans == "yes") {
                img.setImageResource(R.drawable.tick)
            } else {
                img.setImageResource(R.drawable.wrong)
            }

            return view
        }
    }
}