package com.j2k.botAutoCreate.step

import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup

abstract class StepAbstract<T : StepAbstract<T>> {
    abstract val parent: T?
    abstract val message: String
    abstract val expected: Expected
    abstract val keyboard: ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder?
    abstract val children: MutableMap<String, T>
}

interface StepInterface {
    fun update(
        update: Update,
        messageBuilder: SendMessage.SendMessageBuilder
    ): StepInterface

    fun cancel(
        update: Update,
        messageBuilder: SendMessage.SendMessageBuilder
    ): StepInterface
}