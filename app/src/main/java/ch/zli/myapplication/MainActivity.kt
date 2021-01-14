package ch.zli.myapplication

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    var counter: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.counter = getSharedPreferences("integers", Context.MODE_PRIVATE).getInt("counter", 0)
    }

    fun subtract(view: View) {
        this.counter--
        this.setValue()
    }

    fun add(view: View) {
        this.counter++
        this.setValue()
    }

    fun setValue() {
        val amount: TextView = findViewById(R.id.textView)
        amount.text = "${this.counter}"
        this.setPrefs()
    }

    override fun onPause() {
        super.onPause()
        this.setPrefs()
    }

    override fun onStop() {
        super.onStop()
        this.setPrefs()
    }

    override fun onStart() {
        super.onStart()
        this.counter = getSharedPreferences("integers", Context.MODE_PRIVATE).getInt("counter", 0)
        val view: TextView = findViewById(R.id.textView)
        view.text = getSharedPreferences("integers", Context.MODE_PRIVATE).getInt("counter", 0).toString()
    }

    fun reset(view: View) {
        this.counter = 0
        this.setValue()
    }

    private fun setPrefs() {

        val count: Int = this.counter

        val sharedPref = this.getSharedPreferences("integers", Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putInt("counter", count)
            apply()
        }
    }
}