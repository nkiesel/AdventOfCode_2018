import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class Day05 {
    fun one(data: String): Int {
        val remaining = ArrayDeque<Char>(data.length)
        data.forEach {
            val c = if (it.isLowerCase()) it.uppercaseChar() else it.lowercaseChar()
            if (c == remaining.lastOrNull()) remaining.removeLast() else remaining.addLast(it)
        }
        return remaining.size
    }

    fun two(data: String): Int {
        return ('a'..'z').minOf { one(data.replace(it.toString(), "", ignoreCase = true)) }
    }
}

class Day05Test : FunSpec({
    val input = lines("Day05")

    val sample = "dabAcCaCBAcCcaDA"

    with(Day05()) {
        test("one") {
            one(sample) shouldBe 10
            one(input[0]) shouldBe 9822
        }

        test("two") {
            two(sample) shouldBe 4
            two(input[0]) shouldBe 5726
        }
    }
})
