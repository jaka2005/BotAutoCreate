package com.j2k.botAutoCreate.admin.steps

import com.j2k.botAutoCreate.exceptions.UnexpectedResponseException
import com.j2k.botAutoCreate.json.ScriptManager
import com.j2k.botAutoCreate.model.User
import com.j2k.botAutoCreate.model.UserMode
import com.j2k.botAutoCreate.step.StepInterface
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

    private fun deleteMessage(
        user: User,
        update: Update,
        messageBuilder: SendMessage.SendMessageBuilder
    ): StepInterface {
        val previousStepId = ScriptManager.deleteStep(id)

        return ScriptManager.builder.build(userMode = UserMode.ADMIN)
            .searchNodeById(previousStepId)
            .update(user, update, messageBuilder)
    }

    override fun update(
        user: User,
        update: Update,
        messageBuilder: SendMessage.SendMessageBuilder
    ): StepInterface {
        if (waitResponse) {
            return when(update.message.text) {
                "Принять" -> deleteMessage(user, update, messageBuilder)
                "Отменить" -> cancel(user, update, messageBuilder)
                else -> throw UnexpectedResponseException(update.message.text)
            }

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
        ScriptManager.builder.build(userMode = UserMode.ADMIN).searchNodeById(id)
}
