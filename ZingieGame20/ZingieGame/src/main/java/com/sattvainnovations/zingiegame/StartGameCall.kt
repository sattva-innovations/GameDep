package com.sattvainnovations.zingiegame

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity


class StartGameCall {

    //pass the hostId
    
    fun startGameActivity(view: Context): Intent {
        val intent = Intent(view, callGame::class.java)
       return intent
    }




}