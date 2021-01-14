package ch.keller.sensodroid.misc

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import java.text.DecimalFormat
import kotlin.math.abs

class ViewDrawer(context: Context?) : View(context) {
    var paint = Paint()
    private var orientation: FloatArray = FloatArray(3)
    private var lastOrientationZ = 0f
    private var canvasOrientation = 0f
    var df: DecimalFormat = DecimalFormat()



    public override fun onDraw(canvas: Canvas) {
        canvas.save()
        paint.color = Color.WHITE

        lastOrientationZ = zDegrees

        val diff = abs(canvasOrientation - lastOrientationZ).toDouble()

        if (abs(canvasOrientation - 1 - lastOrientationZ) < diff) {
            canvasOrientation -= if (diff > 5) {
                1f
            } else {
                0.1f
            }
        }
        if (abs(
                canvasOrientation + 1 - lastOrientationZ
            ) < diff
        ) {
            canvasOrientation += if (diff > 5) {
                1f
            } else {
                0.1f
            }
        }
       canvas.rotate(
            canvasOrientation,
            (this.width / 2).toFloat(),
            (this.height / 2).toFloat()
        )
        paint.color = Color.argb(150, 45, 140, 254)
        val r = Rect(-this.width, -this.height, this.width / 2, this.height * 2)
        canvas.drawRect(r, paint)
        canvas.restore()
        paint.color = Color.argb(90, 255, 255, 255)
        paint.textSize = 300f
        canvas.drawText(
            df!!.format((lastOrientationZ % 90).toDouble()),
            0.25.toFloat() * this.width,
            0.25.toFloat() * this.height,
            paint
        )
        paint.strokeWidth = 5f
        canvas.drawLine(
            (this.width / 2).toFloat(),
            0f,
            (this.width / 2).toFloat(),
            this.height.toFloat(),
            paint
        )
        canvas.drawLine(
            0f,
            (this.height / 2).toFloat(),
            this.width.toFloat(),
            (this.height / 2).toFloat(),
            paint
        )
        invalidate()
    }

    val zDegrees: Float
        get() = orientation!![2] % 360

    fun setOrientation(orientation: FloatArray) {
        this.orientation = orientation
    }
}