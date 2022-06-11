package com.j2k.botAutoCreate

import com.j2k.botAutoCreate.model.User
import com.j2k.botAutoCreate.model.UserMode
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

class BotManager(
    private val token: String,
    private val username: String,
    private val adminChatId: String
) : TelegramLongPollingBot() {

    private val getChatMemberBuilder = GetChatMember.builder()


    override fun getBotToken() = token

    override fun getBotUsername() = username

    override fun onUpdateReceived(update: Update) {
        if (update.hasMessage() && update.message.isUserMessage) {
            try {
                val messageText = update.message.text
                val userId: Long = update.message.from.id
                val messageBuilder = SendMessage.builder()

                val user = User.findByUserIdOrCreate(userId)

                if (messageText.startsWith("/mode ")) {
                    if (isChatMember(adminChatId, userId)) {
                        user.mode = UserMode.valueOf(messageText.drop(6).uppercase())
                        messageBuilder.text(
                            "Режим пользования изменен на \"${user.mode}\""
                        )
                    } else {
                        messageBuilder.text(
                            "Вы не можете изменять режим пользования," +
                                    " так как не являетесь членом чата администраторов"
                        )
                    }
                    messageBuilder.chatId(userId.toString())

                } else {
                    if (update.message.text == "Назад") {
                        user.state.cancel(user, update, messageBuilder)
                    } else {
                        user.state = user.state.update(user, update, messageBuilder)
                    }
                }

                execute(messageBuilder.build())

            } catch (e: TelegramApiException) {
                e.printStackTrace()
            }
        }
    }

    private fun isChatMember(chatId: String, userId: Long): Boolean {
        val getChatMember = getChatMemberBuilder.chatId(chatId)
            .userId(userId)
            .build()

        return execute(getChatMember) != null
    }
}
