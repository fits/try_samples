
sealed class Command {
    companion object {
        val open = Open.Companion::create
        val close = Close.Companion::create
    }

    sealed class Open : Command() {
        abstract val id: String

        companion object {
            fun create(id: String): Open? = 
                if (id.isBlank()) null
                else OpenData(id)
        }

        private data class OpenData(override val id: String) : Open()
    }

    sealed class Close : Command() {
        abstract val id: String
        abstract val value: Int

        companion object {
            fun create(id: String, value: Int): Close? = 
                if (id.isBlank() || value < 0) null
                else CloseData(id, value)
        }

        private data class CloseData(override val id: String, 
                                     override val value: Int = 0) : Close()
    }
}

fun show(d: Command): String =
    when(d) {
        is Command.Open -> "open"
        is Command.Close -> "close"
    }


fun main() {
    Command.open("test1")?.let { a ->
        println("1-1: ${show(a)}")

        Command.close(a.id, 1)?.let { b ->
            println("1-2: ${show(b)}")
        }
    }

    Command.open("test2")?.let { a ->
        println("2-1: ${show(a)}")

        println( Command.open("") )

        Command.open("test2")?.let { b ->
            println("2-2: ${a == b}")
        }

        println( Command.close(a.id, -1) )
    }
}