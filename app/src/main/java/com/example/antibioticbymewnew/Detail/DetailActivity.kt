package com.example.antibioticbymewnew.Detail

import android.content.Context
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.antibioticbymewnew.Domain.Question
import com.example.antibioticbymewnew.Domain.QuestionList
import com.example.antibioticbymewnew.Domain.GetCutPoint
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.row_main.view.*
import java.text.SimpleDateFormat
import java.util.*
import com.example.antibioticbymewnew.R

class DetailActivity : AppCompatActivity() {

    lateinit var dataReference: DatabaseReference
    lateinit var msgList: MutableList<Question>
    private var result_list: MutableList<String> = mutableListOf()

    var username = ""
    var result = ""
    var name = ""
    var type = ""
    var date_his = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        username = intent.getStringExtra("username")
        result = intent.getStringExtra("resultNumber")
        type = intent.getStringExtra("type")

        if (type.equals("provider")) {
            name = intent.getStringExtra("namePatient")
            nameText2.text = "คนไข้ : " + name
        }

        conclude_listView.adapter = myCustomAdapter(this)

        val date = Date()
        val dt = SimpleDateFormat("dd-MM-yyyy_kk:mm:ss")
        date_his = dt.format(date).toString()
        msgList = mutableListOf()

        // set toolbar
        setSupportActionBar(Title2)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //get result of each type from firebase database
        if (type == "patient") {
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
                            resultText2.text = result_list[0]
                        } else {
                            resultText2.text = result_list[1]
                        }

                    }
                }
            })
        }

        if (type == "provider") {
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
                            resultText2.text = result_list[1]
                        } else {
                            resultText2.text = result_list[0]
                        }
                    }
                }
            })
        }

        sumTextView.text = result

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

    private class myCustomAdapter(context: Context) : BaseAdapter() {
        private val mContext: Context
        val questionlist = QuestionList.getQuestion()

        init {
            mContext = context
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getItem(p0: Int): Any {
            return questionlist[p0]
        }

        override fun getCount(): Int {
            return questionlist.size
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            val rowMain: View

            if (p1 == null) {
                val layoutInfrator = LayoutInflater.from(p2!!.context)
                rowMain = layoutInfrator.inflate(R.layout.row_main, p2, false)
                val viewHolder = ViewHolder(rowMain.question_textView, rowMain.answerImageView)
                rowMain.tag = viewHolder
            } else {
                rowMain = p1
            }

            rowMain.setBackgroundColor(Color.WHITE)
            val viewHolder = rowMain.tag as ViewHolder

            viewHolder.nameTextView.text = questionlist.get(p0).question

            //set image
            if (questionlist.get(p0).ans == "yes") {
                viewHolder.rowImageView.setImageResource(R.drawable.tick)
            } else {
                viewHolder.rowImageView.setImageResource(R.drawable.wrong)
            }

            return rowMain
        }

        private class ViewHolder(val nameTextView: TextView, val rowImageView: ImageView)

    }
}
