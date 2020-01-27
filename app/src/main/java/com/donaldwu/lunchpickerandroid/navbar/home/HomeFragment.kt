package com.donaldwu.lunchpickerandroid.navbar.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.donaldwu.lunchpickerandroid.R

class HomeFragment : Fragment() {

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

    private fun handleFoodCategoryDropdown(root: View) {

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