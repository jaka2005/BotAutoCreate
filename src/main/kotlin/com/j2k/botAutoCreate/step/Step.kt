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
        override fun isExpected(message: Message): Boolean = message.hasPhoto()

        override fun getStringData(message: Message): String {
            TODO("implement in next life")
        }
    },
    TEXT("/text/") {
        override fun isExpected(message: Message): Boolean = message.hasText()
        override fun getStringData(message: Message): String = message.text
    },
    CLICK(null) {
        override fun isExpected(message: Message): Boolean = message.hasText()
        override fun getStringData(message: Message): String = message.text
    };

    abstract fun isExpected(message: Message): Boolean
    abstract fun getStringData(message: Message): String
}

open class Step(
    final override val id: Long,
    final override val parent: Step?,
    final override val message: String,
    final override val keyboard: ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder,
    final override val expected: Expected,
    final override var children: MutableMap<String, Step>
) : StepAbstract<Step>(), StepInterface {

    private var waitResponse: Boolean = false
    var data: Message? = null

    private fun getChild(key: String): Step {
        return if (children.isEmpty())
            stepBuilder.build()
        else
            children.getOrElse(key) {
                throw StepNotFoundException("Step with reason '$key' not found")
            }
    }

    override fun update(
        user: User,
        update: Update,
        messageBuilder: SendMessage.SendMessageBuilder
    ): Step {
        if (waitResponse) {
            return if (expected.isExpected(update.message)) {
                data = update.message

                transaction {
                    States.insert { state ->
                        state[step_id] = this@Step.id
                        state[data] = expected.getStringData(this@Step.data!!)
                        state[this.user] = user.id
                    }
                }

                getChild(expected.key ?: update.message.text)
                    .update(user, update, messageBuilder)

            } else {
                this
            }
        } else {
            user.stepId = id

            // messageBuilder changes here and BotManager works with the changed messageBuilder
            messageBuilder.apply {
                replyMarkup(keyboard.build())
                chatId(update.message.chatId.toString())
                text(message)
            }
            waitResponse = true
            return this
        }

    }

    override fun cancel(
        user: User,
        update: Update,
        messageBuilder: SendMessage.SendMessageBuilder
    ): Step {
        // clear this step
        waitResponse = false
        data = null
        // clear previous step
        parent!!.waitResponse = false
        parent.data = null

        transaction {
            States.deleteWhere { States.user.eq(user.id) and States.step_id.eq(this@Step.id) }
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
