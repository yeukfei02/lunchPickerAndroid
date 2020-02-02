package com.donaldwu.lunchpickerandroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.donaldwu.lunchpickerandroid.R
import org.json.JSONArray

class RestaurantDetailsPhotosAdapter(photos: JSONArray) : PagerAdapter() {

    private val photosJSONArray = photos

    override fun getCount(): Int {
        return photosJSONArray.length()
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = LayoutInflater.from(container.context)

        val itemView = layoutInflater.inflate(R.layout.restaurant_details_photos_pager_item, container, false)

        val photosUrl = photosJSONArray[position]
        val restaurantDetailsPhotosPagerItemImageView: ImageView = itemView.findViewById(R.id.restaurant_details_photos_pager_item_image_view)
        Glide.with(itemView.context).load(photosUrl).centerCrop().into(restaurantDetailsPhotosPagerItemImageView)

        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }
}