import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day05 {
    private val sample = """dabAcCaCBAcCcaDA""".trimIndent().lines()

    private fun parse(input: List<String>) = input[0]

    private fun one(input: List<String>): Int {
        var data = parse(input)
        val letters = ('a'..'z').zip('A'..'Z').joinToString("|") { (l, u) -> "$l$u|$u$l" }.toRegex()
        do {
            val l = data.length
            data = letters.replace(data, "")
        } while (l != data.length)
        return data.length
    }

    private fun two(input: List<String>): Int {
        var data = parse(input)
        val letters = ('a'..'z').zip('A'..'Z').joinToString("|") { (l, u) -> "$l$u|$u$l" }.toRegex()
        return ('a'..'z').minOf { l ->
            var d = data.replace(l.toString(), "", ignoreCase = true)
            do {
                val l = d.length
                d = letters.replace(d, "")
            } while (l != d.length)
            d.length
        }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 10
        one(input) shouldBe 9822
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 4
        two(input) shouldBe 5726
    }
}
