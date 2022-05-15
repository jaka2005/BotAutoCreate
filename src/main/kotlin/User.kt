data class User(
    val chatId: String,
    var state: Step<*>,
) {
    companion object {
        val users: MutableMap<String, User> = mutableMapOf()
        fun findByChatId(chatId: String): User? = users.getOrElse(chatId) {
            users[chatId] = User(chatId, scriptCreator.createScript())
            users[chatId]
        }
    }
    init {
        users[chatId] = this
    }
}