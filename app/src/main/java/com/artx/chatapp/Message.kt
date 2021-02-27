package com.artx.chatapp

import java.util.*

class Message {
    var userName: String? = null
    var textMessage: String? = null
    var messageTime: Long = 0

    constructor() {}
    constructor(userName: String?, textMessage: String?) {
        this.userName = userName
        this.textMessage = textMessage
        messageTime = Date().time
    }
}