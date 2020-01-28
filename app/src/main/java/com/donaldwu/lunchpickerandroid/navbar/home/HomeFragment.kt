package com.donaldwu.lunchpickerandroid.navbar.home

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import org.json.JSONObject
import server.Server
import com.donaldwu.lunchpickerandroid.R
import com.google.android.material.snackbar.Snackbar
import org.json.JSONArray

class HomeFragment : Fragment() {

    private val foodCategoryList = arrayListOf<String>()
    private var selectedTerm = ""
    private var locationStr = ""
    private var latitude = 0.0
    private var longitude = 0.0

    private var radioButtonValue = ""

    private lateinit var resultList: JSONArray

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        getFoodCategories()

        getCurrentLocationFromSharedPreferences(root)

        findLocationByLatLong(root)

        handleFoodCategoryDropdown(root)

        handlePlaceRadioButton(root)

        handleCurrentLocationRadioButton(root)

        handleLocationEditText(root)

        handleSubmitButton(root)

        handleClearButton(root)

        return root
    }

    private fun getFoodCategories() {
        val response = Server.getCategories()
        if (response != null && response.isNotEmpty()) {
            val jsonObject = JSONObject(response)
            val categories = jsonObject.getJSONArray("categories")

            val foodList = arrayListOf<String>()
            val restaurantsList = arrayListOf<String>()
            val barsList = arrayListOf<String>()
            val breakfastBrunchList = arrayListOf<String>()
            for (a in 0 until categories.length()) {
                val item = categories.getJSONObject(a)

                val parentAliases = item.getJSONArray("parent_aliases")
                for (b in 0 until parentAliases.length()) {
                    val parentAliasesItem = parentAliases.getString(b)
                    if (parentAliasesItem == "food") {
                        val title = item.getString("title")
                        foodList.add(title)
                    }
                    if (parentAliasesItem == "restaurants") {
                        val title = item.getString("title")
                        restaurantsList.add(title)
                    }
                    if (parentAliasesItem == "bars") {
                        val title = item.getString("title")
                        barsList.add(title)
                    }
                    if (parentAliasesItem == "breakfast_brunch") {
                        val title = item.getString("title")
                        breakfastBrunchList.add(title)
                    }
                }
            }

            foodCategoryList.add("")

            if (foodList.isNotEmpty()) {
                foodList.forEach {
                    foodCategoryList.add(it)
                }
            }
            if (restaurantsList.isNotEmpty()) {
                restaurantsList.forEach {
                    foodCategoryList.add(it)
                }
            }
            if (barsList.isNotEmpty()) {
                barsList.forEach {
                    foodCategoryList.add(it)
                }
            }
            if (breakfastBrunchList.isNotEmpty()) {
                breakfastBrunchList.forEach {
                    foodCategoryList.add(it)
                }
            }
        }
    }

    private fun getCurrentLocationFromSharedPreferences(root: View) {
        val prefs: SharedPreferences = root.context.getSharedPreferences("sharedPreferences", MODE_PRIVATE)
        val latitudeFloat = prefs.getFloat("latitude", 0f)
        val longitudeFloat = prefs.getFloat("longitude", 0f)
        latitude = latitudeFloat.toDouble()
        longitude = longitudeFloat.toDouble()
    }

    private fun findLocationByLatLong(root: View) {
        if (latitude != 0.0 && longitude != 0.0) {
            val response = Server.findLocationByLatLong(latitude, longitude)
            if (response != null && response.isNotEmpty()) {
                val responseJSONObject = JSONObject(response)
                val location = responseJSONObject.getJSONObject("location")
                val displayName = location.getString("display_name")

                locationStr = displayName

                val locationEditText: EditText = root.findViewById(R.id.location_edit_text)
                locationEditText.setText(displayName, TextView.BufferType.EDITABLE)
            }
        }
    }

    private fun handleFoodCategoryDropdown(root: View) {
        val spinner: Spinner = root.findViewById(R.id.food_category_dropdown)
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            root.context,
            android.R.layout.simple_spinner_dropdown_item,
            foodCategoryList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                selectedTerm = selectedItem
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun handlePlaceRadioButton(root: View) {
        val placeRadioButton: RadioButton = root.findViewById(R.id.place_radio_button)
        placeRadioButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                radioButtonValue = "place"

                val placeLinearLayout: LinearLayout = root.findViewById(R.id.place_linear_layout)
                placeLinearLayout.visibility = View.VISIBLE

                val needToShowLinearLayout: LinearLayout = root.findViewById(R.id.need_to_show_linear_layout)
                needToShowLinearLayout.visibility = View.VISIBLE

                val clearButton: Button = root.findViewById(R.id.clear_button)
                clearButton.visibility = View.VISIBLE
            }
        }
    }

    private fun handleCurrentLocationRadioButton(root: View) {
        val currentLocationRadioButton: RadioButton = root.findViewById(R.id.current_location_radio_button)
        currentLocationRadioButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                radioButtonValue = "currentLocation"

                val placeLinearLayout: LinearLayout = root.findViewById(R.id.place_linear_layout)
                placeLinearLayout.visibility = View.GONE

                val needToShowLinearLayout: LinearLayout = root.findViewById(R.id.need_to_show_linear_layout)
                needToShowLinearLayout.visibility = View.VISIBLE

                val clearButton: Button = root.findViewById(R.id.clear_button)
                clearButton.visibility = View.VISIBLE
            }
        }
    }

    private fun handleLocationEditText(root: View) {
        val locationEditText: EditText = root.findViewById(R.id.location_edit_text)
        locationEditText.addTextChangedListener {
            locationStr = it.toString()
        }
    }

    private fun handleSubmitButton(root: View) {
        val submitButton: Button = root.findViewById(R.id.submit_button)
        submitButton.setOnClickListener {
            if (radioButtonValue == "place") {
                if (locationStr.isNotEmpty()) {
                    val response = Server.findRestaurantsByLocation(selectedTerm, locationStr)
                    if (response != null && response.isNotEmpty()) {
                        Log.i("logger", "response = ${response}")

                        val responseJSONObject = JSONObject(response)
                        val restaurants = responseJSONObject.getJSONObject("restaurants")
                        val restaurantsList = restaurants.getJSONArray("businesses")
                        resultList = restaurantsList
                    }
                }
            } else if (radioButtonValue == "currentLocation") {
                if (latitude != 0.0 && longitude != 0.0) {
                    val response = Server.findRestaurantsByLatLong(selectedTerm, latitude, longitude)
                    if (response != null && response.isNotEmpty()) {
                        Log.i("logger", "response = ${response}")

                        val responseJSONObject = JSONObject(response)
                        val restaurants = responseJSONObject.getJSONObject("restaurants")
                        val restaurantsList = restaurants.getJSONArray("businesses")
                        resultList = restaurantsList
                    }
                }
            }
        }
    }

    private fun handleClearButton(root: View) {
        val clearButton: Button = root.findViewById(R.id.clear_button)
        clearButton.setOnClickListener {
            val placeLinearLayout: LinearLayout = root.findViewById(R.id.place_linear_layout)
            placeLinearLayout.visibility = View.GONE

            val needToShowLinearLayout: LinearLayout = root.findViewById(R.id.need_to_show_linear_layout)
            needToShowLinearLayout.visibility = View.GONE

            resultList = JSONArray()

            Snackbar.make(root, "Clear result", Snackbar.LENGTH_LONG).show()
        }
    }
}