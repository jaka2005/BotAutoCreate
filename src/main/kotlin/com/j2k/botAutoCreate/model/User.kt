package com.j2k.botAutoCreate.model

import com.j2k.botAutoCreate.step.StepInterface
import com.j2k.botAutoCreate.stepBuilder
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction


class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users) {
        fun findByUserIdOrCreate(id: Long) : User = transaction {
            val users = find { Users.userId eq id }

            users.elementAtOrElse(0) {
                new {
                    stepId = 0
                    userId = id
                    mode = UserMode.USER
                }
            }
        }
    }

    var state: StepInterface = stepBuilder.build()

    private var userId by Users.userId
    var mode           by Users.mode
    var stepId         by Users.stepId
}

enum class UserMode {
    ADMIN, USER
}

