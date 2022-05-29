package com.j2k.botAutoCreate.step

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

class StepBuilder(
    override val message: String,
    override val parent: Step? = null,
    override val expected: EXPECTED = EXPECTED.CLICK,
) : StepAbstract<StepBuilder>() {
    override val keyboard: ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder = ReplyKeyboardMarkup.builder()
    override val children: MutableMap<String, StepBuilder> = mutableMapOf()

    fun addChild(reason: String = expected.key.orEmpty(), child: StepBuilder) {
        children[reason] = child
    }

    fun addButton(text: String, step: StepBuilder) {
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

        val buildChildren = children.mapValues { it.value.build() }

        return Step(
            parent, message, keyboard, expected, buildChildren.toMutableMap()
        )
    }

    companion object {
        fun loadFromJson(json: String): StepBuilder {
//            val gson = Gson()
//            val stepsData = gson.fromJson(json, StepsData::class.java)
//            print(stepsData)
            TODO("implement function for loading step builder from JSON file")
        }
    }
}
