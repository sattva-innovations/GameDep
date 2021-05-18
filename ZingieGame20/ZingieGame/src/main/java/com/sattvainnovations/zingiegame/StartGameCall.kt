package com.sattvainnovations.zingiegame

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat.startActivity


class StartGameCall {

    //pass the hostId
    
    fun attachFragment(view:Context): Intent {
        val intent = Intent(view , callGame::class.java)
        startActivity(view,intent,null)
       return intent
    }

}