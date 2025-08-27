import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.math.*

class Day23 {
    data class Bot(val x: Int, val y: Int, val z: Int, val r: Int) {
        constructor(p: IntArray, r: Int) : this(p[0], p[1], p[2], r)

        fun contains(oz: Int, oy: Int, ox: Int) = abs(x - ox) + abs(y - oy) + abs(z - oz) <= r
    }

    private fun parse(input: List<String>) = input.map { it.ints().let { it.take(3).toIntArray() to it.last() } }

    fun one(input: List<String>): Int {
        val teles = parse(input)
        val s = teles.maxBy { it.second }
        return teles.count { t -> manhattanDistance(t.first, s.first) <= s.second }
    }

    fun two(input: List<String>): Int {
        val bots = parse(input).map { Bot(it.first, it.second) }
        val minX = bots.minOf { it.x - it.r }
        val minY = bots.minOf { it.y - it.r }
        val minZ = bots.minOf { it.z - it.r }
        val maxX = bots.maxOf { it.x + it.r }
        val maxY = bots.maxOf { it.y + it.r }
        val maxZ = bots.maxOf { it.z + it.r }
        var count = 0
        var best = 0
        for (x in minX..maxX) {
            for (y in minY..maxY) {
                for (z in minZ..maxZ) {
                    val s = bots.count { it.contains(x, y, z) }
                    if (s > count) {
                        count = s
                        best = abs(x) + abs(y) + abs(z)
                    } else if (s != 0 && s == count) {
                        best = min(best, abs(x) + abs(y) + abs(z))
                    }
                }
            }
        }
        return best
    }

    fun twoA(input: List<String>) {
        val bots = parse(input).map { Bot(it.first, it.second) }
        val maxR = bots.maxOf { it.r }
        val f = 10.0.pow(floor(log10(maxR.toDouble())))
        val b = bots.map {
            Bot(
                ceil(it.x / f).toInt(),
                ceil(it.y / f).toInt(),
                ceil(it.z / f).toInt(),
                ceil(it.r / f).toInt()
            )
        }
        val p = foo(b, 0, 0, 0)
        println(p)
        for (x in p) {
            val bx = b.map { Bot(it.x * 10, it.y * 10, it.z * 10, it.r * 10) }
        }
    }

    private fun foo(bots: List<Bot>, x: Int, y: Int, z: Int): List<List<Int>> {
        var count = 0
        val p = mutableListOf<List<Int>>()
        for (x in (x - 10)..(x + 10)) {
            for (y in (y - 10)..(y + 10)) {
                for (z in (z - 10)..(z + 10)) {
                    val s = bots.count { it.contains(x, y, z) }
                    if (s > count) {
                        count = s
                        p.clear()
                        p += listOf(x, y, z)
                    } else if (s != 0 && s == count) {
                        p += listOf(x, y, z)
                    }
                }
            }
        }
        return p
    }
}

class Day23Test : FunSpec({
    val input = lines("Day23")

    val sample = """
        pos=<0,0,0>, r=4
        pos=<1,0,0>, r=1
        pos=<4,0,0>, r=3
        pos=<0,2,0>, r=1
        pos=<0,5,0>, r=3
        pos=<0,0,3>, r=1
        pos=<1,1,1>, r=1
        pos=<1,1,2>, r=1
        pos=<1,3,1>, r=1
    """.trimIndent().lines()

    val sample2 = """
        pos=<10,12,12>, r=2
        pos=<12,14,12>, r=2
        pos=<16,12,12>, r=4
        pos=<14,14,14>, r=6
        pos=<50,50,50>, r=200
        pos=<10,10,10>, r=5
    """.trimIndent().lines()

    with(Day23()) {
        test("one") {
            one(sample) shouldBe 7
            one(input) shouldBe 326
        }
        test("twoA") {
            twoA(sample2)
        }
        test("two") {
            two(sample2) shouldBe 36
            two(input) shouldBe 0
        }
    }
})
