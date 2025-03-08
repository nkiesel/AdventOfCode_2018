import Day22.Tool.*
import Direction.N
import Direction.W
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day22 {
    private val sample = """
        depth: 510
        target: 10,10
    """.trimIndent().lines()

    val rocky = '.'
    val wet = '='
    val narrow = '|'

    class Context(
        val area: CharArea,
        val start: Point,
        val target: Point,
        val depth: Int,
        val erosionLevels: MutableMap<Point, Int>
    )

    private fun parse(input: List<String>): Context {
        val depth = input[0].ints()[0]
        val target = input[1].ints().let { (x, y) -> Point(x, y) }
        val area = CharArea(target.x + 1, target.y + 1, ' ')
        val start = Point(0, 0)
        area[start] = 'M'
        area[target] = 'T'
        val context = Context(area, start, target, depth, mutableMapOf())
        area.tiles().forEach { p -> area[p] = regionType(erosionLevel(p, context)) }
        return context
    }

    private fun one(input: List<String>): Int {
        val ctx = parse(input)
        return ctx.area.tiles().map {
            when (ctx.area[it]) {
                rocky -> 0
                wet -> 1
                else -> 2
            }
        }.sum()
    }

    private fun erosionLevel(p: Point, ctx: Context): Int {
        return ctx.erosionLevels.getOrPut(p) {
            val gi = when {
                p == ctx.start -> 0L
                p == ctx.target -> 0L
                p.y == 0 -> p.x * 16807L
                p.x == 0 -> p.y * 48271L
                else -> erosionLevel(p.move(W), ctx).toLong() * erosionLevel(p.move(N), ctx).toLong()
            }
            ((gi + ctx.depth) % 20183L).toInt()
        }
    }

    private fun regionType(el: Int) = when (el % 3) {
        0 -> rocky
        1 -> wet
        else -> narrow
    }

    enum class Tool { torch, climbing, neither }

    data class Pos(val pos: Point, val tool: Tool, val type: Char)

    private fun two(input: List<String>): Int {
        val ctx = parse(input)
        val types = ctx.area.tiles().associate { it to ctx.area[it] }.toMutableMap()
        val reached = mutableMapOf<Int, MutableSet<Pos>>()
        val seen = mutableSetOf<Pos>()
        reached[0] = mutableSetOf(Pos(ctx.start, torch, types[ctx.start]!!))
        fun add(minutes: Int, pos: Pos) {
            if (pos !in seen) reached.getOrPut(minutes) { mutableSetOf() }.add(pos)
        }
        while (true) {
            val minutes = reached.keys.min()
            val reachedPos = reached[minutes]!!
            for (p in reachedPos) {
                val (pos, tool, type) = p
                if (pos == ctx.target) {
                    if (tool == torch) return minutes
                    add(minutes + 7, Pos(ctx.target, torch, rocky))
                }
                for (n in pos.neighbors4().filter { it.x >= 0 && it.y >= 0 }) {
                    val nt = types.getOrPut(n) { regionType(erosionLevel(n, ctx)) }
                    val np = when (type to nt) {
                        rocky to wet -> Pos(n, climbing, nt)
                        rocky to narrow -> Pos(n, torch, nt)
                        wet to rocky -> Pos(n, climbing, nt)
                        wet to narrow -> Pos(n, neither, nt)
                        narrow to rocky -> Pos(n, torch, nt)
                        narrow to wet -> Pos(n, neither, nt)
                        else -> Pos(n, tool, type)
                    }
                    add(minutes + if (tool == np.tool) 1 else 8, np)
                }
            }
            seen += reachedPos
            reached.remove(minutes)
        }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 114
        one(input) shouldBe 4479
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 45
        two(input) shouldBe 1032
    }
}
