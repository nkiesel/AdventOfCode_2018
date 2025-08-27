import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

enum class Part { ONE, TWO }

object Day04 {
    private fun parse(input: List<String>) = input.map { it.split("] ") }.sortedBy { it.first() }

    private fun three(input: List<String>, part: Part): Int {
        val records = parse(input)
        val minutes = CountingMap<Int>()
        val asleep = mutableMapOf<Int, MutableList<IntRange>>()
        var guard = 0
        var fellAsleep = -1
        records.forEach { (ts, act) ->
            val a = act.split(" ")
            val minute = ts.takeLast(2).toInt()
            when (a[0]) {
                "Guard" -> {
                    if (fellAsleep >= 0) {
                        minutes.inc(guard, 60 - fellAsleep)
                        asleep.getOrPut(guard) { mutableListOf() }.add(fellAsleep..<60)
                    }
                    guard = a[1].drop(1).toInt()
                    fellAsleep = -1
                }

                "falls" -> {
                    fellAsleep = minute
                }

                "wakes" -> {
                    minutes.inc(guard, minute - fellAsleep)
                    asleep.getOrPut(guard) { mutableListOf() }.add(fellAsleep..<minute)
                    fellAsleep = -1
                }
            }
        }
        if (part == Part.ONE) {
            val guardWithMostMinutes = minutes.entries().maxBy { it.value }.key
            val guardMinutes = CountingMap<Int>()
            asleep[guardWithMostMinutes]!!.forEach { r -> r.forEach { guardMinutes.inc(it) } }
            return guardWithMostMinutes * guardMinutes.entries().maxBy { it.value }.key
        } else {
            val minutesToGuards = mutableMapOf<Int, MutableList<Int>>()
            (0..59).forEach { m ->
                val l = mutableListOf<Int>()
                asleep.entries.forEach { (g, s) ->
                    s.forEach { if (m in it) l += g }
                }
                minutesToGuards[m] = l
            }
            val counts = minutesToGuards.mapValues { v -> v.value.groupingBy { it }.eachCount() }
            val maxMinute = counts.entries.filter { it.value.isNotEmpty() }.maxBy { it.value.values.max() }
            return maxMinute.key * maxMinute.value.maxBy { it.value }.key
        }
    }

    fun one(input: List<String>) = three(input, Part.ONE)
    fun two(input: List<String>) = three(input, Part.TWO)
}

object Day04Test : FunSpec({
    val input = lines("Day04")

    val sample = """
        [1518-11-01 00:00] Guard #10 begins shift
        [1518-11-01 00:05] falls asleep
        [1518-11-01 00:25] wakes up
        [1518-11-01 00:30] falls asleep
        [1518-11-01 00:55] wakes up
        [1518-11-01 23:58] Guard #99 begins shift
        [1518-11-02 00:40] falls asleep
        [1518-11-02 00:50] wakes up
        [1518-11-03 00:05] Guard #10 begins shift
        [1518-11-03 00:24] falls asleep
        [1518-11-03 00:29] wakes up
        [1518-11-04 00:02] Guard #99 begins shift
        [1518-11-04 00:36] falls asleep
        [1518-11-04 00:46] wakes up
        [1518-11-05 00:03] Guard #99 begins shift
        [1518-11-05 00:45] falls asleep
        [1518-11-05 00:55] wakes up
    """.trimIndent().lines()

    with(Day04) {
        test("one") {
            one(sample) shouldBe 240
            one(input) shouldBe 87681
        }

        test("two") {
            two(sample) shouldBe 4455
            two(input) shouldBe 136461
        }
    }
})
