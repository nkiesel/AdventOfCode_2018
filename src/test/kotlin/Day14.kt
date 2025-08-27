import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class Day14 {
    private fun three(input: List<String>, part: Part): String {
        val data = "37".map { it.digitToInt() }.toMutableList()
        var e1 = 0
        var e2 = 1
        val count = input[0].toInt()
        val wanted = input[0].map { it.digitToInt() }
        val ws = wanted.size
        while (true) {
            var nr = data[e1] + data[e2]
            val two = nr > 9
            if (two) {
                data += 1
                nr %= 10
            }
            data += nr
            val ds = data.size
            if (part == Part.ONE && ds >= count + 10) {
                return data.subList(count, count + 10).joinToString("")
            } else if (part == Part.TWO && ds > ws) {
                when {
                    data.subList(ds - ws, ds) == wanted -> return (ds - ws).toString()
                    two && data.subList(ds - ws - 1, ds - 1) == wanted -> return (ds - ws - 1).toString()
                }
            }
            e1 = (e1 + data[e1] + 1) % ds
            e2 = (e2 + data[e2] + 1) % ds
        }
    }

    fun one(input: List<String>) = three(input, Part.ONE)
    fun two(input: List<String>) = three(input, Part.TWO)
}

class Day14Test : FunSpec({
    val input = lines("Day14")

    with(Day14()) {
        test("one") {
            one(listOf("9")) shouldBe "5158916779"
            one(listOf("18")) shouldBe "9251071085"
            one(listOf("2018")) shouldBe "5941429882"
            one(input) shouldBe "7162937112"
        }

        test("two") {
            two(listOf("51589")) shouldBe "9"
            two(input) shouldBe "20195890"
        }
    }
})
