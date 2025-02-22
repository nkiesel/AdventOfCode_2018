import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import kotlin.math.max
import kotlin.math.sqrt

class Day11 {
    private val sample = """
        18
    """.trimIndent().lines()

    private fun parse(input: List<String>): Array<IntArray> {
        val serial = input[0].toInt()
        val area = Array(300) { IntArray(300) { 0 } }
        for (x in 0..<300) {
            for (y in 0..<300) {
                val v = ((x + 11) * (y + 1) + serial) * (x + 11)
                area[y][x] = if (v >= 100) v / 100 % 10 - 5 else -5
            }
        }
        return area
    }

    private fun one(input: List<String>): String {
        val area = parse(input)
        var m = Int.MIN_VALUE
        var p = ""
        for (x in 0..<297) {
            for (y in 0..<297) {
                var t = 0
                for (dx in 0..2) for (dy in 0..2) t += area[y + dy][x + dx]
                if (t > m) {
                    m = t
                    p = "${x + 1},${y + 1}"
                }
            }
        }
        return p
    }

    private fun two(input: List<String>): String {
        val area = parse(input)
        var m = Int.MIN_VALUE
        var p = ""
        var minSize = 1
        for (x in 0..<300) {
            for (y in 0..<300) {
                for (s in minSize..(300 - max(x, y))) {
                    var t = 0
                    for (dx in 0..<s) for (dy in 0..<s) t += area[y + dy][x + dx]
                    if (t > m) {
                        m = t
                        p = "${x + 1},${y + 1},${s}"
                        minSize = sqrt(t / 4.0).toInt() + 1
                    }
                }
            }
        }
        return p
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe "33,45"
        one(input) shouldBe "21,41"
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe "90,269,16"
        two(input) shouldBe "227,199,19"
    }
}
