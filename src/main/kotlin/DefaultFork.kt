import org.telegram.telegrambots.meta.api.objects.Update

class DefaultFork(override val steps: List<(update: Update) -> Unit> = emptyList()) : Fork() {

}