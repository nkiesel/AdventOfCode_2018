import Direction.*
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day13 {
    private val sample = """
        /->-\        
        |   |  /----\
        | /-+--+-\  |
        | | |  | v  |
        \-+-/  \-+--/
          \------/   
    """.trimIndent().lines()

    private val sample2 = """
        />-<\  
        |   |  
        | /<+-\
        | | | v
        \>+</ |
          |   ^
          \<->/        
    """.trimIndent().lines()

    private fun parse(input: List<String>): Pair<CharArea, MutableList<Cart>> {
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

    private fun one(input: List<String>): String {
        val (area, carts) = parse(input)
        while (true) {
            for (c in carts.sorted()) {
                when (area[c.pos]) {
                    '/' -> if (c.direction in setOf(N, S)) c.turnRight() else c.turnLeft()
                    '\\' -> if (c.direction in setOf(N, S)) c.turnLeft() else c.turnRight()
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

    private fun two(input: List<String>): String {
        val (area, carts) = parse(input)
        while (true) {
            for (c in carts.sorted()) {
                when (area[c.pos]) {
                    '/' -> if (c.direction in setOf(N, S)) c.turnRight() else c.turnLeft()
                    '\\' -> if (c.direction in setOf(N, S)) c.turnLeft() else c.turnRight()
                    '+' -> when (c.intersections++ % 3) {
                        0 -> c.turnLeft()
                        2 -> c.turnRight()
                    }
                }
                c.move()
                if (carts.map { it.pos }.distinct().size != carts.size) {
                    carts.removeIf { it.pos == c.pos }
                }
            }
            if (carts.size == 1) return carts.first().toString()
        }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe "7,3"
        one(input) shouldBe "39,52"
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample2) shouldBe "6,4"
        two(input) shouldBe "133,146"
    }
}
