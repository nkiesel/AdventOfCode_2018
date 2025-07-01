import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day17 {
    private val sample = """
        x=495, y=2..7
        y=7, x=495..501
        x=501, y=3..7
        x=498, y=2..4
        x=506, y=1..2
        x=498, y=10..13
        x=504, y=10..13
        y=13, x=498..504
    """.trimIndent().lines()

    private fun parse(input: List<String>): List<Point> {
        return buildList {
            input.forEach { l ->
                val (a, b, c) = l.ints()
                if (l[0] == 'x') (b..c).forEach { add(Point(a, it)) } else (b..c).forEach { add(Point(it, a)) }
            }
        }
    }

    context(area: CharArea)
    private fun toWall(s: Point, direction: Direction): List<Point>? {
        var n = s
        val l = mutableListOf<Point>()
        while (true) {
            n = n.move(direction)
            when {
                area[n.move(Direction.S)] == '.' -> return null
                area[n] == '#' -> return l
                else -> l += n
            }
        }
    }

    context(area: CharArea)
    private fun fill(s: Point) {
        var n = s
        while (true) {
            n = n.move(Direction.S)
            if (n !in area) return
            when (area[n]) {
                '.' -> area[n] = '|'
                '|' -> return
                '~', '#' -> break
            }
        }

        while (true) {
            n = n.move(Direction.N)
            val left = toWall(n, Direction.W)
            val right = toWall(n, Direction.E)
            if (left == null || right == null) break
            (left + right + n).forEach { area[it] = '~' }
        }

        for (d in listOf(Direction.E, Direction.W)) {
            var s = '.'
            var m = n
            do {
                area[m] = '|'
                s = area[m.move(Direction.S)]
                m = m.move(d)
            } while (s != '.' && s != '|' && area[m] != '#')
            if (s == '.') {
                fill(m.move(d.reverse()))
            }
        }
    }

    private fun one(input: List<String>) = three(input, Part.ONE)

    private fun two(input: List<String>) = three(input, Part.TWO)

    private fun three(input: List<String>, part: Part): Int {
        val points = parse(input)
        val rx = points.minOf { it.x } - 2
        val area = CharArea(points.maxOf { it.x } - rx + 4, points.maxOf { it.y } + 1, '.')
        points.forEach { area[it.x - rx, it.y] = '#' }
        val spring = Point(500 - rx, 0)
        area[spring] = '+'
        with(area) {
            fill(spring)
        }
        area.tiles { it == '|' }.filter { area[it.move(Direction.N)] == '~' }.forEach { area[it] = '~' }
//        area.png()
        val minY = points.minOf { it.y }
        val relevant = if (part == Part.ONE) setOf('~', '|') else setOf('~')
        return area.tiles { it in relevant }.filter { it.y >= minY }.count()
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 57
        one(input) shouldBe 38364
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 29
        two(input) shouldBe 30551
    }
}
