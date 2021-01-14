package ch.keller.sensodroid.gui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ch.keller.sensodroid.R

class CompassActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compass)
    }
}