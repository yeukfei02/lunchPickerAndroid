package com.donaldwu.lunchpickerandroid.navbar.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.donaldwu.lunchpickerandroid.R
import org.json.JSONObject
import server.Server

class HomeFragment : Fragment() {

    private val foodCategoryList = arrayListOf<String>()
    private var selectedTerm = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        handleFoodCategoryDropdown(root)

        getCurrentLocation(root)

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
                    val parentAliasesFirstItem = parentAliases.getString(b)
                    if (parentAliasesFirstItem == "food") {
                        val title = item.getString("title")
                        foodList.add(title)
                    }
                    if (parentAliasesFirstItem == "restaurants") {
                        val title = item.getString("title")
                        restaurantsList.add(title)
                    }
                    if (parentAliasesFirstItem == "bars") {
                        val title = item.getString("title")
                        barsList.add(title)
                    }
                    if (parentAliasesFirstItem == "breakfast_brunch") {
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
        }
    }

    private fun handleFoodCategoryDropdown(root: View) {
        getFoodCategories()

        val spinner: Spinner = root.findViewById(R.id.food_category_dropdown)
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            root.context,
            android.R.layout.simple_spinner_dropdown_item,
            foodCategoryList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                selectedTerm = selectedItem
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun getCurrentLocation(root: View) {

    }

    private fun handlePlaceRadioButton(root: View) {
        val placeRadioButton: RadioButton = root.findViewById(R.id.place_radio_button)
        placeRadioButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val placeLinearLayout: LinearLayout = root.findViewById(R.id.place_linear_layout)
                placeLinearLayout.visibility = View.VISIBLE

                val needToShowLinearLayout: LinearLayout = root.findViewById(R.id.need_to_show_linear_layout)
                needToShowLinearLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun handleCurrentLocationRadioButton(root: View) {
        val currentLocationRadioButton: RadioButton = root.findViewById(R.id.current_location_radio_button)
        currentLocationRadioButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val placeLinearLayout: LinearLayout = root.findViewById(R.id.place_linear_layout)
                placeLinearLayout.visibility = View.GONE

                val needToShowLinearLayout: LinearLayout = root.findViewById(R.id.need_to_show_linear_layout)
                needToShowLinearLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun handleLocationEditText(root: View) {
        val locationEditText: EditText = root.findViewById(R.id.location_edit_text)
        locationEditText.addTextChangedListener {

        }
    }

    private fun handleSubmitButton(root: View) {
        val submitButton: Button = root.findViewById(R.id.submit_button)
        submitButton.setOnClickListener {

        }
    }

    private fun handleClearButton(root: View) {
        val clearButton: Button = root.findViewById(R.id.clear_button)
        clearButton.setOnClickListener {

        }
    }
}