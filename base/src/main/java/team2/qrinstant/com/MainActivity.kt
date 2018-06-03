package team2.qrinstant.com

import android.Manifest
import android.content.ComponentName
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.customtabs.*
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.budiyev.android.codescanner.*
import com.google.zxing.Result

class MainActivity : AppCompatActivity() {

    companion object {
        private val CUSTOM_TAB_PACKAGE_NAME: String = "com.qrinstant.chrome"
    }

    private val MY_PERMISSIONS_REQUEST_CAMERA: Int = 1

    private var mCodeScanner: CodeScanner? = null
    private var mCustomTabsServiceConnection: CustomTabsServiceConnection? = null
    private var mClient: CustomTabsClient? = null

    private var mCustomTabsSession: CustomTabsSession? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initCustomTabs();
    }

    override fun onResume() {
        super.onResume()
        requestCameraPermission()
    }

    override fun onPause() {
        super.onPause()
        mCodeScanner!!.releaseResources()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CAMERA -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    openCamera()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this@MainActivity, "Please grant!", Toast.LENGTH_LONG).show()
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun initCustomTabs() {
        mCustomTabsServiceConnection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(componentName: ComponentName, customTabsClient: CustomTabsClient) {
                //Pre-warming
                mClient = customTabsClient
                mClient?.warmup(0L)
                mCustomTabsSession = mClient?.newSession(object : CustomTabsCallback() {
                    override fun onNavigationEvent(navigationEvent: Int, extras: Bundle?) {
                        Log.w(">>>", "onNavigationEvent: Code = $navigationEvent")

                        when (navigationEvent) {
                            NAVIGATION_STARTED -> {
                            }
                            NAVIGATION_FINISHED -> {
                                Log.d(">>>", "NAVIGATION_FINISHED")
                            }
                            NAVIGATION_FAILED -> {
                                Log.d(">>>", "NAVIGATION_FAILED")
                            }
                            NAVIGATION_ABORTED -> {
                                Log.d(">>>", "NAVIGATION_ABORTED")
                            }
                        }
                    }
                })
            }

            override fun onServiceDisconnected(name: ComponentName) {
                mClient = null
            }
        }

        CustomTabsClient.bindCustomTabsService(this, CUSTOM_TAB_PACKAGE_NAME, mCustomTabsServiceConnection)
    }

    private fun requestCameraPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.CAMERA),
                        MY_PERMISSIONS_REQUEST_CAMERA)

                // MY_PERMISSIONS_REQUEST_CAMERA is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            openCamera()
        }

    }

    private fun openCamera() {
        if (mCodeScanner == null) {

            val scannerView: CodeScannerView = findViewById(R.id.scanner_view)
            // Use builder
            mCodeScanner = CodeScanner.builder()
                    /*camera can be specified by calling .camera(cameraId),
                        first back-facing camera on the device by default*/
                    /*code formats*/
                    .formats(CodeScanner.ALL_FORMATS)/*List<BarcodeFormat>*/
                    /*or .formats(BarcodeFormat.QR_CODE, BarcodeFormat.DATA_MATRIX, ...)*/
                    /*or .format(BarcodeFormat.QR_CODE) - only one format*/
                    /*auto focus*/
                    .autoFocus(true).autoFocusMode(AutoFocusMode.SAFE).autoFocusInterval(2000L)
                    /*flash*/
                    .flash(false)
                    /*decode callback*/
                    .onDecoded(object : DecodeCallback {
                        override fun onDecoded(@NonNull result: Result) {
                            runOnUiThread({
                                Toast.makeText(this@MainActivity, result.getText(),
                                        Toast.LENGTH_LONG).show()

//                                val intent = ChromeCustomTabsActivity.newIntent(this@MainActivity, result.getText())
//                                startActivity(intent)
                                loadCustomTabForSite(result.getText())

                            })
                        }
                    })
                    /*error callback*/
                    .onError(object : ErrorCallback {
                        override fun onError(@NonNull error: Exception) {
                            runOnUiThread({
                                Toast.makeText(this@MainActivity, error.message,
                                        Toast.LENGTH_LONG).show()
                            })
                        }
                    }).build(this, scannerView)
            // Or use constructor to create scanner with default parameters
            // All parameters can be changed after scanner created
            // mCodeScanner = new CodeScanner(this, scannerView);
            // mCodeScanner.setDecodeCallback(...);
            scannerView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {
                    mCodeScanner!!.startPreview()
                }
            })
        }
        mCodeScanner!!.startPreview()
    }

    fun loadCustomTabForSite(url: String, color: Int = Color.GRAY) {
        val customTabsIntent = CustomTabsIntent.Builder(mCustomTabsSession)
                .setToolbarColor(color)
                .setShowTitle(true)
                .build()

//        if (url.contains("(?i)\\b((?:[a-z][\\w-]+:(?:\\/{1,3}|[a-z0-9%])|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}\\/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:'\".,<>?«»“”‘’]))")) {
        if (url.matches("^(http://|https://)+(www.)?([a-zA-Z0-9]+).[a-zA-Z0-9]*.[a-z]{3}\\.([a-z]+)?\$".toRegex())) {
            customTabsIntent.launchUrl(this, Uri.parse(url))
        } else {
            customTabsIntent.launchUrl(this, Uri.parse("https://www.google.com/search?q=" + url))
        }
    }
}