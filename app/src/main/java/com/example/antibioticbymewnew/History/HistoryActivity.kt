package com.example.antibioticbymewnew.History

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.antibioticbymewnew.Domain.User
import com.example.antibioticbymewnew.Domain.UserObject
import com.example.antibioticbymewnew.Domain.GetCutPoint
import com.example.antibioticbymewnew.Domain.GetHistoryFromFireBase
import com.example.antibioticbymewnew.Provider_Patient.HomeProviderActivity
import com.example.antibioticbymewnew.Provider_Patient.HomePatientActivity
import com.example.antibioticbymewnew.R
import com.example.antibioticbymewnew.Regis_Login.LoginActivity
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_history.*

/**
 * Created by Smew on 6/12/2560.
 */

class HistoryActivity : AppCompatActivity() {

    lateinit var dataReference: DatabaseReference
    lateinit var msgList: MutableList<GetHistoryFromFireBase>

    private var date_list: MutableList<String> = mutableListOf()
    private var score_list: MutableList<String> = mutableListOf()

    var username = ""
    var type = ""
    var user: User = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        user = UserObject.getUser()
        username = user.name
        type = user.type


        // set toolbar
        setSupportActionBar(Title3)
        toolbar_title3.setText("ประวัติการทำแบบสอบถาม")
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val title = "การวินิจฉัยครั้งนี้เป็นของคุณ : $username"
        titlelistview.text = title

        // get history from firebase
        dataReference = FirebaseDatabase.getInstance().getReference("history").child(username)
        msgList = mutableListOf()
        dataReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(p0: DataSnapshot?) {
                if (p0!!.exists()) {
                    msgList.clear()
                    historyListView.adapter = null
                    for (i in p0.children) {
                        val message = i.getValue(GetHistoryFromFireBase::class.java)
                        msgList.add(message!!)
                        date_list.add(message.date_his)
                        score_list.add(message.result)
                    }
                    val adapter = MessageAdapter3(applicationContext, R.layout.row_history, username, type, msgList)
                    historyListView.adapter = adapter

                }
            }
        })

        // intent for show detail history
        historyListView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(baseContext, DetailHistoryActivity::class.java)
            intent.putExtra("username", username)
            intent.putExtra("date_his", date_list[position])
            intent.putExtra("score", score_list[position])
            intent.putExtra("type", type)
            startActivity(intent)
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
            if (type == "patient") {
                startActivity(Intent(this, HomePatientActivity::class.java))
                finish()
            }
            if (type == "provider") {
                startActivity(Intent(this, HomeProviderActivity::class.java))
                finish()
            }
            if (type == "staff(provider)") {
                startActivity(Intent(this, HomeProviderActivity::class.java))
                finish()
            }
            if (type == "staff(patient)") {
                startActivity(Intent(this, HomePatientActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun actionResourceClickHandler(item: MenuItem?) {
        Toast.makeText(this, "Signed out!", Toast.LENGTH_LONG).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    // set listview for show data
    class MessageAdapter3(val mContext: Context, val layoutResId: Int, val username: String, val type: String, val messageList: MutableList<GetHistoryFromFireBase>) : ArrayAdapter<GetHistoryFromFireBase>(mContext, layoutResId, messageList) {
        lateinit var dataReference: DatabaseReference
        private var result_list: MutableList<String> = mutableListOf()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val layoutInflator: LayoutInflater = LayoutInflater.from(mContext)
            val view: View = layoutInflator.inflate(layoutResId, null)

            val idTextView = view.findViewById<TextView>(R.id.hisIdTextView)
            val scoreTextView = view.findViewById<TextView>(R.id.resultScoreTextView)
            val resultTextView = view.findViewById<TextView>(R.id.resultTextView)
            val dateTextView = view.findViewById<TextView>(R.id.dateTextView)
            val deleteBtn = view.findViewById<ImageView>(R.id.deleteBtn)

            val msg = messageList[position]
            idTextView.text = (position + 1).toString()
            scoreTextView.text = "Result: " + msg.result
            dateTextView.text = msg.date_his

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
                            var _result = result_list[0].split(" ")
                            var __result = result_list[1].split(" ")
                            if (msg.result.toInt() <= 2) {
                                resultTextView.text = _result[0]
                            } else {
                                resultTextView.text = __result[0]
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
                            if (msg.result.toInt() > 3) {
                                resultTextView.text = result_list[1]
                            } else {
                                var _result = result_list[0].split(" ")
                                resultTextView.text = _result[0] + " " + _result[1]
                            }

                        }
                    }
                })
            }

            // delete data in firebase when click image
            dataReference = FirebaseDatabase.getInstance().getReference("history").child(username)
            deleteBtn.setOnClickListener {
                deleteBtn.animate().setDuration(1000).alpha(0F).withEndAction({
                    dataReference.child(msg.date_his).removeValue()
                    messageList.removeAt(position)
                    deleteBtn.setAlpha(1.0F)
                })
            }
            return view
        }
    }
}