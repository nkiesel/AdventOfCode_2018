import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.io.path.Path
import kotlin.io.path.readLines

class Day08 {
    private fun parse(input: List<String>) = input[0].ints()

    private data class Node(val children: List<Node>, val meta: List<Int>, val part: Part) {
        val value: Int = when {
            children.isEmpty() -> meta.sum()
            part == Part.ONE -> meta.sum() + children.sumOf { it.value }
            else -> meta.map { it - 1 }.filter { it in children.indices }.sumOf { children[it].value }
        }
    }

    private fun three(input: List<String>, part: Part): Int {
        val data = parse(input)
        fun getNode(i: Int): Pair<Node, Int> {
            val nc = data[i]
            val nm = data[i + 1]
            var ni = i + 2
            val children = buildList {
                repeat(nc) {
                    val r = getNode(ni)
                    add(r.first)
                    ni = r.second
                }
            }
            val meta = data.subList(ni, ni + nm)
            return Node(children, meta, part) to ni + nm
        }
        return getNode(0).first.value
    }

    fun one(input: List<String>) = three(input, Part.ONE)
    fun two(input: List<String>) = three(input, Part.TWO)
}

class Day08Test : FunSpec({
    val input = Path("input/Day08.txt").readLines()

    val sample = """
        2 3 0 3 10 11 12 1 1 0 1 99 2 1 1 2
    """.trimIndent().lines()

    with(Day08()) {
        test("one") {
            one(sample) shouldBe 138
            one(input) shouldBe 43825
        }

        test("two") {
            two(sample) shouldBe 66
            two(input) shouldBe 19276
        }
    }
})
