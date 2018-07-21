package com.marcinmejner.miaumiau.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.marcinmejner.miaumiau.R
import com.marcinmejner.miaumiau.models.ChatMessage
import com.marcinmejner.miaumiau.utils.UniversalImageLoader
import kotlinx.android.synthetic.main.chat_message_item.view.*

class MainChatRecyclerAdapter(val messageList: ArrayList<ChatMessage>, val context: Context) : RecyclerView.Adapter<MainChatRecyclerAdapter.ViewHolder>() {
    private val TAG = "MainChatRecyclerAdapter"

    var uImageLoader: UniversalImageLoader

    init {
        uImageLoader = UniversalImageLoader(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.chat_message_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        uImageLoader.setImage(messageList[position].profile_photo, holder.profileImage, "")
        holder.username.text = messageList[position].username
        holder.dateCreated.text = messageList[position].date_created
        holder.chatMessage.text = messageList[position].chat_message
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImage = view.main_chat_image
        val username = view.chat_username
        val dateCreated = view.chat_timestamp
        val chatMessage = view.chat_message
    }
}