import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day07 {
    private val sample = """
        Step C must be finished before step A can begin.
        Step C must be finished before step F can begin.
        Step A must be finished before step B can begin.
        Step A must be finished before step D can begin.
        Step B must be finished before step E can begin.
        Step D must be finished before step E can begin.
        Step F must be finished before step E can begin.
    """.trimIndent().lines()

    private fun parse(input: List<String>): Pair<MutableSet<Char>, Map<Char, Set<Char>>> {
        val before = input.map { it.split(" ").let { it[1][0] to it[7][0] } }
        val steps = before.flatMap { it.toList() }.sorted().toMutableSet()
        val after = mutableMapOf<Char, MutableSet<Char>>()
        before.forEach { (b, a) -> after.getOrPut(a) { mutableSetOf() } += b }
        return steps to after
    }


    private fun one(input: List<String>): String {
        val (steps, after) = parse(input)
        val start = steps.first { after[it] == null }
        val visited = mutableSetOf(start)
        steps -= visited
        while (steps.isNotEmpty()) {
            val n = steps.first { after[it].let { it.isNullOrEmpty() || visited.containsAll(it) } }
            visited += n
            steps -= n
        }

        return visited.joinToString("")
    }

    private fun two(input: List<String>, nw: Int, delay: Int): Int {
        val (steps, after) = parse(input)
        val visited = mutableSetOf<Char>()

        class Worker {
            var s: Char? = null
            var d: Int = 0

            init {
                val n = steps.firstOrNull { after[it] == null }
                if (n != null) take(n)
            }

            fun take(c: Char) {
                s = c
                d = c - 'A' + 1 + delay
                steps -= c
            }

            fun step() {
                if (s != null && --d == 0) {
                    visited += s!!
                    s = null
                }
            }
        }

        val workers = List(nw) { Worker() }

        var t = 0
        while (true) {
            t++
            workers.forEach { it.step() }
            workers.filter { it.s == null }.forEach { w ->
                val c = steps.firstOrNull { after[it].let { it.isNullOrEmpty() || visited.containsAll(it) } }
                if (c != null) {
                    w.take(c)
                    if (steps.isEmpty()) return t + workers.sumOf { it.d }
                }
            }
        }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe "CABDFE"
        one(input) shouldBe "CFMNLOAHRKPTWBJSYZVGUQXIDE"
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample, 2, 0) shouldBe 15
        two(input, 5, 60) shouldBe 971
    }
}
