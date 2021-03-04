package com.artx.chatapp

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

class DataLoader {
    companion object {
        fun createQuery(): Query {
            return FirebaseDatabase.getInstance().reference
        }

        fun setupOptions(query: Query): FirebaseRecyclerOptions<Message> {
            return FirebaseRecyclerOptions.Builder<Message>()
                .setQuery(query, Message::class.java)
                .build()
        }

        fun createRecyclerViewAdapter(options: FirebaseRecyclerOptions<Message>): FirebaseRecyclerAdapter<Message, ViewHolder> {
            return object : FirebaseRecyclerAdapter<Message, ViewHolder>(options) {

                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                    val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.left_list_item, parent, false)
                    return ViewHolder(itemView)
                }

                override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Message) {
                    holder.messageUser.text = model.userName
                    holder.messageText.text = model.textMessage
                    holder.messagePubDate.text =
                        DateFormat.format("dd-MM-yyyy HH:mm:ss", model.messageTime)
                }

            }
        }
    }
}