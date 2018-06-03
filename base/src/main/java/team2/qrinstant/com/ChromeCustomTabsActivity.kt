package team2.qrinstant.com

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class ChromeCustomTabsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
//        val google = findViewById(android.R.id.google_btn) as Button
//        val vivint = findViewById(R.id.vivint_btn) as Button
//        val geek = findViewById(R.id.geek_btn) as Button
//        val ksl = findViewById(R.id.ksl_btn) as Button
//        val parallax = findViewById(R.id.parallax_btn) as Button
//
//        google.setOnClickListener {
//            loadCustomTabForSite("http://google.com")
//        }
//
//        vivint.setOnClickListener {
//            loadCustomTabForSite("http://vivintsky.com", resources.getColor(R.color.vivint_orange));
//        }
//
//        geek.setOnClickListener {
//            loadCustomTabForSite("http://www.dividendgeek.com", resources.getColor(R.color.colorPrimary));
//        }
//
//        ksl.setOnClickListener {
//            loadCustomTabForSite("http://www.ksl.com", resources.getColor(R.color.colorAccent))
//        }
//
//        parallax.setOnClickListener {
//            loadCustomTabForSite("http://matthew.wagerfield.com/parallax/", Color.RED);
//        }
    }

    companion object {

        private val CUSTOM_TAB_PACKAGE_NAME: String = "com.qrinstant.chrome"
        private val QRCODE_EXTRA_KEY: String = "qrcode_extra_key"

        fun newIntent(context: Context, qrcode: String): Intent {
            val intent = Intent(context, ChromeCustomTabsActivity::class.java)
            intent.putExtra(QRCODE_EXTRA_KEY, qrcode)
            return intent
        }
    }
}