package com.j2k.botAutoCreate.client.model

import com.j2k.botAutoCreate.exceptions.UserNotFoundException
import com.j2k.botAutoCreate.client.step.Step
import com.j2k.botAutoCreate.stepBuilder

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
            users[chatId] = User(chatId, stepBuilder.build())
            users[chatId] ?: throw UserNotFoundException("user with chat id $chatId not found")
        }
    }
}
