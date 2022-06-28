package com.j2k.botAutoCreate

import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import org.telegram.telegrambots.meta.TelegramBotsApi
import com.j2k.botAutoCreate.exceptions.RequiredArgumentException
import com.j2k.botAutoCreate.model.States
import com.j2k.botAutoCreate.model.User
import com.j2k.botAutoCreate.model.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.Path
import kotlin.properties.Delegates

var pathToScriptFile by Delegates.notNull<Path>()

/*
 * syntax of start the program:
 * java -jar BotAutoCreate.jar [token] [bot name] [admin chat id]
 *
 * token is your telegram bot token
 *
 * bot name is this is the unique name of your bot
 * which is written without spaces
 *
 * admin chat id is the id of your admin chat
 *
 * when you first start the program with the specified bot name,
 * a bot_name.json file is created in the "scripts" directory
 */
fun main(args: Array<String>) {
    val token = getArgument(args, "token", 0)
    val botName = getArgument(args, "bot name", 1)
    val adminChatId = getArgument(args, "admin chat id", 2)

    val scriptsDirectory = Paths.get("scripts/")
    if (!Files.isDirectory(scriptsDirectory)) Files.createDirectory(scriptsDirectory)
    val dataDirectory = Paths.get("data/")
    if (!Files.isDirectory(dataDirectory)) Files.createDirectory(dataDirectory)

    pathToScriptFile = Paths.get("scripts/$botName.json")
    if(!Files.exists(pathToScriptFile)) Files.createFile(pathToScriptFile)

    Database.connect("jdbc:sqlite:data/$botName.db", "org.sqlite.JDBC")
    TransactionManager.manager.defaultIsolationLevel =
        Connection.TRANSACTION_SERIALIZABLE

    transaction {
        //initializing tables
        SchemaUtils.create(Users, States)

        // initializing user state
        Users.selectAll().forEach {
            User(
                it[Users.user_id],
                it[Users.mode],
                it[Users.step_id],
                it[Users.id]
            )
        }
    }

    val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
    botsApi.registerBot(BotManager(token, botName, adminChatId))
}

private fun getArgument(args: Array<String>, argumentName: String, index: Int) : String {
    if (args.lastIndex < index) {
        throw RequiredArgumentException(argumentName)
    } else {
        return args[index]
    }
}