package com.marcinmejner.miaumiau.utils


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.marcinmejner.miaumiau.R
import kotlinx.android.synthetic.main.fragment_remove_message_dialog.view.*

class RemoveMessageFragmentDialog : Fragment() {
    private val TAG = "RemoveMessageFragmentDi"

    lateinit var messageId: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView: removeMessageFragment started")

        val view = inflater.inflate(R.layout.fragment_remove_message_dialog, container, false)

        val bundle = this.arguments
        if (bundle != null) {
            messageId = bundle.getString("messageId", "0000001")
            Log.d(TAG, "onCreateView: messageId : $messageId")
            removeMessageDialog(view)
        }




        return view
    }

    private fun removeMessageDialog(view: View) {
        view.yes_btn.setOnClickListener {

            val query = FirebaseDatabase.getInstance().reference
                    .child(context?.getString(R.string.dbname_messages))
                    .orderByKey()
                    .equalTo(messageId)

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                }

                override fun onDataChange(p0: DataSnapshot?) {
                    p0?.children?.forEach { it.ref.removeValue() }
                    Toast.makeText(context, "Wiadomość została usunięta", Toast.LENGTH_LONG)
                            .show()
                    activity?.supportFragmentManager?.popBackStack()
                }
            })
        }

        view.no_btn.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
    }

}
