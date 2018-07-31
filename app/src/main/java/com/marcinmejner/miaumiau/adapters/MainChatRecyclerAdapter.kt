package com.marcinmejner.miaumiau.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.marcinmejner.miaumiau.R
import com.marcinmejner.miaumiau.models.ChatMessage
import com.marcinmejner.miaumiau.utils.UniversalImageLoader
import kotlinx.android.synthetic.main.chat_message_item.view.*

class MainChatRecyclerAdapter(val messageList: ArrayList<ChatMessage>, val context: Context) : RecyclerView.Adapter<MainChatRecyclerAdapter.ViewHolder>() {
    private val TAG = "MainChatRecyclerAdapter"

    var uImageLoader: UniversalImageLoader
    private val mAuth: FirebaseAuth
    var currentUser: String?

    init {
        uImageLoader = UniversalImageLoader(context)
        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth.currentUser?.uid
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.chat_message_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val userId = messageList[position].user_id
        currentUser = mAuth.currentUser?.uid
        deleteMessage(holder, position)

        /*Display chat message for current user*/
        if (userId != currentUser) {
            showCurrentUser(holder)
            uImageLoader.setImage(messageList[position].profile_photo, holder.profileImage, "")
            holder.username.text = messageList[position].username
            holder.dateCreated.text = messageList[position].date_created
            holder.chatMessage.text = messageList[position].chat_message
        }
        if (userId == currentUser) {
            showOtherUser(holder)
            uImageLoader.setImage(messageList[position].profile_photo, holder.profileImageLeft, "")
            holder.usernameLeft.text = messageList[position].username
            holder.dateCreatedLeft.text = messageList[position].date_created
            holder.chatMessageLeft.text = messageList[position].chat_message
        }


    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        /*Current user widgets*/
        val profileImage = view.main_chat_image
        val username = view.chat_username
        val dateCreated = view.chat_timestamp
        val chatMessage = view.chat_message
        val deleteMessage = view.relLayout_right_user

        /*Other User Widgets*/
        val profileImageLeft = view.main_chat_image_right_user
        val usernameLeft = view.chat_username_right_user
        val dateCreatedLeft = view.chat_timestamp_right_user
        val chatMessageLeft = view.chat_message_right_user
    }

    private fun showCurrentUser(holder: ViewHolder) {
        holder.profileImage.visibility = View.VISIBLE
        holder.username.visibility = View.VISIBLE
        holder.dateCreated.visibility = View.VISIBLE
        holder.chatMessage.visibility = View.VISIBLE

        holder.profileImageLeft.visibility = View.GONE
        holder.usernameLeft.visibility = View.GONE
        holder.dateCreatedLeft.visibility = View.GONE
        holder.chatMessageLeft.visibility = View.GONE
    }

    private fun showOtherUser(holder: ViewHolder) {
        holder.profileImageLeft.visibility = View.VISIBLE
        holder.usernameLeft.visibility = View.VISIBLE
        holder.dateCreatedLeft.visibility = View.VISIBLE
        holder.chatMessageLeft.visibility = View.VISIBLE

        holder.profileImage.visibility = View.GONE
        holder.username.visibility = View.GONE
        holder.dateCreated.visibility = View.GONE
        holder.chatMessage.visibility = View.GONE
    }

    fun deleteMessage(holder: ViewHolder, position: Int) {
        holder.deleteMessage.setOnClickListener {
            Log.d(TAG, "deleteMessage: clicked")
            Log.d(TAG, "deleteMessage: ${messageList[position].message_id}")
            val query = FirebaseDatabase.getInstance().reference
                    .child(context.getString(R.string.dbname_messages))
                    .orderByKey()
                    .equalTo(messageList[position].message_id)


            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {

                }

                override fun onDataChange(p0: DataSnapshot?) {
                    p0?.children?.forEach { it.ref.removeValue() }
                }
            })

        }

    }
}