import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import java.io.File

val scriptCreator: ScriptCreator = ScriptCreator()

fun main(args: Array<String>) {
    val tokens = File("token.txt").readLines()
    val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
    botsApi.registerBot(Bot(tokens[0], "test_task"))
}

class Bot(private val token: String, private val username: String): TelegramLongPollingBot() {
    override fun getBotToken() = token

    override fun getBotUsername() = username

    override fun onUpdateReceived(update: Update) {
        if (update.hasMessage()) {
            try {
//                val user =
            } catch (e: TelegramApiException) {
                e.printStackTrace()
            }
        }
    }
}
