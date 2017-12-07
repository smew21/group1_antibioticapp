package com.example.antibioticbymewnew.Regis_Login

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.example.antibioticbymewnew.Domain.UserObject
import com.example.antibioticbymewnew.Provider_Patient.HomePatientActivity
import com.example.antibioticbymewnew.Provider_Patient.HomeProviderActivity
import com.example.antibioticbymewnew.R
import com.example.antibioticbymewnew.Regis_Login.Database.AddregisToFirebase
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var dataReference: DatabaseReference
    lateinit var msgList: MutableList<AddregisToFirebase>

    private var username_list: MutableList<String> = mutableListOf()
    private var password_list: MutableList<String> = mutableListOf()
    private var position_list: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // intent for register
        btn_regis_login.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // get username and password from firebase
        dataReference = FirebaseDatabase.getInstance().getReference("addregis")
        msgList = mutableListOf()
        dataReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }
            override fun onDataChange(p0: DataSnapshot?) {
                if (p0!!.exists()) {
                    msgList.clear()
                    username_list.clear()
                    password_list.clear()
                    for (i in p0.children) {
                        val message = i.getValue(AddregisToFirebase::class.java)
                        msgList.add(message!!)
                        username_list.add(message.username)
                        password_list.add(message.password)
                        position_list.add(message.position)
                    }
                }
            }
        })

        // check username and password for sign-in
        btn_login_login.setOnClickListener {
            val username_edt: String = edt_username_login.text.toString()
            val password_edt: String = edt_password_login.text.toString()
            for ((index, value) in username_list.withIndex()) {
                if (username_edt in username_list[index] && password_edt.equals(password_list[index])) {
                    UserObject.getUser().name = username_edt
                    UserObject.getUser().type = position_list[index]
                    if (position_list[index].equals("patient")) {
                        Toast.makeText(baseContext, "Login success", Toast.LENGTH_SHORT).show()
                        val intent = Intent(baseContext, HomePatientActivity::class.java)
                        intent.putExtra("username", username_edt)
                        intent.putExtra("type", position_list[index])
                        startActivity(intent)
                    }
                    if (position_list[index].equals("provider")) {
                        Toast.makeText(baseContext, "Login success", Toast.LENGTH_SHORT).show()
                        val intent = Intent(baseContext, HomeProviderActivity::class.java)
                        intent.putExtra("username", username_edt)
                        intent.putExtra("type", position_list[index])
                        startActivity(intent)
                    }
                    if (position_list[index].equals("staff(patient)")) {
                        Toast.makeText(baseContext, "Login success", Toast.LENGTH_SHORT).show()
                        val intent = Intent(baseContext, HomePatientActivity::class.java)
                        intent.putExtra("username", username_edt)
                        intent.putExtra("type", "patient")
                        startActivity(intent)
                    }
                    if (position_list[index].equals("staff(provider)")) {
                        Toast.makeText(baseContext, "Login success", Toast.LENGTH_SHORT).show()
                        val intent = Intent(baseContext, HomeProviderActivity::class.java)
                        intent.putExtra("username", username_edt)
                        intent.putExtra("type", "provider")
                        startActivity(intent)
                    }
                }
                if (username_edt in username_list[index] && password_edt != password_list[index]) {
                    Toast.makeText(baseContext, "Login fail", Toast.LENGTH_SHORT).show()
                }
            }
            if (username_edt !in username_list || password_edt !in password_list) {
                Toast.makeText(baseContext, "Login fail", Toast.LENGTH_SHORT).show()
            }
            if (username_edt in username_list && password_edt !in password_list) {
                Toast.makeText(baseContext, "Login fail", Toast.LENGTH_SHORT).show()
            }
        }
    }

}