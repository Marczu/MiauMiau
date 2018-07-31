package com.marcinmejner.miaumiau.utils


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.marcinmejner.miaumiau.R

class RemoveMessageFragmentDialog : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_remove_message_dialog, container, false)
        return view
    }

}
