package com.j2k.botAutoCreate.admin.steps

import com.j2k.botAutoCreate.model.User
import com.j2k.botAutoCreate.model.UserMode
import com.j2k.botAutoCreate.step.StepInterface
import com.j2k.botAutoCreate.stepBuilder
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

class DeleteMessageStep(override val id: Long) : StepInterface {
    private val keyboard = ReplyKeyboardMarkup(
        listOf(
            KeyboardRow().apply { add("Принять") },
            KeyboardRow().apply { add("Отменить") }
        )
    )

    private var waitResponse: Boolean = false

    private fun deleteMessage(stepId: Long) {
        TODO("implement a function for deleting message from json")
    }

    override fun update(
        user: User,
        update: Update,
        messageBuilder: SendMessage.SendMessageBuilder
    ): StepInterface {
        if (waitResponse) {
            when(update.message.text) {
                "Принять" -> deleteMessage(id)
                "Отменить" -> cancel(user, update, messageBuilder)
            }
            return stepBuilder.build(userMode = UserMode.ADMIN)
                .update(user, update, messageBuilder)
        } else {
            messageBuilder.apply {
                replyMarkup(keyboard)
                chatId(update.message.chatId.toString())
                text(
                    "Удалив это сообщение вы удалите всю ветку со всеми исходящими из него сообщениями" +
                            "\n Вы дейсвительно хотите удалить это сообщение?"
                )
            }
            waitResponse = true
            return this
        }
    }

    override fun cancel(
        user: User,
        update: Update,
        messageBuilder: SendMessage.SendMessageBuilder
    ): StepInterface =
        stepBuilder.build(userMode = UserMode.ADMIN).searchNodeById(id)
}
