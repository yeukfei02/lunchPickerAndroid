package com.donaldwu.lunchpickerandroid.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.donaldwu.lunchpickerandroid.R
import com.donaldwu.lunchpickerandroid.activity.RestaurantDetailsActivity
import com.google.android.material.snackbar.Snackbar
import com.donaldwu.lunchpickerandroid.helper.Helper
import kotlinx.android.synthetic.main.food_result_list_item.view.*
import org.json.JSONArray
import com.donaldwu.lunchpickerandroid.server.Server
import java.lang.Exception

class FoodResultListAdapter(
    private val restaurantsList: JSONArray,
    private val nameList: ArrayList<String>,
    private val titleList: ArrayList<String>,
    private val imageUrlList: ArrayList<String>,
    private val urlList: ArrayList<String>,
    private val ratingList: ArrayList<Double>,
    private val addressList: ArrayList<String>,
    private val phoneList: ArrayList<String>,
    private val context: Context,
    private val isFavourites: Boolean
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return nameList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.food_result_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            val item = restaurantsList.getJSONObject(position)
            var id = ""
            if (!isFavourites) {
                id = item.getString("id")
            } else {
                id = item.getJSONObject("item").getString("id")
            }
            val name = nameList[position]
            val title = titleList[position]
            val imageUrl = imageUrlList[position]
            val url = urlList[position]
            val rating = ratingList[position]
            val address = addressList[position]
            val phone = phoneList[position]

            // circle
            holder.itemView.circle_text_view.text = name.substring(0, 1).toUpperCase()
            holder.itemView.circle_text_view.setOnClickListener {
                val i = Intent(context, RestaurantDetailsActivity::class.java)
                i.putExtra("id", id)
                context.startActivity(i)
            }

            // name
            holder.itemView.name_text_view.text = name
            holder.itemView.name_text_view.setOnClickListener {
                val i = Intent(context, RestaurantDetailsActivity::class.java)
                i.putExtra("id", id)
                context.startActivity(i)
            }

            // subtitle
            holder.itemView.category_text_view.text = title

            // image
            Glide.with(context).load(imageUrl).centerCrop().into(holder.itemView.image_view)

            // url
            holder.itemView.image_view.setOnClickListener {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                context.startActivity(i)
            }

            // location
            holder.itemView.location_text_view.text = "%s".format(address)
            holder.itemView.location_text_view.paintFlags = holder.itemView.location_text_view.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            holder.itemView.location_text_view.setOnClickListener {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("https://www.google.com/maps/search/?api=1&query=${address}")
                context.startActivity(i)
            }

            // phone
            holder.itemView.phone_text_view.text = "%s".format(phone)

            // rating
            holder.itemView.rating_text_view.text = "%s".format(rating.toInt().toString())

            // favourites
            if (!isFavourites) {
                holder.itemView.delete_favourites_button_linear_layout.visibility = View.GONE

                holder.itemView.favourites_image_view.setImageResource(R.drawable.favourites_null)

                holder.itemView.favourites_image_view.setOnClickListener {
                    val ip = Helper.getIPAddress(true)
                    val response = Server.addToFavourites(ip, item)
                    Log.i("logger", "response = ${response}")

                    if (response != null && response.isNotEmpty() && !response.contains("<!DOCTYPE html>")) {
                        holder.itemView.favourites_image_view.setImageResource(R.drawable.favourites_added)
                        Snackbar.make(holder.itemView, "Add to favourites", Snackbar.LENGTH_SHORT).show()
                    }
                }
            } else {
                holder.itemView.delete_favourites_button_linear_layout.visibility = View.VISIBLE

                holder.itemView.delete_favourites_button.setOnClickListener {
                    val favouritesItemId = item.getString("_id")
                    val response = Server.deleteFavouritesById(favouritesItemId)
                    Log.i("logger", "response = ${response}")

                    if (response != null && response.isNotEmpty() && !response.contains("<!DOCTYPE html>")) {
                        Snackbar.make(holder.itemView, "Delete favourites by id", Snackbar.LENGTH_SHORT).show()
                    }
                }

                holder.itemView.favourites_image_view.setImageResource(R.drawable.favourites_added)
            }
            
            // link
            holder.itemView.link_image_view.setOnClickListener {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                context.startActivity(i)
            }
        } catch (e: Exception) {
            Log.i("logger", "error = ${e.message}")
        }
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view)
}