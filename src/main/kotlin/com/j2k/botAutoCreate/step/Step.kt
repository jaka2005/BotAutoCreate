package com.j2k.botAutoCreate.step

import com.j2k.botAutoCreate.stepBuilder
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup

enum class Expected(val key: String?) {
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
    override val id: Long,
    override val parent: Step?,
    override val message: String,
    override val keyboard: ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder?,
    override val expected: Expected, // "text" / "photo" / null - click to button
    override var children: MutableMap<String, Step> // "some text" / "/photo/" / "/text/"
) : StepAbstract<Step>(), StepInterface {

    private var waitResponse: Boolean = false
    var data: Message? = null

    private fun getChild(key: String): Step {
        return if (children.isEmpty())
            stepBuilder.build()
        else
            children.getOrElse(key) { throw Exception() }
    }

    override fun update(update: Update, messageBuilder: SendMessage.SendMessageBuilder): Step {
        when (waitResponse) {
            false -> {
                // messageBuilder changes here and BotManager works with the changed messageBuilder
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

    fun searchNodeById(id: Long): Step? {
        if (this.id == id) return this

        var result: Step? = null
        children.values.forEach {
            if (it.id == id) return it
            result = it.searchNodeById(id)
        }
        return result
    }


    companion object {
        val steps = mutableMapOf<Long, Step>()
    }
}
