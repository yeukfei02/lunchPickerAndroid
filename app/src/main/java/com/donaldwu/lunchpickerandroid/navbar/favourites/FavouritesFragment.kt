package com.donaldwu.lunchpickerandroid.navbar.favourites

import adapter.FoodResultListAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.donaldwu.lunchpickerandroid.R
import com.google.android.material.snackbar.Snackbar
import org.json.JSONArray
import org.json.JSONObject
import server.Server

class FavouritesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_favourites, container, false)

        getFavourites(root)

        handleDeleteAllFavourites(root)

        return root
    }

    private fun getFavourites(root: View) {
        val response = Server.getFavourites()
        if (response != null && response.isNotEmpty()) {
            val responseJSONObject = JSONObject(response)
            val favouritesList = responseJSONObject.getJSONArray("favourites")

            val yourTotalFavourites: TextView = root.findViewById(R.id.your_total_favourites)
            yourTotalFavourites.text = "Your total favourites: %s".format(favouritesList.length().toString())

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
                    val address =
                        item.getJSONObject("location").getJSONArray("display_address")
                    var combinedAddressItem = ""
                    for (c in 0 until address.length()) {
                        val addressItem = address.getString(c)

                        if (c == 0) {
                            combinedAddressItem += "%s".format(addressItem)
                        } else {
                            combinedAddressItem += ", %s".format(addressItem)
                        }

                        addressList.add(combinedAddressItem)
                    }
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
        val deleteAllFavouritesButton: Button = root.findViewById(R.id.delete_all_favourites_button)
        deleteAllFavouritesButton.setOnClickListener {
            val response = Server.deleteAllFavourites()
            Log.i("logger", "response = ${response}")

            if (response != null && response.isNotEmpty()) {
                getFavourites(root)
                Snackbar.make(root, "Delete all favourites", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}