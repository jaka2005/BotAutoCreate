package com.j2k.botAutoCreate.model

import com.j2k.botAutoCreate.step.Step
import com.j2k.botAutoCreate.exceptions.AccessErrorException
import com.j2k.botAutoCreate.stepBuilder

class User(
    val chatId: String,
    var state: Step = stepBuilder.build(),
) {
    private var mode: UserMode = UserMode.USER
        set(value) {
            if(isAdmin) {
                field = value
            } else {
                throw AccessErrorException("only administrators can change the user mode")
            }
        }
    private var isAdmin: Boolean = false
        get() {
            field = onAdminChat()
            return field
        }

    init {
        users[chatId] = this
    }

    private fun onAdminChat(): Boolean {
        TODO("implemented a function to check the user's presence in the admin chat")
    }

    companion object {
        private val users: MutableMap<String, User> = mutableMapOf()

        fun findByChatId(chatId: String): User = users.getOrPut(chatId) { User(chatId) }
    }
}

enum class UserMode {
    ADMIN, USER
}