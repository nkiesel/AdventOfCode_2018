import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class Day01 {
    private fun parse(input: List<String>) = input.map { it.toInt() }

    fun one(input: List<String>): Int {
        return parse(input).sum()
    }

    fun two(input: List<String>): Int {
        val data = parse(input)
        val seen = mutableSetOf<Int>()
        var sum = 0
        var idx = 0
        do {
            sum += data[idx]
            idx = (idx + 1) % data.size
        } while (seen.add(sum))
        return sum
    }
}

class Day01Test : FunSpec({
    val input = lines("Day01")

    val sample = """
        +1
        -2
        +3
        +1
        """.trimIndent().lines()


    with(Day01()) {
        test("one") {
            one(sample) shouldBe 3
            one(input) shouldBe 402
        }

        test("two") {
            two(sample) shouldBe 2
            two(input) shouldBe 481
        }
    }
}
)
