package com.example.lunchpickerandroid.ui.randomfood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.lunchpickerandroid.R

class RandomFoodFragment : Fragment() {

    private lateinit var randomFoodViewModel: RandomFoodViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        randomFoodViewModel =
            ViewModelProviders.of(this).get(RandomFoodViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_random_food, container, false)
        val textView: TextView = root.findViewById(R.id.text_gallery)
        randomFoodViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}