package com.sattvainnovations.zingiegame20

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import com.sattvainnovations.zingiegame.StartGameCall
import com.sattvainnovations.zingiegame.callGame


class MainActivity : AppCompatActivity() {



    private var score : String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startButton = findViewById<Button>(R.id.startButton)
        val scoreDisplay = findViewById<TextView>(R.id.scoreDisplay)
        val refreshscore = findViewById<Button>(R.id.getScore)

        //init shared pref
        var scoredata = getSharedPreferences("Score", MODE_PRIVATE)
        score = scoredata.getString("Score","")


        startButton.setOnClickListener {
            val editor: SharedPreferences.Editor = scoredata.edit()
            editor.putString("Score", "")
            editor.commit()
            startActivity(StartGameCall().startGameActivity(application))
        }

        refreshscore.setOnClickListener {
            score = scoredata.getString("Score","")
            if (score == ""){
                scoreDisplay.setText("play")

            }
            else{
                scoreDisplay.setText(score)
                Toast.makeText(applicationContext,score,Toast.LENGTH_LONG).show()

            }

        }

    }

}