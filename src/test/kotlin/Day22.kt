import Direction.N
import Direction.W
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class Day22 {
    private val rocky = '.'
    private val wet = '='
    private val narrow = '|'

    private class Context(
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

    fun one(input: List<String>): Int {
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

    private enum class Tool { torch, climbing, neither }

    private data class Pos(val pos: Point, val tool: Tool, val type: Char)

    fun two(input: List<String>): Int {
        val ctx = parse(input)
        val types = ctx.area.tiles().associateWith { ctx.area[it] }.toMutableMap()
        val reached = mutableMapOf<Int, MutableSet<Pos>>()
        val seen = mutableSetOf<Pos>()
        reached[0] = mutableSetOf(Pos(ctx.start, Tool.torch, types[ctx.start]!!))
        fun add(minutes: Int, pos: Pos) {
            if (pos !in seen) reached.getOrPut(minutes) { mutableSetOf() }.add(pos)
        }
        while (true) {
            val minutes = reached.keys.min()
            val reachedPos = reached[minutes]!!
            for (p in reachedPos) {
                val (pos, tool, type) = p
                if (pos == ctx.target) {
                    if (tool == Tool.torch) return minutes
                    add(minutes + 7, Pos(ctx.target, Tool.torch, rocky))
                }
                for (n in pos.neighbors4().filter { it.x >= 0 && it.y >= 0 }) {
                    val nt = types.getOrPut(n) { regionType(erosionLevel(n, ctx)) }
                    val np = when (type to nt) {
                        rocky to wet -> Pos(n, Tool.climbing, nt)
                        rocky to narrow -> Pos(n, Tool.torch, nt)
                        wet to rocky -> Pos(n, Tool.climbing, nt)
                        wet to narrow -> Pos(n, Tool.neither, nt)
                        narrow to rocky -> Pos(n, Tool.torch, nt)
                        narrow to wet -> Pos(n, Tool.neither, nt)
                        else -> Pos(n, tool, type)
                    }
                    add(minutes + if (tool == np.tool) 1 else 8, np)
                }
            }
            seen += reachedPos
            reached.remove(minutes)
        }
    }
}

class Day22Test : FunSpec({
    val input = lines("Day22")

    val sample = """
        depth: 510
        target: 10,10
    """.trimIndent().lines()

    with(Day22()) {
        test("one") {
            one(sample) shouldBe 114
            one(input) shouldBe 4479
        }

        test("two") {
            two(sample) shouldBe 45
            two(input) shouldBe 1032
        }
    }
})
