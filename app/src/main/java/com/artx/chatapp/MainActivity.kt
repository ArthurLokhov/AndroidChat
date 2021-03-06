package com.artx.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val SIGN_IN_CODE: Int = 1
    }

    private lateinit var activityMain: RelativeLayout
    private lateinit var emojiconEditText: EmojiconEditText
    private lateinit var emojiButton: ImageView
    private lateinit var sendButton: ImageView
    private lateinit var emojIconActions: EmojIconActions

    private var adapter: FirebaseRecyclerAdapter<Message, ViewHolder>? = null


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_CODE) {
            if (resultCode == RESULT_OK) {
                Snackbar.make(activityMain, "Вы авторизованы", Snackbar.LENGTH_LONG).show()
                displayAllMessages()
            } else {
                Snackbar.make(activityMain, "Вы не авторизованы", Snackbar.LENGTH_LONG).show()
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        activityMain = findViewById(R.id.activity_main)
        emojiconEditText = findViewById(R.id.message_field)
        emojiButton = findViewById(R.id.emoji_btn)
        sendButton = findViewById(R.id.send_btn)
        emojIconActions =
            EmojIconActions(applicationContext, activityMain, emojiconEditText, emojiButton)
        emojIconActions.setUseSystemEmoji(true)
        emojIconActions.ShowEmojIcon()

        setListeners()

        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder().build(),
                SIGN_IN_CODE
            )
        } else {
            Snackbar.make(activityMain, "Вы авторизованы", Snackbar.LENGTH_LONG).show()
            displayAllMessages()
        }

    }

    override fun onStart() {
        super.onStart()
        if (adapter != null)
            adapter!!.startListening()
        adapter!!.notifyDataSetChanged()
    }

    override fun onStop() {
        super.onStop()
        if (adapter != null)
            adapter!!.stopListening()
    }

    private fun displayAllMessages() {

        val listOfMessages: RecyclerView = findViewById(R.id.message_list)
        val query: Query = DataLoader.createQuery()
        val options: FirebaseRecyclerOptions<Message> = DataLoader.setupOptions(query)

        CoroutineScope(Dispatchers.IO).launch {
            DataLoader.getFirebaseData()
            delay(100)
        }

        adapter = DataLoader.createRecyclerViewAdapter(options)
        adapter!!.startListening()
        listOfMessages.layoutManager = LinearLayoutManager(this)
        listOfMessages.adapter = adapter
        adapter!!.notifyDataSetChanged()
    }

    private fun setListeners() {
        sendButton.setOnClickListener {
            FirebaseDatabase.getInstance().reference
                .push()
                .setValue(
                    Message(
                        FirebaseAuth.getInstance().currentUser?.email,
                        emojiconEditText.text.toString()
                    )
                )
            emojiconEditText.setText("")
        }
    }
}