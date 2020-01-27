package com.donaldwu.lunchpickerandroid.navbar.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.donaldwu.lunchpickerandroid.R

class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel
    private var languageList: ArrayList<String> = arrayListOf("English", "Chinese")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        settingsViewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)

        handleSwitchSubscribeMessage(root)

        handleLanguageDropdown(root)

        return root
    }

    private fun handleSwitchSubscribeMessage(root: View) {
        val switch: Switch = root.findViewById(R.id.switch_subscribe_message)
        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                Log.i("logger", "isChecked = true")
            } else {
                Log.i("logger", "isChecked = false")
            }
        }
    }

    private fun handleLanguageDropdown(root: View) {
        val spinner: Spinner = root.findViewById(R.id.language_dropdown)
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            root.context,
            android.R.layout.simple_spinner_dropdown_item,
            languageList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                if (selectedItem == "English") {
                    Log.i("logger", "change language to english")
                } else if (selectedItem == "Chinese") {
                    Log.i("logger", "change language to chinese")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }
}