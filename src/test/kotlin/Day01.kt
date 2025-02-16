import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day01 {
    private val sample = """
        +1
        -2
        +3
        +1
        """.trimIndent().lines()

    private fun parse(input: List<String>) = input.map { it.toInt() }

    private fun one(input: List<String>): Int {
        return parse(input).sum()
    }

    private fun two(input: List<String>): Int {
        val data = parse(input)
        val seen = mutableSetOf<Int>()
        var sum = 0
        while (true) {
            data.forEach {
                sum += it
                if (!seen.add(sum)) return sum
            }
        }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 3
        one(input) shouldBe 402
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 2
        two(input) shouldBe 481
    }
}
