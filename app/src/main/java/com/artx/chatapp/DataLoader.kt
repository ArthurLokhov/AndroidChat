package com.artx.chatapp

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.*
import java.util.ArrayList

class DataLoader {
    companion object {
        val users: ArrayList<String> = arrayListOf()

        suspend fun getFirebaseData(): ArrayList<String> = coroutineScope {
            val result = async {
                val reference = FirebaseDatabase.getInstance().reference
                reference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (user in snapshot.children) {
                            if (!users.contains(user.value.toString()))
                                users.add(user.value.toString())
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        return
                    }
                })

            }
            result.await()
            delay(100)
            return@coroutineScope users
        }

        fun createQuery(): DatabaseReference {
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
                    return ViewHolder(
                            LayoutInflater.from(parent.context)
                                .inflate(viewType, parent, false)
                        )
                }

                override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Message) {
                    holder.messageUser.text = model.userName
                    holder.messageText.text = model.textMessage
                    holder.messagePubDate.text =
                        DateFormat.format("dd-MM-yyyy HH:mm:ss", model.messageTime)
                }

                override fun getItemViewType(position: Int): Int {
                    CoroutineScope(Dispatchers.IO).launch {
                        getFirebaseData()
                        delay(100)
                    }
                    val user = users[position]
                    val userName = user.substringAfter("userName=").replace("}", "")
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(500)
                    }
                    return if (userName == FirebaseAuth.getInstance().currentUser?.email.toString()) {
                        R.layout.left_list_item
                    } else {
                        R.layout.right_list_item
                    }
                }
            }
        }
    }
}