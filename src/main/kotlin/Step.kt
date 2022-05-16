import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

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
    private val parent: Step? = null,
    private val message: String,
    private val expected: EXPECTED = EXPECTED.CLICK, // "text" / "photo" / null - click to button
) {
    private val keyboard = ReplyKeyboardMarkup.builder()
    private val children: MutableMap<String, Step> = mutableMapOf() // "some text" / "/photo/" / "/text/"
    private var waitResponse: Boolean = false

    private fun addChild(reason: String, child: Step) {
        children[reason] = child
    }

    private fun getChild(key: String): Step {
        return if (children.isEmpty())
            scriptCreator.createScript()
        else
            children.getOrElse(key) { throw Exception() }
    }

    init {
        if (parent != null) {
            keyboard.keyboardRow(
                KeyboardRow().apply { add("back") }
            )
        }
    }

    operator fun set(reason: String, child: Step) {
        addChild(reason, child)
    }

    fun update(update: Update, messageBuilder: SendMessage.SendMessageBuilder): Step {
        when (waitResponse) {
            false -> {
                messageBuilder.apply {
                    replyMarkup(keyboard.build())
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

    fun cancel(update: Update, messageBuilder: SendMessage.SendMessageBuilder) {
        parent!!.waitResponse = false
        parent.data = null
        data = null

        parent.update(update, messageBuilder)
    }

    var data: Message? = null
}
