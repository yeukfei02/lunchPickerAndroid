package com.donaldwu.lunchpickerandroid.navbar.randomfood

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RandomFoodViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is random food Fragment"
    }
    val text: LiveData<String> = _text
}