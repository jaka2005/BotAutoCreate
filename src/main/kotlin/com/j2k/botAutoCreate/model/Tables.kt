package com.j2k.botAutoCreate.model

import org.jetbrains.exposed.dao.id.IntIdTable

object Users : IntIdTable() {
    val user_id = long("user_id").uniqueIndex()
    val mode = enumeration("mode", UserMode::class)
    val step_id = long("step_id")
}

object States : IntIdTable() {
    val user = reference("user", Users)
    val step_id = long("step_id")
    val data = varchar("data", 255)
}
