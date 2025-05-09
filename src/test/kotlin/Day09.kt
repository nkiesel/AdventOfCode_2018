import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day09 {
    private fun parse(input: List<String>) = input[0].ints()

    private fun one(input: List<String>) = three(input, 1).toInt()

    private fun two(input: List<String>) = three(input, 100)

    private fun three(input: List<String>, fac: Int): Long {
        val (players, points) = parse(input)
        val marbles = points * fac
        val circle = ArrayDeque(listOf(0, 1))
        val kept = CountingMap<Int>()
        var idx = 1
        var cp = 1
        for (m in 2..marbles) {
            cp = (cp + 1) % players
            if (m % 23 == 0) {
                kept.inc(cp, m)
                idx = (idx + circle.size - 7) % circle.size
                kept.inc(cp, circle.removeAt(idx))
            } else {
                idx = (idx + 2) % circle.size
                circle.add(idx, m)
            }
        }
        return kept.values().max()
    }

    @Test
    fun testOne(input: List<String>) {
        fun t(players: Int, points: Int) = one(listOf("$players players; last marble is worth $points points"))
        t(9, 25) shouldBe 32
        t(9, 250) shouldBe 361
        t(9, 2500) shouldBe 22563
        t(13, 7999) shouldBe 146373
        t(10, 1618) shouldBe 8317
        one(input) shouldBe 374287
    }

    @Test
    fun testTwo(input: List<String>) {
        two(input) shouldBe 3083412635L
    }
}
