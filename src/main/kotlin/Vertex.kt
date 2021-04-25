import kotlin.random.Random

data class Vertex(var x: Double, var y: Double, var z: Double) {

    operator fun plus(b: Vertex) = Vertex(x + b.x, y + b.y, z + b.z)

    operator fun minus(b: Vertex) = Vertex(x - b.x, y - b.y, z - b.z)

    operator fun times(b: Number) = Vertex(x * b.toDouble(), y * b.toDouble(), z * b.toDouble())

    operator fun div(b: Number) = Vertex(x / b.toDouble(), y / b.toDouble(), z / b.toDouble())

    override fun toString(): String = "($x, $y, $z)"

    override fun hashCode(): Int {
        return super.hashCode()
    }

    companion object {
        fun random(from: Double, until: Double): Vertex =
            Vertex(
                Random.nextDouble(from, until),
                Random.nextDouble(from, until),
                Random.nextDouble(from, until)
            )
    }
}