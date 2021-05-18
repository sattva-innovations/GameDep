package com.sattvainnovations.zingiegame

import androidx.fragment.app.FragmentManager

class StartGameCall {

    //pass the hostId
    
    fun attachFragment(hostId : Int){
        val fragment = DeliveryjumpFragment()
        val supportFragmentManager : FragmentManager ?= null
        val fragmentTransaction = supportFragmentManager?.beginTransaction()
        fragmentTransaction!!.replace(hostId,fragment)
            .addToBackStack(null)
            .commit()
    }

}