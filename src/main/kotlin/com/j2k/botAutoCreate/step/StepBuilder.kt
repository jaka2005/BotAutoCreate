package com.j2k.botAutoCreate.step

import com.j2k.botAutoCreate.admin.steps.AdminStep
import com.j2k.botAutoCreate.json.StepsData
import com.j2k.botAutoCreate.model.UserMode
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

class StepBuilder : StepAbstract<StepBuilder>() {
    var id: Long = 0

    override var message: String = ""
    override var parent: StepBuilder? = null
    override var expected: Expected = Expected.CLICK
    override val keyboard: ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder = ReplyKeyboardMarkup.builder()
    override val children: MutableMap<String, StepBuilder> = mutableMapOf()

    fun addChild(reason: String = expected.key.orEmpty(), child: StepBuilder) {
        children[reason] = child
        child.parent = this
    }

    fun addButton(text: String, step: StepBuilder) {
        keyboard.keyboardRow(
            KeyboardRow().apply{ add(text) }
        )
        addChild(text, step)
    }

    fun build(userMode: UserMode = UserMode.USER, parent: Step? = null) : Step {
        if (parent != null) {
            keyboard.keyboardRow(
                KeyboardRow().apply{ add("Назад") }
            )
        }

        // magic happens here, I don't know how it works without a reference
        val buildStep = when(userMode) {
            UserMode.USER ->
                Step(id, parent, message, keyboard, expected, mutableMapOf())
            UserMode.ADMIN ->
                AdminStep(id, parent, message, keyboard, expected, mutableMapOf())
        }

        val buildChildren = children.mapValues { it.value.build(parent = buildStep) }
        buildStep.children = buildChildren.toMutableMap()

        return buildStep
    }

    companion object {
        fun loadSettingsFromData(data: StepsData, builder: StepBuilder): StepBuilder {
            return StepBuilder().apply {
                id = data.id
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
