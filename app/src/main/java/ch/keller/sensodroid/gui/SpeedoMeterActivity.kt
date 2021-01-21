package ch.keller.sensodroid.gui

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import ch.keller.sensodroid.MainActivity
import ch.keller.sensodroid.R
import ch.keller.sensodroid.services.SpeedService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class SpeedoMeterActivity : AppCompatActivity(), LocationListener {

    val MY_PERMISSIONS_REQUEST_LOCATION = 99
    lateinit var notificationSwitch: SwitchCompat
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationManager: LocationManager
    var speed = 0.0f
    var speedDisplay: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speedo_meter)

        startService(Intent(applicationContext, SpeedService::class.java))

        this.checkLocationPermissions()

        this.notificationSwitch = findViewById(R.id.speed_switch)
        this.notificationSwitch.setOnCheckedChangeListener { view, isChecked ->
            run {
                if (isChecked) {
                    this.setPrefs(true)
                }
                if (!isChecked) {
                    this.setPrefs(false)
                }
            }
        }

        this.locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            this.checkLocationPermissions()
        }
        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)

        this.speedDisplay = findViewById(R.id.speed)

        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    fun setPrefs(flag: Boolean) {
        val sharedPref = this.getSharedPreferences("strings", Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString("notification_is_active", "${flag}")
            apply()
        }
    }

    override fun onLocationChanged(location: Location) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1f, this)
        }

        if (location.speed <= 0) return

        this.speed = (Math.round(location.speed * 3.6 * 100.0) / 100.0).toFloat()
        this.speedDisplay?.text = "${this.speed}"
    }

    fun onCheckedChange() {}

    fun checkLocationPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder(this)
                    .setTitle(R.string.title_location_permission)
                    .setMessage(R.string.text_location_permission)
                    .setPositiveButton(R.string.ok, DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            MY_PERMISSIONS_REQUEST_LOCATION
                        )
                    })
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSIONS_REQUEST_LOCATION)
            }
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1f , this)
                    }
                } else {
                    val mainIntent = Intent(this, MainActivity::class.java)
                    startActivity(mainIntent)
                }
                return
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1f, this)
        }
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