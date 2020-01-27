package com.donaldwu.lunchpickerandroid.navbar.randomfood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.donaldwu.lunchpickerandroid.R

class RandomFoodFragment : Fragment() {

    private lateinit var randomFoodViewModel: RandomFoodViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_random_food, container, false)

        randomFoodViewModel = ViewModelProviders.of(this).get(RandomFoodViewModel::class.java)
//        val textView: TextView = root.findViewById(R.id.text_random_food)
//        randomFoodViewModel.text.observe(this, Observer {
//            textView.text = it
//        })

        return root
    }
}