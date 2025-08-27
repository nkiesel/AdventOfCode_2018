import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

object Day19 {
    private enum class Op {
        addr, addi,
        mulr, muli,
        banr, bani,
        borr, bori,
        setr, seti,
        gtir, gtri, gtrr,
        eqir, eqri, eqrr
    }

    private data class Instruction(val op: Op, val a: Int, val b: Int, val c: Int) {
        fun execute(r: MutableList<Long>) {
            r[c] = when (op) {
                Op.addr -> r[a] + r[b]
                Op.addi -> r[a] + b
                Op.mulr -> r[a] * b
                Op.muli -> r[a] * b
                Op.banr -> r[a] and r[b]
                Op.bani -> r[a] and b.toLong()
                Op.borr -> r[a] or r[b]
                Op.bori -> r[a] or b.toLong()
                Op.setr -> r[a]
                Op.seti -> a.toLong()
                Op.gtir -> if (a.toLong() > r[b]) 1 else 0
                Op.gtri -> if (r[a] > b) 1 else 0
                Op.gtrr -> if (r[a] > r[b]) 1 else 0
                Op.eqir -> if (a.toLong() == r[b]) 1 else 0
                Op.eqri -> if (r[a] == b.toLong()) 1 else 0
                Op.eqrr -> if (r[a] == r[b]) 1 else 0
            }
        }

        override fun toString() = "$op [$a, $b, $c]"
    }

    private fun parse(input: List<String>) =
        input.map { l ->
            val s = l.split(" ", limit = 2)
            val i = s[1].ints()
            Instruction(Op.valueOf(s[0]), i[0], i[1], i[2])
        }

    private fun three(input: List<String>, part: Part): Long {
        val ir = input[0].ints()[0]
        val instructions = parse(input.drop(1))
        var ip = 0
        val registers = MutableList(6) { 0L }
        registers[0] = if (part == Part.ONE) 0 else 1
        var b = registers.toList()
        while (ip in instructions.indices) {
            registers[ir] = ip.toLong()
            instructions[ip].execute(registers)
            val r1 = registers[0]
            if (b[0] != r1) {
                println("d0=${r1 - b[0]} d2=${b[2] - registers[2]} d4=${registers[4] - b[4]} $registers")
                b = registers.toList()
            }
            ip = registers[ir].toInt() + 1
        }
        return registers[0]
    }

    fun one(input: List<String>) = three(input, Part.ONE)
    fun two(input: List<String>) = three(input, Part.TWO)
}

object Day19Test : FunSpec({
    val input = lines("Day19")

    val sample = """
        #ip 0
        seti 5 0 1
        seti 6 0 2
        addi 0 1 0
        addr 1 2 3
        setr 1 0 0
        seti 8 0 4
        seti 9 0 5
    """.trimIndent().lines()

    with(Day19) {
        test("one") {
            one(sample) shouldBe 6
            one(input) shouldBe 3224
        }

        test("two") {
            two(input) shouldBe 0
        }
    }
})
