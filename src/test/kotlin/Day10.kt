import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class Day10 {
    fun one(input: List<String>): Int {
        val data = input.map { it.ints() }.map { Point(it[0], it[1]) to Point(it[2], it[3]) }
        var pos = data.map { it.first }
        val vel = data.map { it.second }
        var s = 0
        do {
            s++
            pos = pos.mapIndexed { i, p -> p.move(vel[i]) }
        } while (pos.none { p -> (0..6).all { p.move(0, -it) in pos } })
        val minX = pos.minOf { it.x }
        val minY = pos.minOf { it.y }
        val maxX = pos.maxOf { it.x }
        val maxY = pos.maxOf { it.y }
        val area = CharArea(maxX - minX + 1, maxY - minY + 1, '.')
        pos.map { it.move(-minX, -minY) }.forEach { area[it] = '#' }
        area.png()
        return s
    }
}

class Day10Test : FunSpec({
    val input = lines("Day10")

    val sample = """
        position=< 9,  1> velocity=< 0,  2>
        position=< 7,  0> velocity=<-1,  0>
        position=< 3, -2> velocity=<-1,  1>
        position=< 6, 10> velocity=<-2, -1>
        position=< 2, -4> velocity=< 2,  2>
        position=<-6, 10> velocity=< 2, -2>
        position=< 1,  8> velocity=< 1, -1>
        position=< 1,  7> velocity=< 1,  0>
        position=<-3, 11> velocity=< 1, -2>
        position=< 7,  6> velocity=<-1, -1>
        position=<-2,  3> velocity=< 1,  0>
        position=<-4,  3> velocity=< 2,  0>
        position=<10, -3> velocity=<-1,  1>
        position=< 5, 11> velocity=< 1, -2>
        position=< 4,  7> velocity=< 0, -1>
        position=< 8, -2> velocity=< 0,  1>
        position=<15,  0> velocity=<-2,  0>
        position=< 1,  6> velocity=< 1,  0>
        position=< 8,  9> velocity=< 0, -1>
        position=< 3,  3> velocity=<-1,  1>
        position=< 0,  5> velocity=< 0, -1>
        position=<-2,  2> velocity=< 2,  0>
        position=< 5, -2> velocity=< 1,  2>
        position=< 1,  4> velocity=< 2,  1>
        position=<-2,  7> velocity=< 2, -2>
        position=< 3,  6> velocity=<-1, -1>
        position=< 5,  0> velocity=< 1,  0>
        position=<-6,  0> velocity=< 2,  0>
        position=< 5,  9> velocity=< 1, -2>
        position=<14,  7> velocity=<-2,  0>
        position=<-3,  6> velocity=< 2, -1>
    """.trimIndent().lines()


    with(Day10()) {
        test("one") {
            one(sample) shouldBe 3 // HI
            one(input) shouldBe 10391 // BFFZCNXE
        }
    }
})
