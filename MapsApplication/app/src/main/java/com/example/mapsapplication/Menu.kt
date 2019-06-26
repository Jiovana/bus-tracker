package com.example.mapsapplication

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.content.Intent
import android.support.multidex.MultiDexApplication


class Menu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu)
        val user = findViewById(R.id.button) as Button
        user.setOnClickListener {
            val user_it = Intent(this@Menu, User::class.java)
            startActivity(user_it)
        }
        val bus = findViewById(R.id.button2) as Button
        bus.setOnClickListener {
            val bus_it = Intent(this@Menu, Bus::class.java)
            startActivity(bus_it)
        }


    }
}