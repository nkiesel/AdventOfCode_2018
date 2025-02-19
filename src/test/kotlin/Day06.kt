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

    private fun area(upperLeft: Point, lowerRight: Point) = sequence {
        for (x in upperLeft.x..lowerRight.x) for (y in upperLeft.y..lowerRight.y) yield(Point(x, y))
    }

    private fun one(input: List<String>): Int {
        val points = parse(input)
        val upperLeft = Point(points.minOf { it.x }, points.minOf { it.y })
        val lowerRight = Point(points.maxOf { it.x }, points.maxOf { it.y })
        val edges = mutableSetOf<String>()
        return buildList {
            for (p in area(upperLeft, lowerRight)) {
                    val d = points.associateWith { manhattanDistance(p, it) }
                    val m = d.values.min()
                    val md = d.filter { it.value == m }
                    if (md.size == 1) {
                        val v = md.keys.first().toString()
                        if (p.x == upperLeft.x || p.x == lowerRight.x || p.y == upperLeft.y || p.y == lowerRight.y) edges += v
                        else if (v !in edges) add(v)
                    }
            }
        }.filter { it !in edges }.groupingBy { it }.eachCount().values.max()
    }

    private fun two(input: List<String>, maxSum: Int): Int {
        val points = parse(input)
        val delta = maxSum / points.size
        val upperLeft = Point(points.minOf { it.x }, points.minOf { it.y }).move(-delta, -delta)
        val lowerRight = Point(points.maxOf { it.x }, points.maxOf { it.y }).move(delta, delta)
        return area(upperLeft, lowerRight).count { p -> points.sumOf { manhattanDistance(p, it) } < maxSum }
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
