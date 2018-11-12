package nanz.com.majliskuadmin

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val context: Context = this

        Handler().postDelayed(object : Runnable{
            override fun run() {
                startActivity(Intent(context, MainActivity::class.java))
                finish()
            }
        }, 2000)
    }
}
