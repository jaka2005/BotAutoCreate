package com.j2k.botAutoCreate

import com.j2k.botAutoCreate.client.model.Client
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

class BotManager(private val token: String, private val username: String) : TelegramLongPollingBot() {
    override fun getBotToken() = token

    override fun getBotUsername() = username

    override fun onUpdateReceived(update: Update) {
        if (update.hasMessage() && update.message.isUserMessage) {
            try {
                val client = Client.findByChatId(update.message.chatId.toString())

                val messageBuilder = SendMessage.builder()

                if (update.message.text == "Назад") {
                    client.state.cancel(update, messageBuilder)
                } else {
                    client.state = client.state.update(update, messageBuilder)
                }

                execute(messageBuilder.build())

            } catch (e: TelegramApiException) {
                e.printStackTrace()
            }
        }
    }
}
