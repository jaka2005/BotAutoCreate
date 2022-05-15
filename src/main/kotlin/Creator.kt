import org.telegram.telegrambots.meta.api.objects.Update

interface Creator {
    fun createScript(): Step<*>
    fun <T> createStep(
        parent: Step<*>? = null,
        message: String,
        buttonsText: List<String> = emptyList(),
        expected: String? = null
    ): Step<T>
}

class ScriptCreator : Creator {
    override fun createScript(): Step<*> {
        TODO("Not yet implemented")
    }

    override fun <T> createStep(
        parent: Step<*>?,
        message: String,
        buttonsText: List<String>,
        expected: String?
    ): Step<T> {
        TODO("Not yet implemented")
    }
}