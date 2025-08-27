import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class Template {
    private fun parse(input: List<String>) = input

    fun one(input: List<String>): Int {
        return 0
    }

    fun two(input: List<String>): Int {
        return 0
    }
}

class TemplateTest : FunSpec({
    val input = lines("Template")

    val sample = """""".trimIndent().lines()

    with(Template()) {
        test("one") {
            one(sample) shouldBe 0
//            one(input) shouldBe 0
        }

        xtest("two") {
            two(sample) shouldBe 0
//            two(input) shouldBe 0
        }
    }
})
