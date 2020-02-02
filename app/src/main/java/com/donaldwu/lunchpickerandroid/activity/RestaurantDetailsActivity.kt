package com.donaldwu.lunchpickerandroid.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
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

                if (hours.length() > 0) {
                    val restaurantDetailsHoursCardView: CardView = findViewById(R.id.restaurant_details_hours_card_view)
                    restaurantDetailsHoursCardView.visibility = View.VISIBLE

                    val restaurantDetailsTableLayout: TableLayout = findViewById(R.id.restaurant_details_table_layout)

                    for (a in 0 until hours.length()) {
                        val item = hours.getJSONObject(a)
                        val open = item.getJSONArray("open")
                        for (b in 0 until open.length()) {
                            val openItem = open.getJSONObject(b)
                            val day = openItem.getInt("day")
                            val dayStr = getDayStr(day)
                            val start = openItem.getString("start")
                            val startStr = formatTimeStr(start)
                            val end = openItem.getString("end")
                            val endStr = formatTimeStr(end)
                            val isOvernight = openItem.getBoolean("is_overnight")
                            val isOvernightStr = formatIsOvernight(isOvernight)
                            Log.i("logger", "dayStr = ${dayStr}")
                            Log.i("logger", "startStr = ${startStr}")
                            Log.i("logger", "endStr = ${endStr}")
                            Log.i("logger", "isOvernightStr = ${isOvernightStr}")

                            val tableRow = TableRow(applicationContext)
                            tableRow.setBackgroundResource(android.R.color.holo_red_light)
                            tableRow.setPadding(10, 10, 10 ,10)

                            val dayTextView = TextView(applicationContext)
                            dayTextView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)
                            dayTextView.text = dayStr
                            dayTextView.setTextColor(ContextCompat.getColor(applicationContext, android.R.color.white))

                            val startTextView = TextView(applicationContext)
                            startTextView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)
                            startTextView.text = startStr
                            startTextView.setTextColor(ContextCompat.getColor(applicationContext, android.R.color.white))

                            val endTextView = TextView(applicationContext)
                            endTextView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)
                            endTextView.text = endStr
                            endTextView.setTextColor(ContextCompat.getColor(applicationContext, android.R.color.white))

                            val isOvernightTextView = TextView(applicationContext)
                            isOvernightTextView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)
                            isOvernightTextView.text = isOvernightStr
                            isOvernightTextView.setTextColor(ContextCompat.getColor(applicationContext, android.R.color.white))

                            tableRow.addView(dayTextView, TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT))
                            tableRow.addView(startTextView, TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT))
                            tableRow.addView(endTextView, TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT))
                            tableRow.addView(isOvernightTextView, TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT))

                            restaurantDetailsTableLayout.addView(tableRow, TableLayout.LayoutParams(250, TableLayout.LayoutParams.WRAP_CONTENT))
                        }
                        val hoursType = item.getString("hours_type")
                        val isOpenNow = item.getBoolean("is_open_now")

                        val restaurantDetailsHoursTypeTextView: TextView = findViewById(R.id.restaurant_details_hours_type_text_view)
                        restaurantDetailsHoursTypeTextView.text = hoursType.toLowerCase()

                        val restaurantDetailsIsOpenNowCheckBox: CheckBox = findViewById(R.id.restaurant_details_is_open_now_check_box)
                        restaurantDetailsIsOpenNowCheckBox.isChecked = isOpenNow
                    }

                }
            }
        }
    }

    private fun getDayStr(day: Int): String {
        var dayStr = ""

        when(day) {
            0 -> {
                dayStr = "Mon"
            }
            1 -> {
                dayStr = "Tue"
            }
            2 -> {
                dayStr = "Wed"
            }
            3 -> {
                dayStr = "Thu"
            }
            4 -> {
                dayStr = "Fri"
            }
            5 -> {
                dayStr = "Sat"
            }
            6 -> {
                dayStr = "Sun"
            }
        }

        return dayStr
    }

    private fun formatTimeStr(str: String): String {
        var result = ""

        if (str.isNotEmpty()) {
            result = "%s:%s".format(str.substring(0, 2), str.substring(2))
         }

        return result
    }

    private fun formatIsOvernight(isOvernight: Boolean): String {
        var result = "no"

        if (isOvernight) {
            result = "yes"
        }

        return result
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
