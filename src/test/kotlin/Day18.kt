import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.io.path.Path
import kotlin.io.path.readLines

class Day18 {
    private fun parse(input: List<String>) = CharArea(input)

    private fun three(input: List<String>, part: Part): Int {
        var area = parse(input)
        var m = 0
        val hashes = mutableMapOf<Int, Int>()
        val limit = if (part == Part.ONE) 10 else 1_000_000_000
        var jump = true
        while (m < limit) {
            val next = area.clone('.')
            area.tiles().forEach { t ->
                val n8 = area.neighbors8(t).map { area[it] }
                next[t] = when (area[t]) {
                    '.' -> if (n8.count { it == '|' } >= 3) '|' else '.'
                    '|' -> if (n8.count { it == '#' } >= 3) '#' else '|'
                    '#' -> if (n8.any { it == '#' } && n8.any { it == '|' }) '#' else '.'
                    else -> error("invalid character")
                }
            }
            area = next
            if (jump) hashes.put(area.hashCode(), m)?.let {
                val c = m - it
                m += (limit - m) / c * c
                jump = false
            }
            m++
        }
        return area.tiles { it == '|' }.count() * area.tiles { it == '#' }.count()
    }

    fun one(input: List<String>) = three(input, Part.ONE)
    fun two(input: List<String>) = three(input, Part.TWO)
}

class Day18Test : FunSpec({
    val input = Path("input/Day18.txt").readLines()

    val sample = """
        .#.#...|#.
        .....#|##|
        .|..|...#.
        ..|#.....#
        #.#|||#|#|
        ...#.||...
        .|....|...
        ||...#|.#|
        |.||||..|.
        ...#.|..|.
    """.trimIndent().lines()

    with(Day18()) {
        test("one") {
            one(sample) shouldBe 1147
            one(input) shouldBe 543312
        }

        test("two") {
            two(input) shouldBe 199064
        }
    }
})
