package com.j2k.botAutoCreate.step

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

data class StepsData(
    val reason: String,
    val steps: List<StepsData>,
    val text: String
)

class StepBuilder : StepAbstract<StepBuilder>() {
    override var message: String = ""
    override var parent: StepBuilder? = null
    override var expected: Expected = Expected.CLICK
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

    fun build(parent: Step? = null) : Step {
        if (parent != null) {
            keyboard.keyboardRow(
                KeyboardRow().apply{ add("Back") }
            )
        }

        val buildStep = Step(parent, message, keyboard, expected, mutableMapOf())
        val buildChildren = children.mapValues { it.value.build(buildStep) }
        buildStep.children = buildChildren.toMutableMap()

        return buildStep
    }

    companion object {
        fun loadSettingsFromData(data: StepsData, builder: StepBuilder): StepBuilder {
            return StepBuilder().apply {
                message = data.text
                data.steps.forEach { child ->
                    addButton(
                        child.reason,
                        loadSettingsFromData(
                            child, StepBuilder().apply { parent = this }
                        )
                    )
                }
            }
        }
    }
}
