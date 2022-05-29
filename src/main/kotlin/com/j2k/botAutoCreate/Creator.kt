import com.j2k.botAutoCreate.step.Step

interface Creator {
    fun createScript(): Step
    fun createStep(
        parent: Step? = null,
        message: String,
        buttonsText: List<String> = emptyList(),
        expected: String? = null
    ): Step
}

class ScriptCreator : Creator {
    override fun createScript(): Step {
        TODO("Not yet implemented")
    }

    override fun createStep(
        parent: Step?,
        message: String,
        buttonsText: List<String>,
        expected: String?
    ): Step {
        TODO("Not yet implemented")
    }
}
