package com.j2k.botAutoCreate.admin.steps

import com.j2k.botAutoCreate.step.Expected
import com.j2k.botAutoCreate.step.Step
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
                    add("edit message")
                    add("add button")
                }
            )
            .keyboardRow(
                KeyboardRow().apply { add("delete message") }
            )
    }
}