import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.math.abs

object Day25 {
    private fun parse(input: List<String>) = input.map { it.ints() }

    fun one(input: List<String>): Int {
        val constellations = mutableListOf<MutableSet<List<Int>>>()
        for (p in parse(input)) {
            val candidates = constellations.filter { c -> c.any { m4(it, p) <= 3 } }
            when (candidates.size) {
                0 -> constellations += mutableSetOf(p)
                1 -> candidates[0] += p
                else -> {
                    constellations -= candidates.toSet()
                    constellations += candidates.flatten().toMutableSet().apply { add(p) }
                }
            }
        }
        return constellations.size
    }

    private fun m4(a: List<Int>, b: List<Int>) = (0..3).sumOf { abs(a[it] - b[it]) }
}

object Day25Test : FunSpec({
    val input = lines("Day25")

    val sample1 = """
         0,0,0,0
         3,0,0,0
         0,3,0,0
         0,0,3,0
         0,0,0,3
         0,0,0,6
         9,0,0,0
        12,0,0,0
    """.trimIndent().lines()

    val sample2 = """
         -1,2,2,0
          0,0,2,-2
          0,0,0,-2
          -1,2,0,0
          -2,-2,-2,2
          3,0,2,-1
          -1,3,2,2
          -1,0,-1,0
          0,2,1,-2
          3,0,0,0
    """.trimIndent().lines()

    val sample3 = """
        1,-1,0,1
        2,0,-1,0
        3,2,-1,0
        0,0,3,1
        0,0,-1,-1
        2,3,-2,0
        -2,2,0,0
        2,-2,0,-1
        1,-1,0,-1
        3,2,0,2
    """.trimIndent().lines()

    val sample4 = """
        1,-1,-1,-2
        -2,-2,0,1
        0,2,1,3
        -2,3,-2,1
        0,2,3,-2
        -1,-1,1,-2
        0,-2,-1,0
        -2,2,3,-1
        1,2,2,0
        -1,-2,0,-2
    """.trimIndent().lines()

    test("one") {
        with(Day25) {
            one(sample1) shouldBe 2
            one(sample2) shouldBe 4
            one(sample3) shouldBe 3
            one(sample4) shouldBe 8
            one(input) shouldBe 399
        }
    }
})
