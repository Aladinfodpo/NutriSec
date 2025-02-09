package com.example.android.architecture.blueprints.nutrisecapp.statistics

import androidx.compose.ui.graphics.Path
import kotlin.math.max
import kotlin.math.min

data class Point(val x: Float, val y: Float){
    operator fun unaryMinus() = Point(-x, -y)
    operator fun plus(p2: Point) = Point(x + p2.x, y + p2.y)
    operator fun plus(c: Float) = Point(x + c, y + c)
    operator fun minus(p2: Point) : Point = this + (-p2)
    operator fun minus(c: Float) = this + (-c)
    operator fun times(p2: Point) = Point(x * p2.x, y * p2.y)
    operator fun times(c: Float) = Point(x * c, y * c)
    operator fun div(p2: Point) = Point(x / p2.x, y / p2.y)
    operator fun div(c: Float) = this * (1.0F/c)

}


data class BezierCubic(val p0 : Point, val c0 : Point, val c1 : Point, val p1 : Point){
    constructor(points: List<Point>): this(
            p0 = points[1],
            p1 = points[2],
            c0 = points[1] + ((points[2] - points[1]) * 1.5F + (points[1] - points[0]) * 0.5f) / 6.0F,
            c1 = points[2] - ((points[3] - points[2]) * 1.5F + (points[2] - points[1]) * 0.5F) / 6.0F
    )

    fun draw(curve: Path, offset : Point, coef : Point, linear: Boolean = false) : Path {
        val m0 = p0 * coef + offset
        val m1 = c0 * coef + offset
        val m2 = c1 * coef + offset
        val m3 = p1 * coef + offset
        curve.moveTo(m0.x, m0.y)
        if(linear)
            curve.lineTo(m3.x, m3.y)
        else
            curve.cubicTo(m1.x, m1.y, m2.x, m2.y, m3.x, m3.y)
        return curve
    }

    companion object{
        fun drawCurve(points: List<Point>, offset : Point, coef : Point, linear: Boolean = false): Path{
            var curveRes = Path()
            for( i in 0..<points.size-1){
                curveRes = BezierCubic(listOf(points[max(0,i-1)], points[i], points[i+1], points[min(points.size-1, i+2)])).draw(curveRes, offset, coef, linear)
            }
            return curveRes
        }
    }


}