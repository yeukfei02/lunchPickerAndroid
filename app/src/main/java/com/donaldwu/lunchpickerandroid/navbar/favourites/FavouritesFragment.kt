package com.donaldwu.lunchpickerandroid.navbar.favourites

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.donaldwu.lunchpickerandroid.R
import server.Server

class FavouritesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_favourites, container, false)

        getFavourites()

        return root
    }

    private fun getFavourites() {
        val response = Server.getFavourites()
        Log.i("logger", "response = ${response}")
    }
}