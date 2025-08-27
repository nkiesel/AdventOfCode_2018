import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

object Day21 {
    val sample = """"""
        .trimIndent()
        .lines()

    fun parse(input: List<String>) = input

    fun one(input: List<String>): Int {
        return 0
    }

    fun two(input: List<String>): Int {
        return 0
    }
}

object Day21Test : FunSpec({
    val input = lines("Day21")

    val sample = Day21.sample

    with(Day21) {
        test("one") {
            one(sample) shouldBe 0
            // one(input) shouldBe <expected_result> // Uncomment and set expected value when ready
        }

        test("two") {
            two(sample) shouldBe 0
            // two(input) shouldBe <expected_result> // Uncomment and set expected value when ready
        }
    }
})
