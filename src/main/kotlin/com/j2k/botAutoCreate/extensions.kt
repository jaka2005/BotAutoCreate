import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message

fun TelegramLongPollingBot.sendMsg(chatId: Long, message: String): Message {
    return execute(SendMessage().apply {
        text = message
        this.chatId = chatId.toString()
    })
}
