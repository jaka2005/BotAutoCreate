package com.j2k.botAutoCreate.admin.steps

import com.j2k.botAutoCreate.model.User
import com.j2k.botAutoCreate.step.Expected
import com.j2k.botAutoCreate.step.Step
import com.j2k.botAutoCreate.step.StepInterface
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

class AdminStep(
    id: Long,
    parent: Step?,
    message: String,
    keyboard: ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder,
    expected: Expected,
    children: MutableMap<String, Step>
) : Step(id, parent, message, keyboard, expected, children) {
    init {
        keyboard
            .keyboardRow(
                KeyboardRow().apply {
                    add("Добавить кнопку")
                }
            )
            .keyboardRow(
                KeyboardRow().apply {
                    add("Изменить сообщение")
                }
            )
            .keyboardRow(
                KeyboardRow().apply {
                    if(parent != null) add("Удалить сообщение")
                }
            )
    }

    override fun update(
        user: User,
        update: Update,
        messageBuilder: SendMessage.SendMessageBuilder
    ): StepInterface {
        return when(update.message.text) {
            "Добавить кнопку" -> AddButtonStep(id).update(user, update, messageBuilder)
            "Изменить сообщение" -> EditMessageStep(id).update(user, update, messageBuilder)
            "Удалить сообщение" -> {
                if(parent != null) DeleteMessageStep(id).update(user, update, messageBuilder)
                else super.update(user, update, messageBuilder)
            }
            else -> super.update(user, update, messageBuilder)
        }
    }
}