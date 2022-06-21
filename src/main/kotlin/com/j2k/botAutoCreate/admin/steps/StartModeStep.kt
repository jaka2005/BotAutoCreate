package com.j2k.botAutoCreate.admin.steps

import com.j2k.botAutoCreate.model.User
import com.j2k.botAutoCreate.step.StepInterface
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

class StartModeStep : StepInterface {
    override fun update(
        user: User,
        update: Update,
        messageBuilder: SendMessage.SendMessageBuilder
    ): StepInterface {
        TODO("Not yet implemented")
    }

    override fun cancel(
        user: User,
        update: Update,
        messageBuilder: SendMessage.SendMessageBuilder
    ): StepInterface {
        TODO("Not yet implemented")
    }
}