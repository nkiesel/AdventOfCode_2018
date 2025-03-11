import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day23 {
    private val sample = """
        pos=<0,0,0>, r=4
        pos=<1,0,0>, r=1
        pos=<4,0,0>, r=3
        pos=<0,2,0>, r=1
        pos=<0,5,0>, r=3
        pos=<0,0,3>, r=1
        pos=<1,1,1>, r=1
        pos=<1,1,2>, r=1
        pos=<1,3,1>, r=1
    """.trimIndent().lines()

    private val sample2 = """
        pos=<10,12,12>, r=2
        pos=<12,14,12>, r=2
        pos=<16,12,12>, r=4
        pos=<14,14,14>, r=6
        pos=<50,50,50>, r=200
        pos=<10,10,10>, r=5
    """.trimIndent().lines()

    private fun parse(input: List<String>) = input.map { it.ints().let { it.take(3).toIntArray() to it.last() }}

    private fun one(input: List<String>): Int {
        val teles = parse(input)
        val s = teles.maxBy { it.second }
        return teles.count { t -> manhattanDistance(t.first, s.first) <= s.second }
    }

    private fun two(input: List<String>): Int {
        val teles = parse(input)
        return 0
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 7
        one(input) shouldBe 326
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample2) shouldBe 36
//        two(input) shouldBe 0
    }
}
