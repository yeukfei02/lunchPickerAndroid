package com.donaldwu.lunchpickerandroid.view.fragment.navbar.randomfood

import com.donaldwu.lunchpickerandroid.view.adapter.FoodResultListAdapter
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.donaldwu.lunchpickerandroid.R
import com.google.android.material.snackbar.Snackbar
import org.json.JSONArray
import org.json.JSONObject
import com.donaldwu.lunchpickerandroid.model.Model
import com.skydoves.elasticviews.ElasticButton
import com.skydoves.elasticviews.ElasticFloatingActionButton

class RandomFoodFragment : Fragment() {

    private var foodCategoryResult = hashSetOf<String>()
    private var selectedTerm = ""
    private var latitude = 0.0
    private var longitude = 0.0

    private var switchCheckedStatus = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_random_food, container, false)

        getFoodCategories(root)

        getCurrentLocationFromSharedPreferences(root)

        getRandomFood(root)

        handleUseFoodCategorySwitch(root)

        handleRefreshButton(root)

        handleSwipeRefreshLayout(root)

        handleFloatingActionButton(root)

        return root
    }

    private fun getFoodCategories(root: View) {
        val foodCategorySet = getFoodCategoryFromSharedPreferences(root)
        if (foodCategorySet == null) {
            val response = Model.getCategories()
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

                val foodCategoryList = arrayListOf<String>()

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

                foodCategoryResult = foodCategoryStringSet
            }
        } else {
            foodCategoryResult = foodCategorySet.toHashSet()
        }
    }

    private fun storeFoodCategoryInSharedPreferences(root: View, foodCategoryStringSet: HashSet<String>) {
        val editor = root.context.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE).edit()

        editor.putStringSet("foodCategory", foodCategoryStringSet)
        editor.apply()
    }

    private fun getFoodCategoryFromSharedPreferences(root: View): MutableSet<String>? {
        val prefs: SharedPreferences = root.context.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
        return prefs.getStringSet("foodCategory", null)
    }

    private fun getCurrentLocationFromSharedPreferences(root: View) {
        val prefs: SharedPreferences = root.context.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
        val latitudeFloat = prefs.getFloat("latitude", 0f)
        val longitudeFloat = prefs.getFloat("longitude", 0f)
        latitude = latitudeFloat.toDouble()
        longitude = longitudeFloat.toDouble()
    }

    private fun getRandomFood(root: View) {
        val response = Model.findRestaurantsByLatLong(selectedTerm, latitude, longitude)
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

    private fun handleUseFoodCategorySwitch(root: View) {
        val useRandomFoodCategrySwitch: Switch = root.findViewById(R.id.use_random_food_category)
        useRandomFoodCategrySwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                switchCheckedStatus = true
                getRandomFoodWithSwitchOn(root)
            } else {
                switchCheckedStatus = false
                getRandomFoodWithSwitchOff(root)
            }
        }
    }

    private fun handleRefreshButton(root: View) {
        val refreshButton: ElasticButton = root.findViewById(R.id.refresh_button)
        refreshButton.setOnClickListener {
            if (switchCheckedStatus) {
                getRandomFoodWithSwitchOn(root)
            } else {
                getRandomFoodWithSwitchOff(root)
            }
        }
    }

    private fun handleSwipeRefreshLayout(root: View) {
        val mSwipeRefreshLayout: SwipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout)
        mSwipeRefreshLayout.setColorScheme(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent)
        mSwipeRefreshLayout.setOnRefreshListener {
            Handler().postDelayed({
                if (switchCheckedStatus) {
                    getRandomFoodWithSwitchOn(root)
                } else {
                    getRandomFoodWithSwitchOff(root)
                }

                mSwipeRefreshLayout.isRefreshing = false
            }, 1000)
        }
    }

    private fun getRandomFoodWithSwitchOn(root: View) {
        selectedTerm = foodCategoryResult.random()

        val currentFoodCategoryLinearLayout: LinearLayout = root.findViewById(R.id.current_food_category_linear_layout)
        currentFoodCategoryLinearLayout.visibility = View.VISIBLE

        val currentFoodCategory: TextView = root.findViewById(R.id.current_food_category)
        currentFoodCategory.text = selectedTerm

        getRandomFood(root)
    }

    private fun getRandomFoodWithSwitchOff(root: View) {
        selectedTerm = ""

        val currentFoodCategoryLinearLayout: LinearLayout = root.findViewById(R.id.current_food_category_linear_layout)
        currentFoodCategoryLinearLayout.visibility = View.GONE

        getRandomFood(root)
    }

    private fun handleFloatingActionButton(root: View) {
        val fab: ElasticFloatingActionButton = activity!!.findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            val scrollView: ScrollView = root.findViewById(R.id.scrollView)
            scrollView.fullScroll(ScrollView.FOCUS_UP)

            Snackbar.make(view, "Back to top", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }
}