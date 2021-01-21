package ch.keller.sensodroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import ch.keller.sensodroid.gui.CompassActivity
import ch.keller.sensodroid.gui.SpeedoMeterActivity
import ch.keller.sensodroid.gui.SpiritLevelActivity

/**
 *
 * NOTE: This class was mainly inspired by https://github.com/jwpaine/Spirit-Level/blob/master/app/src/main/java/marketplace/spiritlevel2/DrawView.java
 * It is not really as how I wanted to create my Spirit Level, but due to time issues, I do not really have a lot of options.
 * Still it somewhat works. My Class is almost the same, but written in Kotlin with few less features than the original.
 *
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun openCompass(view: View) {
        val compassIntent = Intent(this, CompassActivity::class.java)
        startActivity(compassIntent)
    }

    fun openBubbleLevel(view: View) {
        val bubbleIntent = Intent(this, SpiritLevelActivity::class.java)
        startActivity(bubbleIntent)
    }

    fun openSpeedOMeter(view: View) {
        val speedOMeterIntent = Intent(this, SpeedoMeterActivity::class.java)
        startActivity(speedOMeterIntent)
    }
}