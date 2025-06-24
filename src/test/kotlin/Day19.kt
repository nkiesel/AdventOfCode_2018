import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day19 {
    private val sample = """
        #ip 0
        seti 5 0 1
        seti 6 0 2
        addi 0 1 0
        addr 1 2 3
        setr 1 0 0
        seti 8 0 4
        seti 9 0 5
    """.trimIndent().lines()

    enum class Op {
        addr, addi,
        mulr, muli,
        banr, bani,
        borr, bori,
        setr, seti,
        gtir, gtri, gtrr,
        eqir, eqri, eqrr
    }

    data class Instruction(val op: Op, val a: Int, val b: Int, val c: Int) {
        fun execute(r: MutableList<Long>) {
            r[c] = when (op) {
                addr -> r[a] + r[b]
                addi -> r[a] + b
                mulr -> r[a] * r[b]
                muli -> r[a] * b
                banr -> r[a] and r[b]
                bani -> r[a] and b.toLong()
                borr -> r[a] or r[b]
                bori -> r[a] or b.toLong()
                setr -> r[a]
                seti -> a.toLong()
                gtir -> if (a > r[b]) 1 else 0
                gtri -> if (r[a] > b) 1 else 0
                gtrr -> if (r[a] > r[b]) 1 else 0
                eqir -> if (a.toLong() == r[b]) 1 else 0
                eqri -> if (r[a] == b.toLong()) 1 else 0
                eqrr -> if (r[a] == r[b]) 1 else 0
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

    private fun one(input: List<String>) = three(input, Part.ONE)

    private fun two(input: List<String>) = three(input, Part.TWO)

    private fun three(input: List<String>, part: Part): Long {
        val ir = input[0].ints()[0]
        val instructions = parse(input.drop(1))
        var ip = 0
        val registers = MutableList(6) { 0L }
        registers[0] = if (part ==  Part.ONE) 0 else 1
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

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 6
        one(input) shouldBe 3224
    }

    @Test
    fun testTwo(input: List<String>) {
        two(input) shouldBe 0
    }
}
