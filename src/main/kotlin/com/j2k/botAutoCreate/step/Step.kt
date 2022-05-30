package com.j2k.botAutoCreate.step

import com.j2k.botAutoCreate.scriptCreator
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup

enum class EXPECTED(val key: String?) {
    PHOTO("/photo/"),
    TEXT("/text/"),
    CLICK(null);

    fun isExpected(message: Message): Boolean = when (this) {
        PHOTO -> message.hasPhoto()
        TEXT -> message.hasText()
        CLICK -> message.hasText() // clicking on the button sends the text
    }
}

class Step(
    override val parent: Step?,
    override val message: String,
    override val keyboard: ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder?,
    override val expected: EXPECTED, // "text" / "photo" / null - click to button
    override var children: MutableMap<String, Step> // "some text" / "/photo/" / "/text/"
) : StepAbstract<Step>(), StepInterface {

    private var waitResponse: Boolean = false

    private fun getChild(key: String): Step {
        return if (children.isEmpty())
            scriptCreator.createScript()
        else
            children.getOrElse(key) { throw Exception() }
    }

    override fun update(update: Update, messageBuilder: SendMessage.SendMessageBuilder): Step {
        when (waitResponse) {
            false -> {
                messageBuilder.apply {
                    if (keyboard != null) replyMarkup(keyboard.build())
                    chatId(update.message.chatId.toString())
                    text(message)
                }
                waitResponse = true
                return this
            }
            true -> {
                return if (expected.isExpected(update.message)) {
                    data = update.message
                    getChild(expected.key ?: update.message.text)
                        .update(update, messageBuilder)
                } else {
                    this
                }
            }
        }
    }

    override fun cancel(update: Update, messageBuilder: SendMessage.SendMessageBuilder): Step {
        parent!!.waitResponse = false
        parent.data = null
        data = null

        return parent.update(update, messageBuilder)
    }

    var data: Message? = null
}
