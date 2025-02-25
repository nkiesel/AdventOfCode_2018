import Day16.Op.*
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day16 {
    private val sample = """
        Before: [3, 2, 1, 1]
        9 2 1 2
        After:  [3, 2, 2, 1]
    """.trimIndent().lines()

    private class Sample(val before: List<Int>, val args: List<Int>, val after: List<Int>)

    private fun parse(input: List<String>): List<Sample> {
        return input.chunkedBy(String::isEmpty)
            .filter { it.isNotEmpty() && it[0].startsWith("Before") }
            .map { l -> l.map { it.ints() } }
            .map { l -> Sample(l[0], l[1], l[2]) }
    }

    private enum class Op {
        addr, addi,
        mulr, muli,
        banr, bani,
        borr, bori,
        setr, seti,
        gtir, gtri, gtrr,
        eqir, eqri, eqrr
    }

    private fun possible(op: Op, s: Sample): Boolean {
        val registers = s.before.toMutableList()
        execute(op, s.args.drop(1), registers)
        return registers == s.after
    }

    private fun execute(op: Op, d: List<Int>, registers: MutableList<Int>) {
        val va = d[0]
        val vb = d[1]
        val ra = registers[va]
        val rb = registers[vb]
        val c = d[2]
        registers[c] = when (op) {
            addr -> ra + rb
            addi -> ra + vb
            mulr -> ra * rb
            muli -> ra * vb
            banr -> ra and rb
            bani -> ra and vb
            borr -> ra or rb
            bori -> ra or vb
            setr -> ra
            seti -> va
            gtir -> if (va > rb) 1 else 0
            gtri -> if (ra > vb) 1 else 0
            gtrr -> if (ra > rb) 1 else 0
            eqir -> if (va == rb) 1 else 0
            eqri -> if (ra == vb) 1 else 0
            eqrr -> if (ra == rb) 1 else 0
        }
    }

    private fun one(input: List<String>): Int {
        return parse(input).count { sample -> Op.entries.count { op -> possible(op, sample) } >= 3 }
    }

    private fun two(input: List<String>): Int {
        val samples = parse(input)
        val program = input.drop(samples.size * 4).filter { it.isNotEmpty() }.map { it.ints() }
        val candidates = samples.map { s -> s.args[0] to Op.entries.filter { possible(it, s) }.toMutableSet() }
            .toMutableList()
        val operations = mutableMapOf<Int, Op>()
        while (candidates.isNotEmpty()) {
            candidates.first { (_, ops) -> ops.size == 1 }.let { (id, ops) ->
                val op = ops.first()
                operations[id] = op
                candidates.forEach { (_, ops) -> ops -= op }
                candidates.removeAll { (_, ops) -> ops.isEmpty() }
            }
        }

        val registers = MutableList(4) { 0 }
        program.forEach { i -> execute(operations[i[0]]!!, i.drop(1), registers) }
        return registers[0]
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 1
        one(input) shouldBe 531
    }

    @Test
    fun testTwo(input: List<String>) {
        two(input) shouldBe 649
    }
}
