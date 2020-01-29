package com.donaldwu.lunchpickerandroid.fragment.navbar.settings

import android.content.Context
import android.content.SharedPreferences
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
import com.donaldwu.lunchpickerandroid.R
import server.Server

class SettingsFragment : Fragment() {

    private val languageList: ArrayList<String> = arrayListOf("English", "Chinese")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        handleSwitchSubscribeMessage(root)

        handleLanguageDropdown(root)

        return root
    }

    private fun handleSwitchSubscribeMessage(root: View) {
        val switch: Switch = root.findViewById(R.id.switch_subscribe_message)
        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            val token = getCurrentTokenFromSharedPreferences(root)

            if (isChecked) {
                val currentTokenList = ArrayList<String>()
                currentTokenList.add(token!!)
                val response = Server.subscribeTopic(currentTokenList)
                Log.i("logger", "response = ${response}")
            } else {
                val currentTokenList = ArrayList<String>()
                currentTokenList.add(token!!)
                val response = Server.unsubscribeTopic(currentTokenList)
                Log.i("logger", "response = ${response}")
            }
        }
    }

    private fun getCurrentTokenFromSharedPreferences(root: View): String? {
        val prefs: SharedPreferences = root.context.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
        return prefs.getString("token", "")
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