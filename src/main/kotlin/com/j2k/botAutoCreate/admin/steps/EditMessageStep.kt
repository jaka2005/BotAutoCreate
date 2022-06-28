package com.j2k.botAutoCreate.admin.steps

import com.j2k.botAutoCreate.json.ScriptManager
import com.j2k.botAutoCreate.model.User
import com.j2k.botAutoCreate.model.UserMode
import com.j2k.botAutoCreate.step.StepInterface
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

    private fun editMessage(
        user: User,
        update: Update,
        messageBuilder: SendMessage.SendMessageBuilder
    ): StepInterface { // TODO: change to message and get photo and text from this
        ScriptManager.editStep(id, update.message.text)

        return ScriptManager.builder.build(userMode = UserMode.ADMIN)
            .searchNodeById(id)
            .update(user, update, messageBuilder)
    }

    override fun update(
        user: User,
        update: Update,
        messageBuilder: SendMessage.SendMessageBuilder
    ): StepInterface {
        if(waitResponse) {
            return when(update.message.text) {
                "Отмена" -> cancel(user, update, messageBuilder)
                else -> editMessage(user, update, messageBuilder)
            }
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
        ScriptManager.builder.build(userMode = UserMode.ADMIN).searchNodeById(id)

}
