import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day06 {
    private val sample = """
        1, 1
        1, 6
        8, 3
        3, 4
        5, 5
        8, 9
    """.trimIndent().lines()

    private fun parse(input: List<String>) = input.map { it.ints().let { (x, y) -> Point(x, y) } }

    private fun one(input: List<String>): Int {
        val points = parse(input)
        val upperLeft = Point(points.minOf { it.x }, points.minOf { it.y })
        val lowerRight = Point(points.maxOf { it.x }, points.maxOf { it.y })
        val nearest = mutableMapOf<Point, String>()
        val edges = mutableSetOf<String>()
        for (x in upperLeft.x..lowerRight.x) {
            for (y in upperLeft.y..lowerRight.y) {
                val p = Point(x, y)
                val d = points.associateWith { manhattanDistance(p, it) }
                val m = d.values.min()
                val md = d.filter { it.value == m }
                if (md.size == 1) {
                    val v = md.keys.first().toString()
                    if (x == upperLeft.x || x == lowerRight.x || y == upperLeft.y || y == lowerRight.y) edges += v
                    else if (v !in edges) nearest[p] = v
                }
            }
        }
        return nearest.values.filter { it !in edges }.groupingBy { it }.eachCount().values.max()
    }

    private fun two(input: List<String>, maxSum: Int): Int {
        val points = parse(input)
        val delta = maxSum / points.size
        val upperLeft = Point(points.minOf { it.x }, points.minOf { it.y }).move(-delta, -delta)
        val lowerRight = Point(points.maxOf { it.x }, points.maxOf { it.y }).move(delta, delta)
        var s = 0
        for (x in upperLeft.x..lowerRight.x) {
            for (y in upperLeft.y..lowerRight.y) {
                val p = Point(x, y)
                if (points.sumOf { manhattanDistance(p, it) } < maxSum) s++
            }
        }
        return s
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 17
        one(input) shouldBe 3420
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample, 32) shouldBe 16
        two(input, 10_000) shouldBe 46667
    }
}
