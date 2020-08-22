package com.donaldwu.lunchpickerandroid.view.fragment.navbar.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.donaldwu.lunchpickerandroid.MainActivity
import com.donaldwu.lunchpickerandroid.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging

class SettingsFragment : Fragment() {

    private var languageList = arrayListOf<String>()

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
        val switch: SwitchCompat = root.findViewById(R.id.switch_subscribe_message)
        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                subscribeTopic(root)
            } else {
                unsubscribeTopic(root)
            }
        }
    }

    private fun subscribeTopic(root: View) {
        FirebaseMessaging.getInstance().subscribeToTopic("all").addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i("logger", "subscribe topic message success")
                Snackbar.make(root, "Subscribe topic message success", Snackbar.LENGTH_SHORT).show()
            } else {
                Log.i("logger", "subscribe topic message fail")
                Snackbar.make(root, "Subscribe topic message fail", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun unsubscribeTopic(root: View) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("all").addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i("logger", "unsubscribe topic message success")
                Snackbar.make(root, "Unsubscribe topic message success", Snackbar.LENGTH_SHORT).show()
            } else {
                Log.i("logger", "unsubscribe topic message fail")
                Snackbar.make(root, "Unsubscribe topic message fail", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleLanguageDropdown(root: View) {
        val spinner: Spinner = root.findViewById(R.id.language_dropdown)
        languageList = arrayListOf(
            resources.getString(R.string.please_select),
            resources.getString(R.string.english),
            resources.getString(R.string.chinese)
        )
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
                if (selectedItem == "English" || selectedItem == "英文") {
                    restartApp(root, "en")
                } else if (selectedItem == "Chinese" || selectedItem == "中文") {
                    restartApp(root, "zh")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun restartApp(root: View, language: String) {
        storeLanguageInSharedPreferences(root, language)

        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
    }

    private fun storeLanguageInSharedPreferences(root: View, language: String) {
        val editor = root.context.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE).edit()

        editor.putString("language", language)
        editor.apply()
    }
}