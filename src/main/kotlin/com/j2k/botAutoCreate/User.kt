package com.j2k.botAutoCreate

import com.j2k.botAutoCreate.exceptions.UserNotFoundException
import com.j2k.botAutoCreate.step.Step

data class User(
    val chatId: String,
    var state: Step,
) {
    init {
        users[chatId] = this
    }

    companion object {
        val users: MutableMap<String, User> = mutableMapOf()

        fun findByChatId(chatId: String): User = users.getOrElse(chatId) {
            users[chatId] = User(chatId, scriptCreator.createScript())
            users[chatId] ?: throw UserNotFoundException("user with chat id $chatId not found")
        }
    }
}
