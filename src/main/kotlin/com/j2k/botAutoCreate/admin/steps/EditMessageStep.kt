package com.j2k.botAutoCreate.admin.steps

import com.j2k.botAutoCreate.model.User
import com.j2k.botAutoCreate.model.UserMode
import com.j2k.botAutoCreate.step.StepInterface
import com.j2k.botAutoCreate.stepBuilder
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

class EditMessageStep(override val id: Long) : StepInterface {
    private val keyboard = ReplyKeyboardMarkup(
        listOf(
            KeyboardRow().apply { add("Отмена") }
        )
    )
    private var waitResponse: Boolean = false

    private fun editMessage(update: Update) {
        TODO("implement a function for editing message in json")
    }

    override fun update(
        user: User,
        update: Update,
        messageBuilder: SendMessage.SendMessageBuilder
    ): StepInterface {
        if(waitResponse) {
            when(update.message.text) {
                "Отмена" -> cancel(user, update, messageBuilder)
                else -> editMessage(update)
            }
            return stepBuilder.build(userMode = UserMode.ADMIN)
                .searchNodeById(id)
                .update(user, update, messageBuilder)
        } else {
            messageBuilder.apply {
                replyMarkup(keyboard)
                chatId(update.message.chatId.toString())
                text(
                    "Для редактирования этого сообщения отправьте сообщение, " +
                            "которым вы хотите его заменить. (отличное от 'Отмена' и 'Назад')"
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
