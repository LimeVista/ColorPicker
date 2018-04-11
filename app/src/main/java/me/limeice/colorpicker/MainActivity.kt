package me.limeice.colorpicker

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mOpen.setOnClickListener { ColorPickerDialog(this).openDialog(0xff4c5a79.toInt()) }
    }
}
