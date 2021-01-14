package ch.keller.sensodroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import ch.keller.sensodroid.gui.CompassActivity
import ch.keller.sensodroid.gui.SpiritLevelActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun openCompass(view: View) {
        val compassIntent: Intent = Intent(this, CompassActivity::class.java)
        startActivity(compassIntent)
    }

    fun openBubbleLevel(view: View) {
        val bubbleIntent: Intent = Intent(this, SpiritLevelActivity::class.java)
        startActivity(bubbleIntent)
    }
}