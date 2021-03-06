package com.donaldwu.lunchpickerandroid.view.fragment.navbar.favourites

import com.donaldwu.lunchpickerandroid.view.adapter.FoodResultListAdapter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.donaldwu.lunchpickerandroid.R
import com.google.android.material.snackbar.Snackbar
import com.donaldwu.lunchpickerandroid.helper.Helper
import org.json.JSONArray
import org.json.JSONObject
import com.donaldwu.lunchpickerandroid.model.Model
import com.skydoves.elasticviews.ElasticButton
import com.skydoves.elasticviews.ElasticFloatingActionButton

class FavouritesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_favourites, container, false)

        getFavourites(root)

        handleDeleteAllFavourites(root)

        handleSwipeRefreshLayout(root)

        handleFloatingActionButton(root)

        return root
    }

    private fun getFavourites(root: View) {
        val ip = Helper.getIPAddress(true)
        val response = Model.getFavourites(ip)
        if (response != null && response.isNotEmpty() && !response.contains("<!DOCTYPE html>")) {
            val responseJSONObject = JSONObject(response)
            val favouritesList = responseJSONObject.getJSONArray("favourites")

            val yourTotalFavourites: TextView = root.findViewById(R.id.your_total_favourites)
            yourTotalFavourites.text = "%s".format(favouritesList.length().toString())

            if (favouritesList.length() > 0) {
                val nameList = arrayListOf<String>()
                val titleList = arrayListOf<String>()
                val imageUrlList = arrayListOf<String>()
                val urlList = arrayListOf<String>()
                val ratingList = arrayListOf<Double>()
                val addressList = arrayListOf<String>()
                val phoneList = arrayListOf<String>()

                for (a in 0 until favouritesList.length()) {
                    val favouritesListItem = favouritesList.getJSONObject(a)
                    val item = favouritesListItem.getJSONObject("item")
                    val name = item.getString("name")
                    val categories = item.getJSONArray("categories")
                    var combinedTitle = ""
                    for (b in 0 until categories.length()) {
                        val category = categories.getJSONObject(b)
                        val title = category.getString("title")

                        if (b == 0) {
                            combinedTitle += "%s".format(title)
                        } else {
                            combinedTitle += ", %s".format(title)
                        }

                        titleList.add(combinedTitle)
                    }
                    val imageUrl = item.getString("image_url")
                    val url = item.getString("url")
                    val rating = item.getDouble("rating")

                    val regex = "[\\[\\]\"\\\\]+".toRegex()
                    val address = item
                        .getJSONObject("location")
                        .getJSONArray("display_address")
                        .toString()
                        .replace(regex, "")
                    addressList.add(address)
                    val phone = item.getString("display_phone")

                    nameList.add(name)
                    imageUrlList.add(imageUrl)
                    urlList.add(url)
                    ratingList.add(rating)
                    phoneList.add(phone)
                }

                setFoodResultListRecyclerView(
                    favouritesList,
                    nameList,
                    titleList,
                    imageUrlList,
                    urlList,
                    ratingList,
                    addressList,
                    phoneList,
                    root,
                    true
                )

                val favouritesResultListRecyclerView: RecyclerView = root.findViewById(R.id.favourites_result_list_recyclerView)
                favouritesResultListRecyclerView.visibility = View.VISIBLE

                val noFavouritesResultCardView: CardView = root.findViewById(R.id.no_favourites_result_card_view)
                noFavouritesResultCardView.visibility = View.GONE
            } else {
                val favouritesResultListRecyclerView: RecyclerView = root.findViewById(R.id.favourites_result_list_recyclerView)
                favouritesResultListRecyclerView.visibility = View.GONE

                val noFavouritesResultCardView: CardView = root.findViewById(R.id.no_favourites_result_card_view)
                noFavouritesResultCardView.visibility = View.VISIBLE
            }
        }
    }

    private fun setFoodResultListRecyclerView(
        restaurantsList: JSONArray,
        nameList: ArrayList<String>,
        titleList: ArrayList<String>,
        imageUrlList: ArrayList<String>,
        urlList: ArrayList<String>,
        ratingList: ArrayList<Double>,
        addressList: ArrayList<String>,
        phoneList: ArrayList<String>,
        root: View,
        isFavourites: Boolean
    ) {
        val foodResultListRecyclerView: RecyclerView = root.findViewById(R.id.favourites_result_list_recyclerView)

        val foodResultListAdapter = FoodResultListAdapter(
            restaurantsList,
            nameList,
            titleList,
            imageUrlList,
            urlList,
            ratingList,
            addressList,
            phoneList,
            root.context,
            isFavourites
        )
        foodResultListRecyclerView.adapter = foodResultListAdapter

        val linearLayoutManager = LinearLayoutManager(root.context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        foodResultListRecyclerView.layoutManager = linearLayoutManager

        foodResultListAdapter.notifyDataSetChanged()
    }

    private fun handleDeleteAllFavourites(root: View) {
        val deleteAllFavouritesButton: ElasticButton = root.findViewById(R.id.delete_all_favourites_button)
        deleteAllFavouritesButton.setOnClickListener {
            val response = Model.deleteAllFavourites()
            Log.i("logger", "response = $response")

            if (response != null && response.isNotEmpty() && !response.contains("<!DOCTYPE html>")) {
                getFavourites(root)
                Snackbar.make(root, "Delete all favourites", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleSwipeRefreshLayout(root: View) {
        val mSwipeRefreshLayout: SwipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout)
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent)
        mSwipeRefreshLayout.setOnRefreshListener {
            Handler().postDelayed({
                getFavourites(root)
                mSwipeRefreshLayout.isRefreshing = false
            }, 1000)
        }
    }

    private fun handleFloatingActionButton(root: View) {
        val fab: ElasticFloatingActionButton = requireActivity().findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            val scrollView: ScrollView = root.findViewById(R.id.scrollView)
            scrollView.fullScroll(ScrollView.FOCUS_UP)

            Snackbar.make(view, "Back to top", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }
}