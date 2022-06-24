package com.j2k.botAutoCreate.model

import com.j2k.botAutoCreate.admin.steps.StartModeStep
import com.j2k.botAutoCreate.step.StepInterface
import com.j2k.botAutoCreate.stepBuilder
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update


class User(userId: Long, mode: UserMode, stepId: Long, entityID: EntityID<Int>? = null) {
    companion object {
        val users = mutableMapOf<Long, User>()

        fun findByUserIdOrCreate(userId: Long): User = users[userId] ?: User(userId)
    }

    private val _userId: Long
    private var _mode: UserMode
    private var _stepId: Long

    val id: EntityID<Int>

    private fun addToDB(userId: Long, userMode: UserMode, stepId: Long) = transaction {
        Users.insert { user ->
            user[user_id] = userId
            user[mode] = userMode
            user[step_id] = stepId
        }
    }
    private fun addToDB() = transaction {
        Users.insert { user ->
            user[user_id] = _userId
            user[mode] = _mode
            user[step_id] = _stepId
        } get Users.id
    }

    private fun <T> update(column: Column<T>, value: T) = transaction {
        Users.update({ Users.user_id eq _userId }) { user ->
            user[column] = value
        }
    }

    init {
        _userId = userId
        _mode = mode
        _stepId = stepId
        id = entityID ?: addToDB()

        users[userId] = this
    }

    constructor(userId: Long) : this(userId, UserMode.USER, 0)


    var state: StepInterface = stepBuilder.build().searchNodeById(stepId)

    var mode: UserMode
        set(value) {
            update(Users.mode, value)

            state = if (value == UserMode.USER)
                stepBuilder.build().searchNodeById(stepId)
            else StartModeStep(_stepId)

            _mode = value
        }
        get() = _mode

    var stepId: Long
        set(value) {
            update(Users.step_id, value)
            _stepId = value
        }
        get() = _stepId
}

enum class UserMode {
    ADMIN, USER
}

