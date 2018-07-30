package lx.own.example

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import lx.own.ownwidget.ParallaxImageView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var piv = findViewById<ParallaxImageView>(R.id.piv)
        piv.setImageResource(R.drawable.img)
    }
}
