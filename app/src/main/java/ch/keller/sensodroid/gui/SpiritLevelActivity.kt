package ch.keller.sensodroid.gui

import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ch.keller.sensodroid.R
import ch.keller.sensodroid.misc.ViewDrawer
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class SpiritLevelActivity : AppCompatActivity(), SensorEventListener {

    var sensorManager: SensorManager? = null

    var gyroscope: Sensor? = null
    var rotationVector: Sensor? = null
    var rotationMatrix: FloatArray = FloatArray(16)
    var mappedCoordinates: FloatArray = FloatArray(16)
    var orientation: FloatArray = FloatArray(3)
    var scheduler: ScheduledExecutorService? = null
    var viewDrawer: ViewDrawer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.scheduler = Executors.newScheduledThreadPool(1)
        this.initViewUpdater()

        this.sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        this.registerSensors()

        this.viewDrawer = ViewDrawer(this)
        this.viewDrawer!!.setBackgroundColor(Color.argb(255, 0,0,0))
        if (this.viewDrawer != null) {
            setContentView(this.viewDrawer)
        }
    }

    override fun onResume() {
        super.onResume()
        this.sensorManager?.registerListener(this, this.gyroscope, SensorManager.SENSOR_DELAY_NORMAL)
        this.sensorManager?.registerListener(this, this.rotationVector, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun initViewUpdater() {
        this.scheduler?.scheduleAtFixedRate({
            run {
                runOnUiThread {
                    updateView()
                }
            }
        }, 0, 100, TimeUnit.MILLISECONDS)
    }

    fun updateView() {
        this.viewDrawer?.setOrientation(this.orientation)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        /**
         * not needed at all!
         */
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(this.rotationMatrix, event.values)

            SensorManager.remapCoordinateSystem(this.rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Y, this.mappedCoordinates)

            SensorManager.getOrientation(this.mappedCoordinates, this.orientation)
            this.orientation = this.orientation.map { Math.toDegrees(it.toDouble()).toFloat() }.toFloatArray()
        }
    }

    fun registerSensors() {
        this.gyroscope = this.sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        this.rotationVector = this.sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    }
}