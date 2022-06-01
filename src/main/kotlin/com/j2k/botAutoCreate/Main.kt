package com.j2k.botAutoCreate

import com.j2k.botAutoCreate.exceptions.RequiredArgumentException
import com.j2k.botAutoCreate.client.step.StepBuilder
import com.j2k.botAutoCreate.client.step.StepsData
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import org.telegram.telegrambots.meta.TelegramBotsApi
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.Path
import com.google.gson.Gson
import kotlin.properties.Delegates

var pathToDataFile by Delegates.notNull<Path>()
var stepBuilder by Delegates.notNull<StepBuilder>()

/*
 * syntax of start the program:
 * java -jar BotAutoCreate.jar [token] (bot name)
 *
 * token is your telegram bot token
 *
 * bot name is this is the unique name of your bot
 * which is written without spaces
 *
 * when you first start the program with the specified bot name,
 * a bot_name.json file is created in the data directory
 */
fun main(args: Array<String>) {
    val token = getArgument(args, "token", 0)
    val botName = getArgument(args, "bot name", 1)

    val dataDirectory = Paths.get("data/")
    if (!Files.isDirectory(dataDirectory)) Files.createDirectory(dataDirectory)

    pathToDataFile = Paths.get("data/$botName.json")
    if(!Files.exists(pathToDataFile)) Files.createFile(pathToDataFile)

    val jsonData = Gson().fromJson(pathToDataFile.toFile().readText(), StepsData::class.java)
    stepBuilder = StepBuilder.loadSettingsFromData(jsonData, StepBuilder())

    val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
    botsApi.registerBot(BotManager(token, botName))
}

fun getArgument(args: Array<String>, argumentName: String, index: Int) : String {
    if (args.lastIndex < index) {
        throw RequiredArgumentException(argumentName)
    } else {
        return args[index]
    }
}
