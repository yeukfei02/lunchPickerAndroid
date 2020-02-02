package com.donaldwu.lunchpickerandroid.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.donaldwu.lunchpickerandroid.adapter.RestaurantDetailsPhotosAdapter
import com.donaldwu.lunchpickerandroid.server.Server
import com.github.vivchar.viewpagerindicator.ViewPagerIndicator
import org.json.JSONArray
import org.json.JSONObject
import com.donaldwu.lunchpickerandroid.R

class RestaurantDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_details)

        setActionBarTitle()

        handleBackButton()

        getRestaurantDetails()
    }

    private fun setActionBarTitle() {
        title = resources.getString(R.string.restaurant_details_title)
    }

    private fun handleBackButton() {
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun getRestaurantDetails() {
        val i = intent
        val id = i.getStringExtra("id")

        if (id != null && id.isNotEmpty()) {
            val response = Server.getRestaurantDetails(id)

            if (response != null && response.isNotEmpty() && !response.contains("<!DOCTYPE html>")) {
                val responseJSONObject = JSONObject(response)
                val restaurantDetails = responseJSONObject.getJSONObject("restaurantDetails")

                val photos = restaurantDetails.getJSONArray("photos")
                val name = restaurantDetails.getString("name")
                val phone = restaurantDetails.getString("display_phone")
                val url = restaurantDetails.getString("url")
                val regex = "[\\[\\]\"\\\\]+".toRegex()
                val address = restaurantDetails
                    .getJSONObject("location")
                    .getJSONArray("display_address")
                    .toString()
                    .replace(regex, "")
                var hours = JSONArray()
                if (restaurantDetails.has("hours"))
                    hours = restaurantDetails.getJSONArray("hours")

                val restaurantDetailsViewPager: ViewPager = findViewById(R.id.restaurant_details_view_pager)
                val adapter = RestaurantDetailsPhotosAdapter(photos)
                restaurantDetailsViewPager.adapter = adapter
                val restaurantDetailsViewPagerIndicator: ViewPagerIndicator = findViewById(R.id.restaurant_details_view_pager_indicator)
                restaurantDetailsViewPagerIndicator.setupWithViewPager(restaurantDetailsViewPager)

                val restaurantDetailsNameTextView: TextView = findViewById(R.id.restaurant_details_name_text_view_value)
                restaurantDetailsNameTextView.text = name

                val restaurantDetailsPhoneTextView: TextView = findViewById(R.id.restaurant_details_phone_text_view_value)
                restaurantDetailsPhoneTextView.text = phone

                val restaurantDetailsUrlTextView: TextView = findViewById(R.id.restaurant_details_url_text_view)
                restaurantDetailsUrlTextView.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    startActivity(intent)
                }

                val restaurantDetailsLocationTextView: TextView = findViewById(R.id.restaurant_details_location_text_view)
                restaurantDetailsLocationTextView.text = address
                restaurantDetailsLocationTextView.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://www.google.com/maps/search/?api=1&query=${address}")
                    startActivity(intent)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }
}
