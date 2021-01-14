package ch.keller.sensodroid.gui

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ch.keller.sensodroid.R
import java.lang.Math.toDegrees
import kotlin.math.abs

class CompassActivity : AppCompatActivity(), SensorEventListener {

    var compassImage: ImageView? = null
    var degreeField: TextView? = null

    var sensorManager: SensorManager? = null
    var accelerometer: Sensor? = null
    var magneticFieldReader: Sensor? = null

    var magneticFieldValues: FloatArray = FloatArray(3)
    var accelerometerValues: FloatArray = FloatArray(3)
    var accelerometerValuesSet: Boolean = false
    var magneticFieldValuesSet: Boolean = false

    var currentDirection = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compass)

        this.compassImage = findViewById(R.id.compass_image)
        this.degreeField = findViewById(R.id.degrees) as TextView
        this.degreeField?.text = "0.0 °"

        this.sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        this.registerSensors()
    }

    override fun onResume() {
        super.onResume()
        this.sensorManager?.registerListener(this, this.accelerometer, SensorManager.SENSOR_DELAY_GAME)
        this.sensorManager?.registerListener(this, this.magneticFieldReader, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
        this.sensorManager?.unregisterListener(this, this.accelerometer)
        this.sensorManager?.unregisterListener(this, this.magneticFieldReader)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            this.accelerometerValues = event.values
            lowPass(event.values, this.accelerometerValues)
            this.accelerometerValuesSet = true
        }

        if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
            this.magneticFieldValues = event.values
            lowPass(event.values, this.magneticFieldValues)
            this.magneticFieldValuesSet = true
        }

        if (this.accelerometerValuesSet && this.magneticFieldValuesSet) {
            val r = FloatArray(9)
            if (SensorManager.getRotationMatrix(r, null, this.accelerometerValues, this.magneticFieldValues)) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(r, orientation)
                val degree = (toDegrees(orientation[0].toDouble()) + 360).toFloat() % 360

                val rotateAnimation = RotateAnimation(
                    this.currentDirection,
                    -degree,
                    RELATIVE_TO_SELF, 0.5f,
                    RELATIVE_TO_SELF, 0.5f)
                rotateAnimation.duration = 1000
                rotateAnimation.fillAfter = true

                this.compassImage?.startAnimation(rotateAnimation)
                this.currentDirection = -degree
                this.degreeField?.text = "${abs(this.currentDirection)} °"
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        /**
         * not needed!
         */
    }

    fun registerSensors() {
        this.accelerometer = this.sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        this.magneticFieldReader = this.sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    fun lowPass(input: FloatArray, output: FloatArray) {
        val alpha = 0.05f

        for (i in input.indices) {
            output[i] = output[i] + alpha * (input[i] - output[i])
        }
    }
}