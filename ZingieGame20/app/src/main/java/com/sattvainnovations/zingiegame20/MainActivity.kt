package com.sattvainnovations.zingiegame20

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import com.sattvainnovations.zingiegame.StartGameCall
import com.sattvainnovations.zingiegame.callGame

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val startButton = findViewById<Button>(R.id.startButton)

        startButton.setOnClickListener {
            startActivity(StartGameCall().startGameActivity(application))
        }


    }

    override fun onRestart() {
        val scoreDisplay = findViewById<TextView>(R.id.scoreDisplay)
        var src = callGame()
        if (src == null){

        }
        else{
            scoreDisplay.setText(src.sendScore())
            Toast.makeText(applicationContext,src.sendScore(),Toast.LENGTH_LONG).show()
        }

        super.onRestart()
    }
}