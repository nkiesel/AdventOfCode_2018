import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class Day15 {
    private fun parse(input: List<String>) = CharArea(input)

    data class Path(val next: Point, val steps: Int, val target: Point)

    class Unit(val start: Point, val area: CharArea) : Comparable<Unit> {
        val type = area[start]
        val other = if (type == 'E') 'G' else 'E'
        var pos = start
        var hp = 200
        var attackPower = 3
        override fun compareTo(other: Unit) = pos.compareTo(other.pos)
        override fun toString() = "$type[${start.x},${start.y}] ${pos.x},${pos.y} $hp"

        fun attack(targets: List<Unit>): Boolean {
            val nt = area.neighbors4(pos) { it == other }
            if (nt.isEmpty()) return false
            val target = targets.filter { it.pos in nt }.sorted().minBy { it.hp }
            target.hp -= attackPower
            if (target.hp <= 0) {
                target.hp = 0
                area[target.pos] = '.'
            }
            return true
        }

        fun move(targets: List<Unit>): Boolean {
            val possibleNext = area.neighbors4(pos) { it == '.' }
            if (possibleNext.isEmpty()) return false

            val inRange = targets.flatMap { area.neighbors4(it.pos) { it == '.' } }.toSet()
            if (inRange.isEmpty()) return false

            var np = possibleNext.mapIndexedNotNull { _, n ->
                val t = bfs(n) { area.neighbors4(it) { it == '.' } }.filter { it.value in inRange }
                t.minOfOrNull { it.index }?.let { m -> Path(n, m, t.filter { it.index == m }.minOf { it.value }) }
            }
            if (np.isEmpty()) return false

            area[pos] = '.'
            pos = np.minOf { it.steps }.let { m -> np.filter { it.steps == m }.minBy { it.target }.next }
            area[pos] = type
            return true
        }
    }

    private fun three(input: List<String>, part: Part): Any {
        var (attackPowerMin, attackPowerMax) = if (part == Part.ONE) 3 to 3 else 4 to 200
        outer@ while (true) {
            val attackPower = (attackPowerMin + attackPowerMax) / 2
            val area = parse(input)
            var egs = area.tiles { it == 'E' || it == 'G' }.map { Unit(it, area) }.toList().sorted()
            egs.filter { it.type == 'E' }.forEach { it.attackPower = attackPower }
            var completed = 0
            while (true) {
                for (eg in egs) {
                    if (eg.hp <= 0) continue

                    val targets = egs.filter { it.type == eg.other && it.hp > 0 }
                    if (targets.isEmpty()) {
                        if (attackPowerMin == attackPowerMax) return egs.sumOf { it.hp } * completed
                        attackPowerMax = attackPower
                        continue@outer
                    }

                    val attacked = eg.attack(targets) || eg.move(targets) && eg.attack(targets)
                    if (attacked && part == Part.TWO && eg.type == 'G' && egs.any { it.type == 'E' && it.hp == 0 }) {
                        attackPowerMin = attackPower + 1
                        continue@outer
                    }
                }
                completed++
                egs = egs.filter { it.hp > 0 }.sorted()
            }
        }
    }

    fun one(input: List<String>) = three(input, Part.ONE)

    fun two(input: List<String>) = three(input, Part.TWO)
}

class Day15Test : FunSpec({
    val input = lines("Day15")

    val sample1 = """
        #######
        #.G...#
        #...EG#
        #.#.#G#
        #..G#E#
        #.....#
        #######
    """.trimIndent().lines()

    val sample2 = """
        #######
        #G..#E#
        #E#E.E#
        #G.##.#
        #...#E#
        #...E.#
        #######
    """.trimIndent().lines()

    val sample3 = """
        #######
        #E..EG#
        #.#G.E#
        #E.##E#
        #G..#.#
        #..E#.#
        #######
    """.trimIndent().lines()

    val sample4 = """
        #######
        #E.G#.#
        #.#G..#
        #G.#.G#   
        #G..#.#
        #...E.#
        #######
    """.trimIndent().lines()

    val sample5 = """
        #######
        #.E...#   
        #.#..G#
        #.###.#
        #E#G#G#
        #...#G#
        #######
    """.trimIndent().lines()

    val sample6 = """
        #########
        #G......#
        #.E.#...#
        #..##..G#
        #...##..#
        #...#...#
        #.G...G.#
        #.....G.#
        #########
    """.trimIndent().lines()

    with(Day15()) {
        test("one") {
            one(sample1) shouldBe 27730
            one(sample2) shouldBe 36334
            one(sample3) shouldBe 39514
            one(sample4) shouldBe 27755
            one(sample5) shouldBe 28944
            one(sample6) shouldBe 18740
            one(input) shouldBe 220480
        }

        test("two") {
            two(sample1) shouldBe 4988
            two(sample3) shouldBe 31284
            two(sample4) shouldBe 3478
            two(sample5) shouldBe 6474
            two(sample6) shouldBe 1140
            two(input) shouldBe 53576
        }
    }
})
