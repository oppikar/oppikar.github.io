package ch.keller.sensodroid.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Observable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import ch.keller.sensodroid.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class SpeedService : Service(), LocationListener {

    var isNotificationActive = false
    lateinit var locationManager: LocationManager
    var speed: Float = 0.0f
    lateinit var notificationManager: NotificationManager
    lateinit var notification: NotificationCompat.Builder
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var sharedPref: SharedPreferences


    val CHANNEL_ID = "speed_channel"
    val CHANNEL_NAME = "Tachometer"


    override fun onCreate() {
        super.onCreate()
        this.sharedPref = getSharedPreferences("strings", Context.MODE_PRIVATE)
        this.notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        this.notificationManager.createNotificationChannel(
            NotificationChannel(
                this.CHANNEL_ID,
                this.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )

        this.locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)

        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        this.initiateLocationReading()

        this.checkWhetherNotificationShouldBeSetAndSet()

    }

    fun createNotification() {
        this.notification = NotificationCompat.Builder(this, this.CHANNEL_ID)
            .setContentTitle("Tempo")
            .setContentText("${this.speed} km/h")
            .setSmallIcon(R.drawable.ic_speedometer)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        this.notificationManager.notify(1, this.notification.build())

        this.isNotificationActive = true
    }

    fun checkWhetherNotificationShouldBeSetAndSet() {
        if (this.sharedPref.getString("notification_is_active", false.toString()).toBoolean()) {
            this.createNotification()
        }
    }

    fun dismissNotifications() {
        this.notificationManager.cancelAll()
        this.isNotificationActive = false
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onLocationChanged(location: Location) {
        this.checkWhetherNotificationShouldBeSetAndSet()
        if (location.speed <= 0) return
        this.getSpeed(location.speed)
    }

    fun getSpeed(speed: Float) {
        this.speed =
            (Math.round(speed * 3.6 * 100.0) / 100.0).toFloat() // since location.speed is given in m/s
        this.setToPref(this.speed)
        if (!this::notification.isInitialized) return
        this.notification.setContentText("${this.speed} km/h")
        this.notificationManager.notify(1, this.notification.build())
    }

    fun setToPref(speed: Float) {
        with(this.sharedPref.edit()) {
            putString("current_speed", "${speed}")
            apply()
        }
    }

    fun initiateLocationReading() {
        this.locationManager =
            applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
    }

    fun activatePermaSpeedNotification() {
        this.notification = NotificationCompat.Builder(this, this.CHANNEL_ID)
            .setContentTitle("Tempo")
            .setContentText("${this.speed} km/h")
            .setSmallIcon(R.drawable.ic_speedometer)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        this.notificationManager.notify(0, this.notification.build())

        this.isNotificationActive = true
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1f, this)
        }
    }
}