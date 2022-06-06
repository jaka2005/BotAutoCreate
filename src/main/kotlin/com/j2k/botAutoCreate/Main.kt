package com.j2k.botAutoCreate

import com.j2k.botAutoCreate.exceptions.RequiredArgumentException
import com.j2k.botAutoCreate.step.StepBuilder
import com.j2k.botAutoCreate.step.StepsData
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import org.telegram.telegrambots.meta.TelegramBotsApi
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.Path
import com.google.gson.Gson
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection
import kotlin.properties.Delegates

var pathToScriptFile by Delegates.notNull<Path>()
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
 * a bot_name.json file is created in the "scripts" directory
 */
fun main(args: Array<String>) {


    val token = getArgument(args, "token", 0)
    val botName = getArgument(args, "bot name", 1)

    val scriptsDirectory = Paths.get("scripts/")
    if (!Files.isDirectory(scriptsDirectory)) Files.createDirectory(scriptsDirectory)
    val dataDirectory = Paths.get("data/")
    if (!Files.isDirectory(dataDirectory)) Files.createDirectory(dataDirectory)

    pathToScriptFile = Paths.get("scripts/$botName.json")
    if(!Files.exists(pathToScriptFile)) Files.createFile(pathToScriptFile)

    Database.connect("jdbc:sqlite:/data/$botName.db", "org.sqlite.JDBC")
    TransactionManager.manager.defaultIsolationLevel =
        Connection.TRANSACTION_SERIALIZABLE

    val jsonData = Gson().fromJson(pathToScriptFile.toFile().readText(), StepsData::class.java)
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