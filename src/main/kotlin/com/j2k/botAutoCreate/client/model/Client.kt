package com.j2k.botAutoCreate.client.model

import com.j2k.botAutoCreate.client.step.Step
import com.j2k.botAutoCreate.stepBuilder

data class Client(
    val chatId: String,
    var state: Step = stepBuilder.build(),
) {
    init {
        users[chatId] = this
    }

    companion object {
        val users: MutableMap<String, Client> = mutableMapOf()

        fun findByChatId(chatId: String): Client = users.getOrPut(chatId) { Client(chatId) }
    }
}
