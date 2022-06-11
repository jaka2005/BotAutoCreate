package com.j2k.botAutoCreate.model

import org.jetbrains.exposed.dao.id.IntIdTable

object Users : IntIdTable() {
    val userId = long("user_id").uniqueIndex()
    val mode = enumeration("mode", UserMode::class)
    val stepId = long("step_id")
}

object States : IntIdTable() {
    val user = reference("user", Users)
    val stepId = long("step_id")
    val data = varchar("data", 255)
}
