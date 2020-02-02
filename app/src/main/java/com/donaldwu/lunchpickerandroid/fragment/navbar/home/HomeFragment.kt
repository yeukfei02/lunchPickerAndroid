package com.donaldwu.lunchpickerandroid.fragment.navbar.home

import com.donaldwu.lunchpickerandroid.adapter.FoodResultListAdapter
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import org.json.JSONArray
import org.json.JSONObject
import com.donaldwu.lunchpickerandroid.server.Server
import com.donaldwu.lunchpickerandroid.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {

    private var foodCategoryList = arrayListOf<String>()
    private var selectedTerm = ""
    private var locationStr = ""
    private var latitude = 0.0
    private var longitude = 0.0

    private var radioButtonValue = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        getFoodCategories(root)

        getCurrentLocationFromSharedPreferences(root)

        findLocationByLatLong(root)

        handleFoodCategoryDropdown(root)

        handlePlaceRadioButton(root)

        handleCurrentLocationRadioButton(root)

        handleLocationEditText(root)

        handleSubmitButton(root)

        handleClearButton(root)

        handleSwipeRefreshLayout(root)

        handleFloatingActionButton(root)

        return root
    }

    private fun getFoodCategories(root: View) {
        val foodCategorySet = getFoodCategoryFromSharedPreferences(root)
        if (foodCategorySet == null) {
            val response = Server.getCategories()
            if (response != null && response.isNotEmpty() && !response.contains("<!DOCTYPE html>")) {
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

                val foodCategoryStringSet = foodCategoryList.toHashSet()
                storeFoodCategoryInSharedPreferences(root, foodCategoryStringSet)
            }
        } else {
            val newFoodCategoryList = arrayListOf<String>()
            foodCategorySet.forEach {
                newFoodCategoryList.add(it)
            }
            foodCategoryList = newFoodCategoryList
        }
    }

    private fun storeFoodCategoryInSharedPreferences(root: View, foodCategoryStringSet: HashSet<String>) {
        val editor = root.context.getSharedPreferences("sharedPreferences", MODE_PRIVATE).edit()

        editor.putStringSet("foodCategory", foodCategoryStringSet)
        editor.apply()
    }

    private fun getFoodCategoryFromSharedPreferences(root: View): MutableSet<String>? {
        val prefs: SharedPreferences = root.context.getSharedPreferences("sharedPreferences", MODE_PRIVATE)
        return prefs.getStringSet("foodCategory", null)
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
            if (response != null && response.isNotEmpty() && !response.contains("<!DOCTYPE html>")) {
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
                    if (response != null && response.isNotEmpty() && !response.contains("<!DOCTYPE html>")) {
                        val responseJSONObject = JSONObject(response)
                        val restaurants = responseJSONObject.getJSONObject("restaurants")
                        val restaurantsList = restaurants.getJSONArray("businesses")

                        if (restaurantsList.length() > 0) {
                            val nameList = arrayListOf<String>()
                            val titleList = arrayListOf<String>()
                            val imageUrlList = arrayListOf<String>()
                            val urlList = arrayListOf<String>()
                            val ratingList = arrayListOf<Double>()
                            val addressList = arrayListOf<String>()
                            val phoneList = arrayListOf<String>()

                            for (a in 0 until restaurantsList.length()) {
                                val item = restaurantsList.getJSONObject(a)
                                val name = item.getString("name")
                                val categories = item.getJSONArray("categories")
                                var combinedTitle = ""
                                for (b in 0 until categories.length()) {
                                    val category = categories.getJSONObject(b)
                                    val title = category.getString("title")

                                    if (b == 0) {
                                        combinedTitle += "%s".format(title)
                                    } else {
                                        combinedTitle += ", %s".format(title)
                                    }

                                    titleList.add(combinedTitle)
                                }
                                val imageUrl = item.getString("image_url")
                                val url = item.getString("url")
                                val rating = item.getDouble("rating")

                                val regex = "[\\[\\]\"\\\\]+".toRegex()
                                val address = item
                                    .getJSONObject("location")
                                    .getJSONArray("display_address")
                                    .toString()
                                    .replace(regex, "")
                                addressList.add(address)
                                val phone = item.getString("display_phone")

                                nameList.add(name)
                                imageUrlList.add(imageUrl)
                                urlList.add(url)
                                ratingList.add(rating)
                                phoneList.add(phone)
                            }

                            setFoodResultListRecyclerView(
                                restaurantsList,
                                nameList,
                                titleList,
                                imageUrlList,
                                urlList,
                                ratingList,
                                addressList,
                                phoneList,
                                root,
                                false
                            )

                            val foodResultListRecyclerView: RecyclerView = root.findViewById(R.id.food_result_list_recyclerView)
                            foodResultListRecyclerView.visibility = View.VISIBLE

                            val noResultCardView: CardView = root.findViewById(R.id.no_result_card_view)
                            noResultCardView.visibility = View.GONE
                        } else {
                            val foodResultListRecyclerView: RecyclerView = root.findViewById(R.id.food_result_list_recyclerView)
                            foodResultListRecyclerView.visibility = View.GONE

                            val noResultCardView: CardView = root.findViewById(R.id.no_result_card_view)
                            noResultCardView.visibility = View.VISIBLE
                        }
                    }
                }
            } else if (radioButtonValue == "currentLocation") {
                if (latitude != 0.0 && longitude != 0.0) {
                    val response = Server.findRestaurantsByLatLong(selectedTerm, latitude, longitude)
                    if (response != null && response.isNotEmpty() && !response.contains("<!DOCTYPE html>")) {
                        val responseJSONObject = JSONObject(response)
                        val restaurants = responseJSONObject.getJSONObject("restaurants")
                        val restaurantsList = restaurants.getJSONArray("businesses")

                        if (restaurantsList.length() > 0) {
                            val nameList = arrayListOf<String>()
                            val titleList = arrayListOf<String>()
                            val imageUrlList = arrayListOf<String>()
                            val urlList = arrayListOf<String>()
                            val ratingList = arrayListOf<Double>()
                            val addressList = arrayListOf<String>()
                            val phoneList = arrayListOf<String>()

                            for (a in 0 until restaurantsList.length()) {
                                val item = restaurantsList.getJSONObject(a)
                                val name = item.getString("name")
                                val categories = item.getJSONArray("categories")
                                var combinedTitle = ""
                                for (b in 0 until categories.length()) {
                                    val category = categories.getJSONObject(b)
                                    val title = category.getString("title")

                                    if (b == 0) {
                                        combinedTitle += "%s".format(title)
                                    } else {
                                        combinedTitle += ", %s".format(title)
                                    }

                                    titleList.add(combinedTitle)
                                }
                                val imageUrl = item.getString("image_url")
                                val url = item.getString("url")
                                val rating = item.getDouble("rating")

                                val regex = "[\\[\\]\"\\\\]+".toRegex()
                                val address = item
                                    .getJSONObject("location")
                                    .getJSONArray("display_address")
                                    .toString()
                                    .replace(regex, "")
                                addressList.add(address)
                                val phone = item.getString("display_phone")

                                nameList.add(name)
                                imageUrlList.add(imageUrl)
                                urlList.add(url)
                                ratingList.add(rating)
                                phoneList.add(phone)
                            }

                            setFoodResultListRecyclerView(
                                restaurantsList,
                                nameList,
                                titleList,
                                imageUrlList,
                                urlList,
                                ratingList,
                                addressList,
                                phoneList,
                                root,
                                false
                            )

                            val foodResultListRecyclerView: RecyclerView = root.findViewById(R.id.food_result_list_recyclerView)
                            foodResultListRecyclerView.visibility = View.VISIBLE

                            val noResultCardView: CardView = root.findViewById(R.id.no_result_card_view)
                            noResultCardView.visibility = View.GONE
                        } else {
                            val foodResultListRecyclerView: RecyclerView = root.findViewById(R.id.food_result_list_recyclerView)
                            foodResultListRecyclerView.visibility = View.GONE

                            val noResultCardView: CardView = root.findViewById(R.id.no_result_card_view)
                            noResultCardView.visibility = View.VISIBLE
                        }
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

            val foodResultListRecyclerView: RecyclerView = root.findViewById(R.id.food_result_list_recyclerView)
            foodResultListRecyclerView.visibility = View.GONE

            val noResultCardView: CardView = root.findViewById(R.id.no_result_card_view)
            noResultCardView.visibility = View.GONE

            Snackbar.make(root, "Clear result", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setFoodResultListRecyclerView(
        restaurantsList: JSONArray,
        nameList: ArrayList<String>,
        titleList: ArrayList<String>,
        imageUrlList: ArrayList<String>,
        urlList: ArrayList<String>,
        ratingList: ArrayList<Double>,
        addressList: ArrayList<String>,
        phoneList: ArrayList<String>,
        root: View,
        isFavourites: Boolean
    ) {
        val foodResultListRecyclerView: RecyclerView = root.findViewById(R.id.food_result_list_recyclerView)

        val foodResultListAdapter = FoodResultListAdapter(
            restaurantsList,
            nameList,
            titleList,
            imageUrlList,
            urlList,
            ratingList,
            addressList,
            phoneList,
            root.context,
            isFavourites
        )
        foodResultListRecyclerView.adapter = foodResultListAdapter

        val linearLayoutManager = LinearLayoutManager(root.context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        foodResultListRecyclerView.layoutManager = linearLayoutManager

        foodResultListAdapter.notifyDataSetChanged()
    }

    private fun handleSwipeRefreshLayout(root: View) {
        val mSwipeRefreshLayout: SwipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout)
        mSwipeRefreshLayout.setColorScheme(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent)
        mSwipeRefreshLayout.setOnRefreshListener {
            Handler().postDelayed({
                val placeLinearLayout: LinearLayout = root.findViewById(R.id.place_linear_layout)
                placeLinearLayout.visibility = View.GONE

                val needToShowLinearLayout: LinearLayout = root.findViewById(R.id.need_to_show_linear_layout)
                needToShowLinearLayout.visibility = View.GONE

                val foodResultListRecyclerView: RecyclerView = root.findViewById(R.id.food_result_list_recyclerView)
                foodResultListRecyclerView.visibility = View.GONE

                val noResultCardView: CardView = root.findViewById(R.id.no_result_card_view)
                noResultCardView.visibility = View.GONE

                mSwipeRefreshLayout.isRefreshing = false
            }, 1000)
        }
    }

    private fun handleFloatingActionButton(root: View) {
        val fab: FloatingActionButton = activity!!.findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            val scrollView: ScrollView = root.findViewById(R.id.scrollView)
            scrollView.fullScroll(ScrollView.FOCUS_UP)

            Snackbar.make(view, "Back to top", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }
}