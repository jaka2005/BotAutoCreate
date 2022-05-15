import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.PhotoSize
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

class Step<T>(
    private val parent: Step<*>? = null,
    private val message: String,
    private val keyboard: ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder = ReplyKeyboardMarkup.builder(),
    private val expected: String? = null, // "text" / "photo" / "media" / null - click to button
) {
    private val children: MutableMap<String, Step<*>> = mutableMapOf() // "some text" / "/photo/" / "/media/"
    private var waitResponse: Boolean = false

    init {
        keyboard.keyboardRow(
            KeyboardRow().apply { add("back") }
        )
    }

    fun addChild(reason: String, child: Step<*>) {
        children[reason] = child
    }

    operator fun set(reason: String, child: Step<*>) {
        addChild(reason, child)
    }

    operator fun invoke(update: Update, messageBuilder: SendMessage.SendMessageBuilder): Step<*> =
        when (waitResponse) {
            false -> {
                messageBuilder.apply {
                    replyMarkup(keyboard.build())
                    chatId(update.message.chatId.toString())
                    text(message)
                }
                waitResponse = true
                this
            }
            true -> {
                when (expected) {
                    "/photo/" ->
                }
            }
        }

    var data: T? = null
}