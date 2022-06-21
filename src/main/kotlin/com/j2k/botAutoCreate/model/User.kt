package com.j2k.botAutoCreate.model

import com.j2k.botAutoCreate.admin.steps.StartModeStep
import com.j2k.botAutoCreate.step.StepInterface
import com.j2k.botAutoCreate.stepBuilder
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction


class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users) {
        fun findByUserIdOrCreate(id: Long): User = transaction {
            val users = find { Users.userId eq id }
            users.forEach { print("${it.stepId} - ${it.userId}") }
            users.elementAtOrElse(0) {
                new {
                    stepId = 0
                    userId = id
                    _mode = UserMode.USER
                }
            }
        }
    }

    var state: StepInterface = stepBuilder.build()

    private var userId by Users.userId
    private var _mode  by Users.mode

    var stepId by Users.stepId

    var mode: UserMode
        set(value) {
            state = if (value == UserMode.USER)
                stepBuilder.build().searchNodeById(stepId)
            else StartModeStep()

            _mode = value
        }
        get() = _mode
}

enum class UserMode {
    ADMIN, USER
}

