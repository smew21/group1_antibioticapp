package com.example.antibioticbymewnew.Regis_Login

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.antibioticbymewnew.R
import com.example.antibioticbymewnew.Regis_Login.Database.AddregisToFirebase
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private var username: String = ""
    private var password: String = ""
    private var name: String = ""
    private var position: String = ""

    private var username_list: MutableList<String> = mutableListOf()

    lateinit var dataReference: DatabaseReference
    lateinit var msgList: MutableList<AddregisToFirebase>

    private val position_spinner = arrayOf("patient", "provider", "staff(patient)", "staff(provider)")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // click button for cancel register
        btn_cancel_regis.setOnClickListener {
            finish()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // set spinner for set position (patient provider staff(patient) staff(provider))
        val spinner = spinPosition
        val adapter = ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, position_spinner)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.setAdapter(adapter)

        // register and save data to firebase
        btn_regis_regis.setOnClickListener {
            name = edt_name_regis.text.toString()
            username = edt_username_regis.text.toString().toLowerCase()
            password = edt_password_regis.text.toString()
            position = spinPosition.selectedItem.toString()
            if (password == "") {
                Toast.makeText(baseContext, "Please enter your password", Toast.LENGTH_SHORT).show()
            }
            if (username == "") {
                Toast.makeText(baseContext, "Please enter your username", Toast.LENGTH_SHORT).show()
            }
            if (name == "") {
                Toast.makeText(baseContext, "Please enter your name", Toast.LENGTH_SHORT).show()
            }
            if (username in username_list) {
                Toast.makeText(baseContext, "Have Username in Database Try again!!", Toast.LENGTH_SHORT).show()
            }
            if (username.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()) {
                if (username in username_list) {
                    Toast.makeText(baseContext, "Have Username in Database Try again!!", Toast.LENGTH_SHORT).show()
                } else {
                    saveData()
                    finish()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            }

        }

        // get username from firebase for check username is unique
        dataReference = FirebaseDatabase.getInstance().getReference("addregis")
        msgList = mutableListOf()
        dataReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }
            override fun onDataChange(p0: DataSnapshot?) {
                if (p0!!.exists()) {
                    msgList.clear()
                    for (i in p0.children) {
                        val message = i.getValue(AddregisToFirebase::class.java)
                        msgList.add(message!!)
                        username_list.add(message.username)
                    }
                }
            }
        })
    }

    // save data to firebase
    private fun saveData() {
        val messageId = dataReference.push().key
        val journalEntry1 = AddregisToFirebase(username, password, name, position)
        dataReference.child(messageId).setValue(journalEntry1).addOnCompleteListener {
            Toast.makeText(applicationContext, "Message saved successfully", Toast.LENGTH_SHORT).show()
        }
    }

}