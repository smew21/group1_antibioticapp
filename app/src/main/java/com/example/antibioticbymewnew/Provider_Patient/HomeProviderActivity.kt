package com.example.antibioticbymewnew.Provider_Patient

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.antibioticbymewnew.Domain.UserObject
import com.example.antibioticbymewnew.History.HistoryActivity
import com.example.antibioticbymewnew.R
import com.example.antibioticbymewnew.Regis_Login.LoginActivity

import kotlinx.android.synthetic.main.activity_home_provider.*

class HomeProviderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_provider)

        // set toolbar
        setSupportActionBar(toolbar_home_provider)
        tv_toolbar_home_provider.setText("สำหรับบุคลากรทางการแพทย์")
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        var username = ""
        var type = ""
        val user = UserObject.getUser()
        username = user.name
        type = user.type

        // intent to set name patient
        btn_gotoQ_provider.setOnClickListener {
            if (type.toLowerCase().equals("provider")||type.equals("staff(provider)")) {
                var intent2 = Intent(baseContext, ProfilePatientActivity::class.java)
                intent2.putExtra("username", username)
                intent2.putExtra("type", "provider")
                startActivity(intent2)
            }
        }

        // intent to history
        btn_gotoHis_pro.setOnClickListener {
            var intent2 = Intent(baseContext, HistoryActivity::class.java)
            intent2.putExtra("username", username)
            intent2.putExtra("type", "provider")
            startActivity(intent2)
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
        }
        return super.onOptionsItemSelected(item)
    }

    fun actionResourceClickHandler(item: MenuItem?) {
        Toast.makeText(this, "Signed out!", Toast.LENGTH_LONG).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

}