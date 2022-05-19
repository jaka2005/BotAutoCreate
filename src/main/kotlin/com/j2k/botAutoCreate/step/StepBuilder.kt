package com.j2k.botAutoCreate.step

import com.j2k.botAutoCreate.scriptCreator
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

class StepBuilder(
    override val message: String,
    override val parent: Step? = null,
    override val expected: EXPECTED = EXPECTED.CLICK,
) : StepAbstract() {
    override val keyboard: ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder = ReplyKeyboardMarkup.builder()
    override val children: MutableMap<String, Step> = mutableMapOf()

    fun addChild(reason: String = expected.key.orEmpty(), child: Step) {
        children[reason] = child
    }

    fun addButton(text: String, step: Step) {
        keyboard.keyboardRow(
            KeyboardRow().apply{ add(text) }
        )
        addChild(text, step)
    }

    fun build() : Step {
        if (parent != null) {
            keyboard.keyboardRow(
                KeyboardRow().apply{ add("Back") }
            )
        }
        return Step(
            parent, message, keyboard, expected, children
        )
    }
}