typealias Condition = (Char) -> Boolean

enum class Direction {
    N, NE, E, SE, S, SW, W, NW;

    fun turnRight() = when (this) {
        N -> E
        NE -> SE
        E -> S
        SE -> SW
        S -> W
        SW -> NW
        W -> N
        NW -> NE
    }

    fun turnLeft() = when (this) {
        N -> W
        NE -> NW
        E -> N
        SE -> NE
        S -> E
        SW -> SE
        W -> S
        NW -> SW
    }

    fun reverse() = when (this) {
        N -> S
        NE -> SW
        E -> W
        SE -> NW
        S -> N
        SW -> NE
        W -> E
        NW -> SE
    }

    companion object {
        fun from(char: Char) = when (char) {
            '^' -> N
            '>' -> E
            'v' -> S
            '<' -> W
            else -> error("Char '$char' cannot be converted to a Direction")
        }
        fun from(d: String) = when (d.lowercase()) {
            "n" -> N
            "ne" -> NE
            "e" -> E
            "se" -> SE
            "s" -> S
            "sw" -> SW
            "w" -> W
            "nw" -> NW
            else -> error("Unexpected direction $d")

        }
    }
}

data class Point(val x: Int, val y: Int) : Comparable<Point> {
    fun move(d: Direction, n: Int = 1) = when (d) {
        Direction.N -> Point(x, y - n)
        Direction.NE -> Point(x + n, y - n)
        Direction.E -> Point(x + n, y)
        Direction.SE -> Point(x + n, y + n)
        Direction.S -> Point(x, y + n)
        Direction.SW -> Point(x - n, y + n)
        Direction.W -> Point(x - n, y)
        Direction.NW -> Point(x - n, y - n)
    }

    fun move(dx: Int, dy: Int) = Point(x + dx, y + dy)

    fun move(p: Point, n: Int = 1) = Point(x + p.x * n, y + p.y * n)

    fun neighbors4() = listOf(0 to -1, -1 to 0, 1 to 0, 0 to 1)
        .map { (dx, dy) -> Point(x + dx, y + dy) }

    fun neighbors8() = listOf(-1 to -1, 0 to -1, 1 to -1, -1 to 0, 1 to 0, -1 to 1, 0 to 1, 1 to 1)
        .map { (dx, dy) -> Point(x + dx, y + dy) }

    operator fun minus(other: Point) = Point(x - other.x, y - other.y)

    fun direction(other: Point): Direction {
        require(this != other) { "Points are identical" }
        val d = this - other
        if (d.x == 0) {
            return if (d.y < 0) Direction.S else Direction.N
        }
        if (d.y == 0) {
            return if (d.x < 0) Direction.E else Direction.W
        }
        error("Only handing 4 neighbors")
    }

    override fun compareTo(other: Point): Int = compareValuesBy(this, other, Point::y, Point::x)
}

class CharArea(private val area: Array<CharArray>) {
    constructor(columns: Int, rows: Int, def: Char) : this(Array(rows) { CharArray(columns) { def } })
    constructor(lines: List<String>) : this(lines.map { it.toCharArray() }.toTypedArray())
    constructor(lines: String) : this(lines.trimIndent().lines())

    val xRange = area[0].indices
    val yRange = area.indices

    fun clone(def: Char): CharArea = CharArea(xRange.last + 1, yRange.last + 1, def)

    fun clone(): CharArea {
        val next = clone(' ')
        tiles().forEach { next[it] = this[it] }
        return next
    }

    operator fun get(x: Int, y: Int) = area[y][x]

    fun getOrNull(x: Int, y: Int) = if (valid(x, y)) get(x, y) else null

    fun getOrNull(p: Point) = if (valid(p)) get(p) else null

    operator fun get(p: Point) = get(p.x, p.y)

    fun valid(x: Int, y: Int) = x in xRange && y in yRange

    fun valid(p: Point, c: Char) = valid(p) { it == c }

    fun valid(p: Point, condition: Condition? = null) = valid(p.x, p.y) && ok(p, condition)

    operator fun contains(p: Point) = valid(p)

    operator fun set(x: Int, y: Int, c: Char) {
        if (valid(x, y)) area[y][x] = c
    }

    fun set(x: Int, y: Int, c: (Char) -> Char) {
        if (valid(x, y)) area[y][x] = c(area[y][x])
    }

    operator fun set(p: Point, c: Char) {
        set(p.x, p.y, c)
    }

    fun tiles(condition: Condition? = null): Sequence<Point> = sequence {
        for (x in xRange) {
            for (y in yRange) {
                Point(x, y).takeIf { ok(it, condition) }?.let { yield(it) }
            }
        }
    }

    fun manhattan(p: Point, max: Int, c: Char): Sequence<Point> = manhattan(p, max) { it == c }

    fun manhattan(p: Point, max: Int, condition: Condition? = null): Sequence<Point> = sequence {
        for (x in (-max)..max) {
            for (y in (-max)..(max)) {
                val n = p.move(x, y)
                if (valid(n, condition) && manhattanDistance(p, n) in 1..max) {
                    yield(n)
                }
            }
        }
    }

    fun edges(): Sequence<Point> = tiles()
        .filter { (x, y) -> x == xRange.first || x == xRange.last || y == yRange.first || y == yRange.last }

    fun corners() = listOf(
        Point(xRange.first, yRange.first),
        Point(xRange.first, yRange.last),
        Point(xRange.last, yRange.first),
        Point(xRange.last, yRange.last),
    )

    fun first(c: Char): Point {
        val y = area.indexOfFirst { c in it }
        val x = area[y].indexOfFirst { it == c }
        return Point(x, y)
    }

    fun ok(p: Point, condition: Condition?): Boolean = condition == null || condition(this[p])

    fun neighbors4(p: Point, condition: Condition? = null): List<Point> = p.neighbors4().filter { valid(it, condition) }

    fun neighbors4(p: Point, c: Char): List<Point> = neighbors4(p) { it == c }

    fun neighbors4(x: Int, y: Int, condition: Condition? = null): List<Point> = neighbors4(Point(x, y), condition)

    fun neighbors8(p: Point, condition: Condition? = null): List<Point> = p.neighbors8().filter { valid(it, condition) }

    fun neighbors8(p: Point, c: Char): List<Point> = neighbors8(p) { it == c }

    fun neighbors8(x: Int, y: Int, condition: Condition? = null): List<Point> = neighbors8(Point(x, y), condition)

    fun show() {
        area.forEach { println(it) }
    }

    fun png(tiles: Tiles = Tiles.BASE) {
        showPng(this, tiles)
    }

    fun rows() = sequence { yRange.forEach { y -> yield(area[y]) } }

    fun columns() = sequence { xRange.forEach { x -> yield(yRange.map { y -> get(x, y) }) } }

    fun row(i: Int) = area[i]

    fun column(i: Int) = yRange.map { get(i, it) }.toCharArray()

    fun substring(y: Int, startIndex: Int, endIndex: Int) = area[y].concatToString(startIndex, endIndex)

    fun rotated(): CharArea {
        val inverted = CharArea(yRange.last + 1, xRange.last + 1, ' ')
        tiles().forEach { (x, y) -> inverted[y, x] = get(x, y) }
        return inverted
    }

    override fun toString(): String {
        return area.joinToString("\n") { it.joinToString("") }
    }

    override fun hashCode(): Int {
        return toString().hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is CharArea && toString() == other.toString()
    }
}
