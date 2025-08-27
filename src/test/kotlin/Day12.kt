import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.io.path.Path
import kotlin.io.path.readLines

class Day12 {
    fun parse(input: List<String>): Pair<String, Map<String, String>> {
        val initial = input[0].substringAfter("initial state: ")
        val rules = input.drop(2).associate { l -> l.split(" => ").let { it[0] to it[1] } }
        return initial to rules
    }

    fun one(input: List<String>): Int {
        val (initial, rules) = parse(input)
        var table = initial.trimStart('.')
        var removed = initial.length - table.length
        repeat(20) {
            table.trimEnd('.')
            table = "....$table...."
            val n = (2..table.lastIndex - 2).joinToString("") { i -> rules[table.substring(i - 2, i + 3)] ?: "." }
            table = n.trimStart('.')
            removed += 2 - n.length + table.length
        }
        println(table)
        println("removed: $removed")
        return table.withIndex().sumOf { (i, s) -> if (s == '#') i - removed else 0 }
    }

    fun two(input: List<String>): Long {
        val (initial, rules) = parse(input)
        var table = initial.trimStart('.')
        var removed = (initial.length - table.length).toLong()
        table.trimEnd('.')
        val seen = mutableMapOf<String, Long>()
        val rs = mutableMapOf<Long, Long>()
        var i = 0L
        val limit = 50000000000L
        while (i < limit) {
            table = "....$table...."
            val n = (2..table.lastIndex - 2).joinToString("") { i -> rules[table.substring(i - 2, i + 3)] ?: "." }
            table = n.trimStart('.')
            removed += 2L - n.length + table.length
            table = table.trimEnd('.')
            val prev = seen[table]
            if (prev == null) {
                seen[table] = i
                rs[i] = removed
            } else {
                removed += (removed - rs[prev]!!) * (limit - i) + 1
                break
            }
            i++
        }
        return table.withIndex().sumOf { (i, s) -> if (s == '#') i - removed else 0 }
    }
}

class Day12Test : FunSpec({
    val input = Path("input/Day12.txt").readLines()

    val sample = """
        initial state: #..#.#..##......###...###

        ...## => #
        ..#.. => #
        .#... => #
        .#.#. => #
        .#.## => #
        .##.. => #
        .#### => #
        #.#.# => #
        #.### => #
        ##.#. => #
        ##.## => #
        ###.. => #
        ###.# => #
        ####. => #
    """.trimIndent().lines()

    with(Day12()) {
        test("one") {
            one(sample) shouldBe 325
            one(input) shouldBe 4386
        }

        test("two") {
            two(input) shouldBe 5450000001166L
        }
    }
})
