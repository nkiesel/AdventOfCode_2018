import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day02 {
    private val sample = """
            abcdef
            bababc
            abbcde
            abcccd
            aabcdd
            abcdee
            ababab
    """.trimIndent().lines()

    private val sample2 = """
        abcde
        fghij
        klmno
        pqrst
        fguij
        axcye
        wvxyz
    """.trimIndent().lines()

    private fun parse(input: List<String>) = input

    private fun one(input: List<String>): Int {
        var two = 0
        var three = 0
        parse(input).forEach {
            val c = it.groupingBy { it }.eachCount().values
            if (2 in c) two++
            if (3 in c) three++
        }
        return two * three
    }

    private fun two(input: List<String>): String {
        val ids = parse(input)
        for (a in ids) {
            for (b in ids) {
                val d = a.withIndex().count { it.value != b[it.index] }
                if (d == 1) {
                    return a.withIndex().filter { it.value == b[it.index] }.map { it.value }.joinToString("")
                }
            }
        }
        error("Should not happen")
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 12
        one(input) shouldBe 5750
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample2) shouldBe "fgij"
        two(input) shouldBe "tzyvunogzariwkpcbdewmjhxi"
    }
}
