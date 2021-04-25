import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JPanel
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random

class WindowDetectionTest : JPanel() {
    private val points = ArrayList<Vertex>()
    private val pointCodes = ArrayList<Byte>()
    var windowHeight = 100
    var windowWidth = 200
    var windowPosY = 0
    var windowPosX = 0

    init {
        preferredSize = Dimension(640, 640)
    }

    fun regenerate() {
        pointCodes.clear()
        points.clear()
        repeat(60) {
            points.add(Vertex.random(-450.0, 450.0))
        }
        repeat(points.size) {
            pointCodes.add(0)
        }
    }

    private fun drawFigureFrom(g: Graphics2D, p1: Vertex, p2: Vertex) {
        val axis = p2 - p1
        val phi = atan2(axis.y, axis.x)
        val theta = atan2(sqrt(axis.x * axis.x + axis.y * axis.y), axis.z)
        val fov = sqrt(axis.x * axis.x + axis.y * axis.y + axis.z * axis.z)

        val objectMatrix = Matrix(points.size + 4, 4)
        for (i in points.indices) {
            objectMatrix[i + 1, 1] = points[i].x
            objectMatrix[i + 1, 2] = points[i].y
            objectMatrix[i + 1, 3] = points[i].z
            objectMatrix[i + 1, 4] = 1.0
        }

        objectMatrix[points.size + 1, 1] = 0.0
        objectMatrix[points.size + 1, 2] = 0.0
        objectMatrix[points.size + 1, 3] = 0.0
        objectMatrix[points.size + 1, 4] = 1.0
        objectMatrix[points.size + 2, 1] = 100.0
        objectMatrix[points.size + 2, 2] = 0.0
        objectMatrix[points.size + 2, 3] = 0.0
        objectMatrix[points.size + 2, 4] = 1.0
        objectMatrix[points.size + 3, 1] = 0.0
        objectMatrix[points.size + 3, 2] = 100.0
        objectMatrix[points.size + 3, 3] = 0.0
        objectMatrix[points.size + 3, 4] = 1.0
        objectMatrix[points.size + 4, 1] = 0.0
        objectMatrix[points.size + 4, 2] = 0.0
        objectMatrix[points.size + 4, 3] = 100.0
        objectMatrix[points.size + 4, 4] = 1.0

        val preparationTransform = TransformMatrixFabric.translate(-p1.x, -p1.y, -p1.z) *
                TransformMatrixFabric.rotateZ(-phi) *
                TransformMatrixFabric.rotateY(-theta) *
                TransformMatrixFabric.rotateZ(PI / 2)

        val resultMatrix = objectMatrix * preparationTransform * TransformMatrixFabric.scale(
            1.0,
            1.0,
            0.0
        )
        g.translate(100, 100)
        g.color = Color.GRAY
        for (i in (points.size + 2)..(points.size + 4)) {
            val xy1 = Vertex(
                resultMatrix[(points.size + 1), 1],
                resultMatrix[(points.size + 1), 2],
                resultMatrix[(points.size + 1), 3]
            )
            val xy2 = Vertex(resultMatrix[i, 1], resultMatrix[i, 2], resultMatrix[i, 3])
            g.drawLine(
                xy1.x.roundToInt(), xy1.y.roundToInt(),
                xy2.x.roundToInt(), xy2.y.roundToInt()
            )
        }

        g.translate(width / 2 - 100, height / 2 - 100)

        val limitNorth = windowPosY - windowHeight / 2
        val limitSouth = windowPosY + windowHeight / 2
        val limitWest = windowPosX - windowWidth / 2
        val limitEast = windowPosX + windowWidth / 2

        g.color = Color.RED
        g.drawLine(limitWest, limitNorth, limitEast, limitNorth)
        g.drawLine(limitEast, limitNorth, limitEast, limitSouth)
        g.drawLine(limitWest, limitSouth, limitEast, limitSouth)
        g.drawLine(limitWest, limitSouth, limitWest, limitNorth)

        for (i in points.indices) {
            if (resultMatrix[i + 1, 1] < limitWest)
                pointCodes[i] = (pointCodes[i] + 8.toByte()).toByte()
            else if (resultMatrix[i + 1, 1] > limitEast)
                pointCodes[i] = (pointCodes[i] + 4.toByte()).toByte()
            if (resultMatrix[i + 1, 2] > limitSouth)
                pointCodes[i] = (pointCodes[i] + 2.toByte()).toByte()
            else if (resultMatrix[i + 1, 2] < limitNorth)
                pointCodes[i] = (pointCodes[i] + 1.toByte()).toByte()
        }

        for (i in 0 until points.size step 2) {
            when {
                (pointCodes[i].or(pointCodes[i + 1])) == 0.toByte() -> {
                    g.color = Color(0, 200, 0)
                    g.drawLine(
                        resultMatrix[i + 1, 1].roundToInt(),
                        resultMatrix[i + 1, 2].roundToInt(),
                        resultMatrix[i + 2, 1].roundToInt(),
                        resultMatrix[i + 2, 2].roundToInt()
                    )
                }
                (pointCodes[i].and(pointCodes[i + 1])) != 0.toByte() -> {
                    g.color = Color(200, 0, 200)
                    g.drawLine(
                        resultMatrix[i + 1, 1].roundToInt(),
                        resultMatrix[i + 1, 2].roundToInt(),
                        resultMatrix[i + 2, 1].roundToInt(),
                        resultMatrix[i + 2, 2].roundToInt()
                    )
                }
                else -> {
                    val deltaX = resultMatrix[i + 2, 1] - resultMatrix[i + 1, 1]
                    val deltaY = resultMatrix[i + 2, 2] - resultMatrix[i + 1, 2]
                    val tt = ArrayList<Double>()
                    var t = (limitWest - resultMatrix[i + 1, 1]) / deltaX
                    if ((resultMatrix[i + 1, 2] + t * deltaY).roundToInt() in limitNorth..limitSouth && t in 0.0..1.0)
                        tt += t
                    t = (limitEast - resultMatrix[i + 1, 1]) / deltaX
                    if ((resultMatrix[i + 1, 2] + t * deltaY).roundToInt() in limitNorth..limitSouth && t in 0.0..1.0)
                        tt += t
                    t = (limitNorth - resultMatrix[i + 1, 2]) / deltaY
                    if ((resultMatrix[i + 1, 1] + t * deltaX).roundToInt() in limitWest..limitEast && t in 0.0..1.0)
                        tt += t
                    t = (limitSouth - resultMatrix[i + 1, 2]) / deltaY
                    if ((resultMatrix[i + 1, 1] + t * deltaX).roundToInt() in limitWest..limitEast && t in 0.0..1.0)
                        tt += t

                    when (tt.size) {
                        2 -> {
                            g.color = Color.WHITE
                            g.drawLine(
                                resultMatrix[i + 1, 1].roundToInt(),
                                resultMatrix[i + 1, 2].roundToInt(),
                                (resultMatrix[i + 1, 1] + deltaX * tt.minOrNull()!!).roundToInt(),
                                (resultMatrix[i + 1, 2] + deltaY * tt.minOrNull()!!).roundToInt()
                            )
                            g.drawLine(
                                resultMatrix[i + 2, 1].roundToInt(),
                                resultMatrix[i + 2, 2].roundToInt(),
                                (resultMatrix[i + 1, 1] + deltaX * tt.maxOrNull()!!).roundToInt(),
                                (resultMatrix[i + 1, 2] + deltaY * tt.maxOrNull()!!).roundToInt()
                            )
                            g.color = Color(100, 100, 0)
                            g.drawLine(
                                (resultMatrix[i + 1, 1] + deltaX * tt.minOrNull()!!).roundToInt(),
                                (resultMatrix[i + 1, 2] + deltaY * tt.minOrNull()!!).roundToInt(),
                                (resultMatrix[i + 1, 1] + deltaX * tt.maxOrNull()!!).roundToInt(),
                                (resultMatrix[i + 1, 2] + deltaY * tt.maxOrNull()!!).roundToInt()
                            )
                        }
                        1 -> {
                            if (pointCodes[i] == 0.toByte()) {
                                g.color = Color(100, 100, 0)
                                g.drawLine(
                                    resultMatrix[i + 1, 1].roundToInt(),
                                    resultMatrix[i + 1, 2].roundToInt(),
                                    (resultMatrix[i + 1, 1] + deltaX * tt[0]).roundToInt(),
                                    (resultMatrix[i + 1, 2] + deltaY * tt[0]).roundToInt()
                                )
                                g.color = Color.WHITE
                                g.drawLine(
                                    resultMatrix[i + 2, 1].roundToInt(),
                                    resultMatrix[i + 2, 2].roundToInt(),
                                    (resultMatrix[i + 1, 1] + deltaX * tt[0]).roundToInt(),
                                    (resultMatrix[i + 1, 2] + deltaY * tt[0]).roundToInt()
                                )
                            } else {
                                g.color = Color(100, 100, 0)
                                g.drawLine(
                                    resultMatrix[i + 2, 1].roundToInt(),
                                    resultMatrix[i + 2, 2].roundToInt(),
                                    (resultMatrix[i + 1, 1] + deltaX * tt[0]).roundToInt(),
                                    (resultMatrix[i + 1, 2] + deltaY * tt[0]).roundToInt()
                                )
                                g.color = Color.WHITE
                                g.drawLine(
                                    resultMatrix[i + 1, 1].roundToInt(),
                                    resultMatrix[i + 1, 2].roundToInt(),
                                    (resultMatrix[i + 1, 1] + deltaX * tt[0]).roundToInt(),
                                    (resultMatrix[i + 1, 2] + deltaY * tt[0]).roundToInt()
                                )
                            }
                        }
                        else -> {
                            g.color = Color.WHITE
                            g.drawLine(
                                resultMatrix[i + 1, 1].roundToInt(),
                                resultMatrix[i + 1, 2].roundToInt(),
                                resultMatrix[i + 2, 1].roundToInt(),
                                resultMatrix[i + 2, 2].roundToInt()
                            )
                        }
                    }
                }
            }
        }
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)

        val gg = g as Graphics2D
        background = Color.BLACK
        drawFigureFrom(gg, Vertex(50.0, 50.0, 50.0), Vertex(40.0, 40.0, 40.0))
    }

    fun generateRandomVertices(count: Int) {
        points.clear()
        pointCodes.clear()
        repeat(count) {
            val from = -450.0
            val until = 450.0
            val randomUntil: Double
            val intervalLength: Double = Random.nextDouble(200.0, 400.0)
            val randomFrom: Double = Random.nextDouble(from, until)
            randomUntil = if (intervalLength + randomFrom > 450) 450.0 else intervalLength + randomFrom
            points.add(Vertex.random(randomFrom, randomUntil))
        }
        repeat(points.size) {
            pointCodes.add(0)
        }
    }
}