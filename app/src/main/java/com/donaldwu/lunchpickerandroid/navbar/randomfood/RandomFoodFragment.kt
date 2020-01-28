package com.donaldwu.lunchpickerandroid.navbar.randomfood

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.donaldwu.lunchpickerandroid.R

class RandomFoodFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_random_food, container, false)

        handleSwipeRefreshLayout(root)

        return root
    }

    private fun handleSwipeRefreshLayout(root: View) {
        val mSwipeRefreshLayout: SwipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout)
        mSwipeRefreshLayout.setColorScheme(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent)
        mSwipeRefreshLayout.setOnRefreshListener {
            Handler().postDelayed({
                mSwipeRefreshLayout.isRefreshing = false
            }, 1000)
        }
    }
}