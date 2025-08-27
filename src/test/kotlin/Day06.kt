import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class Day06 {
    private fun parse(input: List<String>) = input.map { it.ints().let { (x, y) -> Point(x, y) } }

    private lateinit var upperLeft: Point
    private lateinit var lowerRight: Point

    private fun area(points: List<Point>, delta: Int = 0) = sequence {
        upperLeft = Point(points.minOf { it.x }, points.minOf { it.y }).move(-delta, -delta)
        lowerRight = Point(points.maxOf { it.x }, points.maxOf { it.y }).move(delta, delta)
        for (x in upperLeft.x..lowerRight.x) for (y in upperLeft.y..lowerRight.y) yield(Point(x, y))
    }

    fun one(input: List<String>): Int {
        val points = parse(input)
        val edges = mutableSetOf<String>()
        return buildList {
            for (p in area(points)) {
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

    fun two(input: List<String>, maxSum: Int): Int {
        val points = parse(input)
        return area(points, maxSum / points.size).count { p -> points.sumOf { manhattanDistance(p, it) } < maxSum }
    }
}

class Day06Test : FunSpec({
    val input = lines("Day06")

    val sample = """
        1, 1
        1, 6
        8, 3
        3, 4
        5, 5
        8, 9
    """.trimIndent().lines()

    with(Day06()) {
        test("one") {
            one(sample) shouldBe 17
            one(input) shouldBe 3420
        }

        test("two") {
            two(sample, 32) shouldBe 16
            two(input, 10_000) shouldBe 46667
        }
    }
})
