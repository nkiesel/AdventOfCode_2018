import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day18 {
    private val sample = """
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

    private fun parse(input: List<String>) = CharArea(input)

    private fun one(input: List<String>) = three(input, Part.ONE)

    private fun two(input: List<String>) = three(input, Part.TWO)

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

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 1147
        one(input) shouldBe 543312
    }

    @Test
    fun testTwo(input: List<String>) {
        two(input) shouldBe 199064
    }
}
