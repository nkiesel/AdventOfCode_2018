import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day05 {
    private val sample = """dabAcCaCBAcCcaDA"""

    private fun one(data: String): Int {
        val remaining = ArrayDeque<Char>(data.length)
        data.forEach {
            val c = if (it.isLowerCase()) it.uppercaseChar() else it.lowercaseChar()
            if (c == remaining.lastOrNull()) remaining.removeLast() else remaining.addLast(it)
        }
        return remaining.size
    }

    private fun two(data: String): Int {
        return ('a'..'z').minOf { one(data.replace(it.toString(), "", ignoreCase = true)) }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 10
        one(input[0]) shouldBe 9822
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 4
        two(input[0]) shouldBe 5726
    }
}
