package com.j2k.botAutoCreate.admin.steps

import com.j2k.botAutoCreate.model.User
import com.j2k.botAutoCreate.model.UserMode
import com.j2k.botAutoCreate.step.StepInterface
import com.j2k.botAutoCreate.stepBuilder
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

class AddButtonStep(override val id: Long) : StepInterface {
    enum class State {
        START, GET_BUTTON_TEXT, GET_MESSAGE
    }

    private var state: State = State.START
    private var buttonText: String? = null
    private var message: String? = null // TODO: change to message and get photo and text from this
    private val keyboard = ReplyKeyboardMarkup(
        listOf(
            KeyboardRow().apply { add("Отмена") }
        )
    )

    private fun addButton(buttonText: String, message: String) {
        TODO("implement a function for adding the button in json")
    }

    override fun update(
        user: User,
        update: Update,
        messageBuilder: SendMessage.SendMessageBuilder
    ): StepInterface {
        return when(state) {
            State.START -> {
                messageBuilder.apply {
                    replyMarkup(keyboard)
                    chatId(update.message.chatId.toString())
                    text(
                        "Введите текст кнопки"
                    )
                }
                state = State.GET_BUTTON_TEXT
                this
            }
            State.GET_BUTTON_TEXT -> {
                buttonText = update.message.text
                messageBuilder.apply {
                    replyMarkup(keyboard)
                    chatId(update.message.chatId.toString())
                    text(
                        "Отлично!\nТеперь введите теекст сообщения, " +
                                "который будет присылаться, после нажатия на эту кнопку"
                    )
                }
                state = State.GET_MESSAGE
                this
            }
            State.GET_MESSAGE -> {
                message = update.message.text
                addButton(buttonText!!, message!!)

                stepBuilder.build(userMode = UserMode.ADMIN)
                    .searchNodeById(id)
                    .update(user, update, messageBuilder)
            }
        }
    }

    override fun cancel(
        user: User,
        update: Update,
        messageBuilder: SendMessage.SendMessageBuilder
    ): StepInterface =
        stepBuilder.build(userMode = UserMode.ADMIN).searchNodeById(id)
}
