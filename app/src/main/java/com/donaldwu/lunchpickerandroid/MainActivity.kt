package com.donaldwu.lunchpickerandroid

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.os.StrictMode
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.donaldwu.lunchpickerandroid.server.Server
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 44

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale()
        setContentView(R.layout.activity_main)

        setNetworkOnMainThread()

        getCurrentLocation()

        getFirebaseToken()

        setToolBar()

        setNavigationDrawer()

        handleUrlClick()
    }

    private fun setNetworkOnMainThread() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    private fun setLocale() {
        val language = getLanguageFromSharedPreferences()

        val locale = Locale(language!!)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }

    private fun getLanguageFromSharedPreferences(): String? {
        val prefs: SharedPreferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
        return prefs.getString("language", "en")
    }

    private fun getCurrentLocation() {
        val checkPermissionStatus = checkPermissions()
        if (checkPermissionStatus) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)

            mFusedLocationClient.lastLocation.addOnCompleteListener {
                val location: Location? = it.result
                if (location == null) {
                    requestNewLocationData()
                } else {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    Log.i("logger", "latitude = ${latitude}")
                    Log.i("logger", "longitude = ${longitude}")
                    storeLatLongInSharedPreferences(latitude, longitude)
                }
            }
        } else {
            requestPermissions()
        }
    }

    private fun getFirebaseToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result?.token
                Log.i("logger", "token = ${token}")

                if (token!!.isNotEmpty()) {
                    storeTokenInSharedPreferences(token)

                    val response = Server.addTokenToServer(token, "")
                    Log.i("logger", "response = ${response}")

                    subscribeTopic()
                }
            } else {
                Log.i("logger", "firebase getInstanceId failed = ${task.exception}")
                return@OnCompleteListener
            }
        })
    }

    private fun subscribeTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("all").addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i("logger", "subscribe topic message success")
            } else {
                Log.i("logger", "subscribe topic message fail")
            }
        }
    }

    private fun checkPermissions(): Boolean {
        return (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        val mLocationCallback: LocationCallback? = object: LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)

                val locations = p0?.locations
                if (locations != null) {
                    val location = locations[0]
                    val latitude = location.latitude
                    val longitude = location.longitude
                    Log.i("logger", "latitude = ${latitude}")
                    Log.i("logger", "longitude = ${longitude}")
                    storeLatLongInSharedPreferences(latitude, longitude)
                }
            }
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkPermissions()) {
            getCurrentLocation()
        }
    }

    private fun storeLatLongInSharedPreferences(latitude: Double, longitude: Double) {
        val editor = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE).edit()

        val latitudeFloat = latitude.toFloat()
        val longitudeFloat = longitude.toFloat()
        editor.putFloat("latitude", latitudeFloat)
        editor.putFloat("longitude", longitudeFloat)
        editor.apply()
    }

    private fun storeTokenInSharedPreferences(token: String) {
        val editor = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE).edit()

        editor.putString("token", token)
        editor.apply()
    }

    private fun setToolBar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    private fun setNavigationDrawer() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_random_food,
                R.id.nav_favourites,
                R.id.nav_settings,
                R.id.nav_contact_us
            ),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun handleUrlClick() {
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navHeaderView = navView.getHeaderView(0)
        val url: TextView = navHeaderView.findViewById(R.id.url)
        url.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("https://lunchpicker-2232b.firebaseapp.com")
            startActivity(i)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                val navController = findNavController(R.id.nav_host_fragment)
                navController.navigate(R.id.nav_settings)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
