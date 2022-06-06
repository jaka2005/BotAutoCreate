package com.j2k.botAutoCreate.model

import com.j2k.botAutoCreate.exceptions.AccessErrorException
import com.j2k.botAutoCreate.step.StepInterface
import com.j2k.botAutoCreate.stepBuilder
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable


object Users : IntIdTable() {
    val userId = long("chat_id").uniqueIndex()
    val mode = enumeration("mode", UserMode::class)
    val stepId = long("step_id")
}

object States : IntIdTable() {
    val user = reference("user", Users)
    val stepId = long("step_id")
    val data = varchar("data", 255)
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var state: StepInterface = stepBuilder.build()

    private val userId by Users.userId
    private var _mode  by Users.mode

    var stepId by Users.stepId

    var mode: UserMode
        set(value) {
            if (this.onAdminChat()) {
                _mode = value
            } else {
                throw AccessErrorException("only administrators can change the user mode")
            }
        }
        get() = _mode

    private fun onAdminChat(): Boolean {
        TODO("implemented a function to check the user's presence in the admin chat")
    }


}

enum class UserMode {
    ADMIN, USER
}

