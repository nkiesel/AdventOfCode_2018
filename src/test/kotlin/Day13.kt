import Direction.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

object Day13 {

    class Cart(var pos: Point, d: Char) : Comparable<Cart> {
        var intersections: Int = 0
        var direction: Direction = Direction.from(d)

        fun turnRight() {
            direction = direction.turnRight()
        }

        fun turnLeft() {
            direction = direction.turnLeft()
        }

        fun move() {
            pos = pos.move(direction)
        }

        override fun compareTo(other: Cart) = compareValuesBy(this.pos, other.pos, Point::y, Point::x)
        override fun toString() = "${pos.x},${pos.y}"
    }

    fun parse(input: List<String>): Pair<CharArea, MutableList<Cart>> {
        val area = CharArea(input)
        val carts = area.tiles { it in "<>^v" }.map { Cart(it, area[it]) }.toMutableList()
        carts.forEach { c ->
            area[c.pos] = when (c.direction) {
                E, W -> '-'
                N, S -> '|'
                else -> error("invalid")
            }
        }
        return area to carts
    }


    fun one(input: List<String>): String {
        val (area, carts) = parse(input)
        val ns = setOf(N, S)
        while (true) {
            for (c in carts.sorted()) {
                when (area[c.pos]) {
                    '/' -> if (c.direction in ns) c.turnRight() else c.turnLeft()
                    '\\' -> if (c.direction in ns) c.turnLeft() else c.turnRight()
                    '+' -> when (c.intersections++ % 3) {
                        0 -> c.turnLeft()
                        2 -> c.turnRight()
                    }
                }
                c.move()
                if (carts.map { it.pos }.distinct().size != carts.size) return c.toString()
            }
        }
    }

    fun two(input: List<String>): String {
        val (area, carts) = parse(input)
        while (carts.size > 1) {
            for (c in carts.sorted()) {
                if (c !in carts) continue
                when (area[c.pos]) {
                    '/' -> if (c.direction in setOf(N, S)) c.turnRight() else c.turnLeft()
                    '\\' -> if (c.direction in setOf(N, S)) c.turnLeft() else c.turnRight()
                    '+' -> when (c.intersections++ % 3) {
                        0 -> c.turnLeft()
                        2 -> c.turnRight()
                    }
                }
                c.move()
                val crashed = carts.filter { it.pos == c.pos }
                if (crashed.size > 1) {
                    carts.removeAll(crashed)
                }
            }
        }
        return carts.first().toString()
    }
}

object Day13Test : FunSpec({
    val input = lines("Day13")

    val sample = """
        /->-\        
        |   |  /----\
        | /-+--+-\  |
        | | |  | v  |
        \-+-/  \-+--/
          \------/   
    """.trimIndent().lines()

    val sample2 = """
        />-<\  
        |   |  
        | /<+-\
        | | | v
        \>+</ |
          |   ^
          \<->/        
    """.trimIndent().lines()

    with(Day13) {
        test("one") {
            one(sample) shouldBe "7,3"
            one(input) shouldBe "39,52"
        }

        test("two") {
            two(sample2) shouldBe "6,4"
            two(input) shouldBe "133,146"
        }
    }
})
