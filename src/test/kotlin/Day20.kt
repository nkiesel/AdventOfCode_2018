import Direction.*
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day20 {
    private fun one(regex: String): Int {
        val area = parse(regex)
        return bfs(area.first('X')) { p -> area.neighbors4(p) { it != '#' } }.maxOf { it.index } / 2
    }

    private fun two(regex: String): Int {
        val area = parse(regex)
        val start = area.first('X')
        val rooms = area.tiles { it == '.' }
        return rooms.count { r -> bfs(start) { p -> area.neighbors4(p) { it != '#' } }.first { it.value == r }.index >= 2000 }
    }

    private fun parse(regex: String): CharArea {
        val points = mutableMapOf<Point, Char>()
        var start = Point(0, 0)
        addPoints(regex, setOf(start), points)
        val minX = points.keys.minOf { it.x }
        val maxX = points.keys.maxOf { it.x }
        val minY = points.keys.minOf { it.y }
        val maxY = points.keys.maxOf { it.y }
        val area = CharArea(maxX - minX + 3, maxY - minY + 3, '#')
        points.forEach { (p, c) -> area[p.move(-minX + 1, -minY + 1)] = c }
        area[start.move(-minX + 1, -minY + 1)] = 'X'
        return area
    }

    private fun addPoints(regex: String, starts: Set<Point>, map: MutableMap<Point, Char>): Set<Point> {
        var ps = starts

        fun add(d: Direction, c: Char) {
            ps = buildSet {
                ps.forEach {
                    var p = it.move(d)
                    map[p] = c
                    p = p.move(d)
                    map[p] = '.'
                    add(p)
                }
            }
        }

        var i = 0
        while (i < regex.length) {
            when (regex[i]) {
                'E' -> add(E, '|')
                'W' -> add(W, '|')
                'N' -> add(N, '-')
                'S' -> add(S, '-')
                '(' -> {
                    val gr = groups(regex, i)
                    ps = buildSet { gr.first.forEach { addAll(addPoints(it, ps, map)) } }
                    i = gr.second
                }
            }
            i++
        }
        return ps
    }

    private fun groups(regex: String, s: Int): Pair<List<String>, Int> {
        var i = s + 1
        var k = i
        var l = 0
        val g = mutableListOf<String>()
        while (true) {
            when (regex[i]) {
                '(' -> l++
                ')' -> if (l-- == 0) {
                    g += regex.substring(k, i)
                    return g to i
                }

                '|' -> if (l == 0) {
                    g += regex.substring(k, i)
                    k = i + 1
                }
            }
            i++
        }
    }

    @Test
    fun testOne(input: List<String>) {
        one($$"^ENNWSWW(NEWS|)SSSEEN(WNSE|)EE(SWEN|)NNN$") shouldBe 18
        one($$"^WNE$") shouldBe 3
        one($$"^ENWWW(NEEE|SSE(EE|N))$") shouldBe 10
        one($$"^ESSWWN(E|NNENN(EESS(WNSE|)SSS|WWWSSSSE(SW|NNNE)))$") shouldBe 23
        one($$"^WSSEESWWWNW(S|NENNEEEENN(ESSSSW(NWSW|SSEN)|WSWWN(E|WWS(E|SS))))$") shouldBe 31
        one(input[0]) shouldBe 3415
    }

    @Test
    fun testTwo(input: List<String>) {
        two(input[0]) shouldBe 8583
    }
}
