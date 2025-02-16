import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day03 {
    private val sample = """
        #1 @ 1,3: 4x4
        #2 @ 3,1: 4x4
        #3 @ 5,5: 2x2
    """.trimIndent().lines()

    private fun parse(input: List<String>) = input.map { it.ints() }

    private fun one(input: List<String>): Int {
        val counts = CountingMap<Point>()
        parse(input).forEach { l ->
            val (sx, sy, nx, ny) = l.drop(1)
            for (x in 0..<nx) {
                for (y in 0..<ny) {
                    counts.inc(Point(sx + x, sy + y))
                }
            }
        }
        return counts.values().count { it >= 2L }
    }

    private fun two(input: List<String>): Int {
        val counts = mutableMapOf<Point, Int>()
        val sizes = mutableMapOf<Int, Int>()
        parse(input).forEach { l ->
            val (n, sx, sy, nx, ny) = l
            sizes[n] = nx * ny
            for (x in 0..<nx) {
                for (y in 0..<ny) {
                    val p = Point(sx + x, sy + y)
                    if (p in counts.keys) counts[p] = -1 else counts[p] = n
                }
            }
        }
        return counts.values
            .filter { it != -1 }
            .groupingBy { it }.eachCount().entries
            .first { it.value == sizes[it.key] }
            .key
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 4
        one(input) shouldBe 116140
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 3
        two(input) shouldBe 574
    }
}
