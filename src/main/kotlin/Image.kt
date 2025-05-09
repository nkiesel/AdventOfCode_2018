import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.io.path.createTempFile

const val tileSize = 22

private val baseTiles = mapOf(
    '*' to """
        ...........
        .*...*...*.
        ..*..*..*..
        ...*.*.*...
        ....***....
        .*********.
        ....***....
        ...*.*.*...
        ..*..*..*..
        .*...*...*.
        ...........
        """,
    '#' to """
        ***********
        ***********
        ***********
        ***********
        ***********
        ***********
        ***********
        ***********
        ***********
        ***********
        ***********
        """,
    'g' to """
        ***********
        ***********
        ***********
        ***********
        ***********
        ***********
        ***********
        ***********
        ***********
        ***********
        ***********
        """,
    ' ' to """
        ...........
        ...........
        ...........
        ...........
        ...........
        ...........
        ...........
        ...........
        ...........
        ...........
        ...........
        """,
    '~' to """
        ...........
        ...........
        ...........
        ...........
        ..***....*.
        .*...*...*.
        .*....***..
        ...........
        ...........
        ...........
        ...........
        """,
    '.' to """
        ...........
        ...........
        ...........
        ...........
        ...........
        .....*.....
        ...........
        ...........
        ...........
        ...........
        ...........
        """,
    'd' to """
        ...........
        ...........
        .....*.....
        ....***....
        ...*****...
        ..*******..
        ...*****...
        ....***....
        .....*.....
        ...........
        ...........
        """,
    '-' to """
        ...........
        ...........
        ...........
        ...........
        ***********
        ***********
        ***********
        ...........
        ...........
        ...........
        ...........
        """,
    '|' to """
        ....***....
        ....***....
        ....***....
        ....***....
        ....***....
        ....***....
        ....***....
        ....***....
        ....***....
        ....***....
        ....***....
        """,
    '+' to """
        ....***....
        ....***....
        ....***....
        ....***....
        ***********
        ***********
        ***********
        ....***....
        ....***....
        ....***....
        ....***....
        """,
    ' ' to """
        ...........
        ...........
        ...........
        ...........
        ...........
        ...........
        ...........
        ...........
        ...........
        ...........
        ...........
        """,
    '/' to """
        ..........*
        .........*.
        ........*..
        .......*...
        ......*....
        .....*.....
        ....*......
        ...*.......
        ..*........
        .*.........
        *..........
        """,
    '\\' to """
        *..........
        .*.........
        ..*........
        ...*.......
        ....*......
        .....*.....
        ......*....
        .......*...
        ........*..
        .........*.
        ..........*
        """,
).mapValues { toBufferedImage(it.key, it.value) }

private val pathTiles = mapOf(
    'L' to """
        ....***....
        ....***....
        ....***....
        ....***....
        ....*******
        ....*******
        ....*******
        ...........
        ...........
        ...........
        ...........
        """,
    '7' to """
        ...........
        ...........
        ...........
        ...........
        *******....
        *******....
        *******....
        ....***....
        ....***....
        ....***....
        ....***....
        """,
    'S' to """
        ...........
        ...........
        ...........
        ...........
        ....*******
        ....*******
        ....*******
        ....***....
        ....***....
        ....***....
        ....***....
        """,
    'F' to """
        ...........
        ...........
        ...........
        ...........
        ....*******
        ....*******
        ....*******
        ....***....
        ....***....
        ....***....
        ....***....
        """,
    'J' to """
        ....***....
        ....***....
        ....***....
        ....***....
        *******....
        *******....
        *******....
        ...........
        ...........
        ...........
        ...........
        """,
).mapValues { toBufferedImage(it.key, it.value) }

