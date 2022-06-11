package com.j2k.botAutoCreate.step

import com.j2k.botAutoCreate.exceptions.StepNotFoundException
import com.j2k.botAutoCreate.model.States
import com.j2k.botAutoCreate.model.User
import com.j2k.botAutoCreate.stepBuilder
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup

enum class Expected(val key: String?) {
    PHOTO("/photo/") {
        override fun getStringData(message: Message): String {
            TODO("Not yet implemented")
        }
    },
    TEXT("/text/") {
        override fun getStringData(message: Message): String = message.text
    },
    CLICK(null) {
        override fun getStringData(message: Message): String = message.text
    };

    abstract fun getStringData(message: Message): String

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

    override fun update(user: User, update: Update, messageBuilder: SendMessage.SendMessageBuilder): Step {
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

                    transaction {
                        States.insert { state ->
                            state[stepId] = this@Step.id
                            state[data] = expected.getStringData(this@Step.data!!)
                            state[this.user] = user.id
                        }
                    }

                    getChild(expected.key ?: update.message.text)
                        .update(user, update, messageBuilder)
                } else {
                    this
                }
            }
        }
    }

    override fun cancel(user: User, update: Update, messageBuilder: SendMessage.SendMessageBuilder): Step {
        parent!!.waitResponse = false
        parent.data = null
        data = null

        transaction {
            States.deleteWhere { States.user.eq(user.id) and States.stepId.eq(this@Step.id) }
        }

        return parent.update(user, update, messageBuilder)
    }

    fun searchNodeById(id: Long): Step {
        if (this.id == id) return this

        var result: Step? = null
        children.values.forEach {
            if (it.id == id) return it
            result = it.searchNodeById(id)
        }

        return result ?: throw StepNotFoundException("Step with id \"$id\" not found ")
    }
}
