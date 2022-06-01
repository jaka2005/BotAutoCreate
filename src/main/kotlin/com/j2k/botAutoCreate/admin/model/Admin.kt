package com.j2k.botAutoCreate.admin.model

import com.j2k.botAutoCreate.client.step.Step
import com.j2k.botAutoCreate.exceptions.UserNotFoundException
import com.j2k.botAutoCreate.stepBuilder

data class Admin(
    val chatId: String,
    var state: Step = stepBuilder.build(),
    var mode: AdminMode = AdminMode.ADMIN
) {
    init {
        admins[chatId] = this
    }

    companion object {
        val admins: MutableMap<String, Admin> = mutableMapOf()

        fun findByChatId(chatId: String): Admin = admins[chatId]
            ?: throw UserNotFoundException("Admin with chat id '$chatId' not found")
    }
}

enum class AdminMode {
    ADMIN, USER
}
