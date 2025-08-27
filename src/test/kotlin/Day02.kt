import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

object Day02 {
    private fun parse(input: List<String>) = input

    fun one(input: List<String>): Int {
        var two = 0
        var three = 0
        parse(input).forEach { id ->
            val c = id.groupingBy { it }.eachCount().values
            if (2 in c) two++
            if (3 in c) three++
        }
        return two * three
    }

    fun two(input: List<String>): String {
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
}

object Day02Test : FunSpec({
    val input = lines("Day02")

    val sample = """
        abcdef
        bababc
        abbcde
        abcccd
        aabcdd
        abcdee
        ababab
    """.trimIndent().lines()

    val sample2 = """
        abcde
        fghij
        klmno
        pqrst
        fguij
        axcye
        wvxyz
    """.trimIndent().lines()

    with(Day02) {
        test("one") {
            one(sample) shouldBe 12
            one(input) shouldBe 5750
        }

        test("two") {
            two(sample2) shouldBe "fgij"
            two(input) shouldBe "tzyvunogzariwkpcbdewmjhxi"
        }
    }
})
