package edu.put.inf151917

import android.app.Application
import android.content.Intent

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val dataAccess = DataAccess(applicationContext)
        if (dataAccess != null) {
            val intent = Intent(this, GamesActivity::class.java)
            startActivity(intent)
        }else
        {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }



    }
}