private val digitTiles = mapOf(
    '1' to """
        ......................
        ......................
        .........****.........
        .......******.........
        ......***.***.........
        .....***..***.........
        ....***...***.........
        ...***....***.........
        ..........***.........
        ..........***.........
        ..........***.........
        ..........***.........
        ..........***.........
        ..........***.........
        ..........***.........
        ..........***.........
        ..........***.........
        ..........***.........
        .....**************...
        .....**************...
        ......................
        ......................
        """,
    '2' to """
        ......................
        ......................
        ........******........
        .......*********......
        ......***.....***.....
        .....***.......***....
        ....***.........***...
        ...***.........***....
        ...***........***.....
        .............***......
        ............***.......
        ...........***........
        ..........***.........
        .........***..........
        ........***...........
        .......***............
        ......***.............
        .....***..............
        ....*************.....
        ....*************.....
        ......................
        ......................
        """,
    '3' to """
        ......................
        ......................
        ...**************.....
        ...*************......
        ............***.......
        ...........***........
        ..........***.........
        .........***..........
        ..........***.........
        ...........***........
        ............***.......
        .............***......
        ..............***.....
        ...............***....
        ................***...
        ...............***....
        ...***.......***......
        ....***.....***.......
        .....********.........
        ........***...........
        ......................
        ......................
        """,
    '4' to """
        ......................
        ......................
        ......................
        ................***...
        ...............***....
        ..............***.....
        .............***......
        ............***.......
        ...........***........
        ..........***.........
        .........***..........
        ........***.....***...
        .......***......***...
        ......***.......***...
        .....***........***...
        ....***.........***...
        ...****************...
        ...****************...
        ................***...
        ................***...
        ......................
        ......................
        """,
    '5' to """
        ......................
        ......................
        ...**************.....
        ...*************......
        ...***................
        ...***................
        ...***................
        ...***................
        ....***...............
        ......***.............
        ........***...........
        ..........***.........
        ............***.......
        .............***......
        ...***........***.....
        ....***.......****....
        .....***......***.....
        ......***.....***.....
        .......*********......
        .........****.........
        ......................
        ......................
        """,
    '6' to """
        ......................
        ......................
        .............***......
        ............***.......
        ...........***........
        ..........***.........
        .........***..........
        ........***...........
        .......***............
        ......***.............
        .....***..............
        ....***..****.........
        ...************.......
        ..****........***.....
        ..****.........***....
        ...***........***.....
        ....***......***......
        .....***....***.......
        ......********........
        .......******.........
        ......................
        ......................
        """,
    '7' to """
        ......................
        ......................
        ...**************.....
        ...**************.....
        ...***.......***......
        ...***.......***......
        ...***......***.......
        ............***.......
        ...........***........
        ...........***........
        ..........***.........
        ..........***.........
        .........***..........
        .........***..........
        ........***...........
        ........***...........
        .......***............
        .......***............
        ......***.............
        ......***.............
        ......................
        ......................
        """,
    '8' to """
        ......................
        ......................
        .......******.........
        .....**********.......
        ....***.......***.....
        ...***.........***....
        ...***.........***....
        ...***.........***....
        ....****.....****.....
        .....***********......
        ....*************.....
        ...****.......****....
        ...***..........***...
        ...***..........***...
        ...***..........***...
        ...***..........***...
        ....***........***....
        ....***........***....
        .....************.....
        ......**********......
        ......................
        ......................
        """,
    '9' to """
        ......................
        ......................
        .......********.......
        .....************.....
        ....****......****....
        ....***........***....
        ....***........***....
        .....*****...****.....
        ......***********.....
        ...........*****......
        ............****......
        ............***.......
        ............***.......
        ...........***........
        ...........***........
        ..........***.........
        ..........***.........
        .........***..........
        .........***..........
        ........***...........
        ......................
        ......................
        """,
    '0' to """
        ......................
        ......................
        .......*******........
        .....***********......
        ...***........***.....
        ..***..........***....
        ..***...........***...
        ..***...........***...
        ..***...........***...
        ..***...........***...
        ..***...........***...
        ..***...........***...
        ..***...........***...
        ..***...........***...
        ..***...........***...
        ..***..........***....
        ...***........***.....
        ....***......***......
        .....**********.......
        ......********........
        ......................
        ......................
        """,
).mapValues { toBufferedImage(it.key, it.value) }

private val egTiles = mapOf(
    'E' to """
        ...........
        ..*******..
        ..**.......
        ..**.......
        ..**.......
        ..*******..
        ..**.......
        ..**.......
        ..**.......
        ..*******..
        ...........
        """,
    'G' to """
        ...........
        ..******...
        .**.....*..
        .**........
        .**........
        .**..****..
        .**.....*..
        .**.....*..
        .**.....*..
        ...*****...
        ...........
        """,
).mapValues { toBufferedImage(it.key, it.value) }

enum class Tiles {
    BASE, PATH, DIGIT, EG
}

private fun toBufferedImage(char: Char, data: String): BufferedImage {
    val charImage = BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB)
    val dot = Color.YELLOW.rgb
    val star = when (char) {
        'b' -> Color.BLACK.rgb
        'w' -> Color.WHITE.rgb
        'g' -> Color.GREEN.rgb
        else -> Color.RED.rgb
    }
    val lines = data.trimIndent().lines()
    val rep = tileSize / lines.size
    fun setRGB(x: Int, y: Int, rgb: Int) {
        for (dx in 0 until rep) {
            for (dy in 0 until rep) {
                charImage.setRGB(x * rep + dx, y * rep + dy, rgb)
            }
        }
    }
    lines.forEachIndexed { y, line ->
        line.forEachIndexed { x, c ->
            when (c) {
                '.' -> setRGB(x, y, dot)
                '*' -> setRGB(x, y, star)
            }
        }
    }
    return charImage
}

fun showPng(area: CharArea, tiles: Tiles) {
    val out = createTempFile(suffix = ".png").toFile()
    toPng(area, tiles, out)
    val pngViewer = System.getenv("PNG_VIEWER") ?: with(System.getProperty("os.name")) {
        when {
            startsWith("Mac") -> "open"
            startsWith("Windows") -> "explorer"
            else -> "loupe"
        }
    }
    ProcessBuilder(pngViewer, out.path).start().waitFor()
    out.delete()
}

private fun toPng(area: CharArea, tiles: Tiles, output: File) {
    val image = BufferedImage(
        (area.xRange.endInclusive + 1) * tileSize,
        (area.yRange.endInclusive + 1) * tileSize,
        BufferedImage.TYPE_INT_ARGB
    )
    val graphics = image.graphics

    val t = when (tiles) {
        Tiles.BASE -> baseTiles
        Tiles.PATH -> baseTiles + pathTiles
        Tiles.DIGIT -> baseTiles + digitTiles
        Tiles.EG -> baseTiles + egTiles
    }
    area.tiles().forEach { p ->
        t[area[p]]?.let { graphics.drawImage(it, p.x * tileSize, p.y * tileSize, null) }
    }

    ImageIO.write(image, "png", output)
}
