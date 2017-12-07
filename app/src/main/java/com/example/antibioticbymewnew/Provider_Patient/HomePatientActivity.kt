package com.example.antibioticbymewnew.Provider_Patient

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.antibioticbymewnew.Domain.UserObject
import com.example.antibioticbymewnew.History.HistoryActivity
import com.example.antibioticbymewnew.Question.QuestionActivity
import com.example.antibioticbymewnew.R
import com.example.antibioticbymewnew.Regis_Login.LoginActivity
import kotlinx.android.synthetic.main.activity_home_patient.*

class HomePatientActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_patient)

        // set title for toolbar
        setSupportActionBar(toolbar_home)
        tv_toolbar_home.setText("สำหรับผู้ป่วย-ประชาชน")
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        var username = ""
        var type = ""
        val user = UserObject.getUser()
        username = user.name
        type = user.type

        // go to questionnaire
        btn_gotoQ_patient.setOnClickListener {
            if (type.equals("patient")) {
                var intent2 = Intent(baseContext, QuestionActivity::class.java)
                intent2.putExtra("username", username)
                intent2.putExtra("type", "patient")
                startActivity(intent2)
            }
            if (type.equals("staff(patient)")) {
                var intent2 = Intent(baseContext, QuestionActivity::class.java)
                intent2.putExtra("username", username)
                intent2.putExtra("type", "patient")
                startActivity(intent2)
            }
        }

        // go to history
        btn_gotoHis_pa.setOnClickListener {
            var intent2 = Intent(baseContext, HistoryActivity::class.java)
            intent2.putExtra("username", username)
            intent2.putExtra("type", "patient")
            startActivity(intent2)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // click gear to sign-out
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        if (id == R.id.action_logout) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun actionResourceClickHandler(item: MenuItem?) {
        Toast.makeText(this, "Signed out!", Toast.LENGTH_LONG).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

}
