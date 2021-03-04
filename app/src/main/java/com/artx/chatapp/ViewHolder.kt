package com.artx.chatapp

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.library.bubbleview.BubbleTextView

class ViewHolder(v: View?): RecyclerView.ViewHolder(v!!) {
    val messageUser: TextView = v!!.findViewById(R.id.message_user)
    val messagePubDate: TextView = v!!.findViewById(R.id.message_pub_time)
    val messageText: BubbleTextView = v!!.findViewById(R.id.message_text)
}