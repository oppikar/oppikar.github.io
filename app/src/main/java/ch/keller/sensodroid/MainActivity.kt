package ch.keller.sensodroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import ch.keller.sensodroid.gui.CompassActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun openCompass(view: View) {
        val compassIntent: Intent = Intent(this, CompassActivity::class.java)
        startActivity(compassIntent)
    }
}