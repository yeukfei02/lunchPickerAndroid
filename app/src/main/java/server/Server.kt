package server

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject

class Server {
    companion object {
        private const val rootUrl = "https://lunch-picker-api.herokuapp.com/api"
        private const val scheme = "https"
        private const val host = "lunch-picker-api.herokuapp.com"
        private val getCategoriesUrl = "%s/category/get-categories".format(rootUrl)
        private val findLocationByLatLongUrl = "%s/restaurant/find-location-text-by-lat-long".format(rootUrl)
        private val findRestaurantsByLocation = "%s/restaurant/find-restaurants-by-location".format(rootUrl)
        private val findRestaurantsByLatLong = "%s/restaurant/find-restaurants-by-lat-long".format(rootUrl)
        private val addToFavouritesUrl = "%s/favourites/add-to-favourites".format(rootUrl)
        private val getFavouritesUrl = "%s/favourites/get-favourites".format(rootUrl)
        private val deleteAllFavouritesUrl = "%s/favourites/delete-all-favourites".format(rootUrl)
        private val deleteFavouritesByIdUrl = "%s/favourites/delete-favourites".format(rootUrl)

        fun getCategories(): String? {
            val client = OkHttpClient()

            val url = getCategoriesUrl
            Log.i("logger", "url = ${url}")

            val request: Request = Request.Builder()
                .header("Content-type", "application/json")
                .url(url)
                .build()

            val response = client.newCall(request).execute()

            return response.body?.string()
        }

        fun findLocationByLatLong(latitude: Double, longitude: Double): String? {
            val client = OkHttpClient()

            val urlBuilder = HttpUrl.Builder()
            urlBuilder.scheme(scheme)
            urlBuilder.host(host)
            urlBuilder.addPathSegment("api")
            urlBuilder.addPathSegment("restaurant")
            urlBuilder.addPathSegment("find-location-text-by-lat-long")
            urlBuilder.addQueryParameter("latitude", latitude.toString())
            urlBuilder.addQueryParameter("longitude", longitude.toString())
            val url = urlBuilder.build().toString()
            Log.i("logger", "url = ${url}")

            val request: Request = Request.Builder()
                .header("Content-type", "application/json")
                .url(url)
                .build()

            val response = client.newCall(request).execute()

            return response.body?.string()
        }

        fun findRestaurantsByLocation(selectedTerm: String, location: String): String? {
            val client = OkHttpClient()

            val urlBuilder = HttpUrl.Builder()
            urlBuilder.scheme(scheme)
            urlBuilder.host(host)
            urlBuilder.addPathSegment("api")
            urlBuilder.addPathSegment("restaurant")
            urlBuilder.addPathSegment("find-restaurants-by-location")
            urlBuilder.addQueryParameter("term", selectedTerm)
            urlBuilder.addQueryParameter("location", location)
            val url = urlBuilder.build().toString()
            Log.i("logger", "url = ${url}")

            val request: Request = Request.Builder()
                .header("Content-type", "application/json")
                .url(url)
                .build()

            val response = client.newCall(request).execute()

            return response.body?.string()
        }

        fun findRestaurantsByLatLong(selectedTerm: String, latitude: Double, longitude: Double): String? {
            val client = OkHttpClient()

            val urlBuilder = HttpUrl.Builder()
            urlBuilder.scheme(scheme)
            urlBuilder.host(host)
            urlBuilder.addPathSegment("api")
            urlBuilder.addPathSegment("restaurant")
            urlBuilder.addPathSegment("find-restaurants-by-lat-long")
            urlBuilder.addQueryParameter("term", selectedTerm)
            urlBuilder.addQueryParameter("latitude", latitude.toString())
            urlBuilder.addQueryParameter("longitude", longitude.toString())
            val url = urlBuilder.build().toString()
            Log.i("logger", "url = ${url}")

            val request: Request = Request.Builder()
                .header("Content-type", "application/json")
                .url(url)
                .build()

            val response = client.newCall(request).execute()

            return response.body?.string()
        }

        fun addToFavourites(item: JSONObject): String? {
            val client = OkHttpClient()

            val urlBuilder = HttpUrl.Builder()
            urlBuilder.scheme(scheme)
            urlBuilder.host(host)
            urlBuilder.addPathSegment("api")
            urlBuilder.addPathSegment("favourites")
            urlBuilder.addPathSegment("add-to-favourites")
            val url = urlBuilder.build().toString()
            Log.i("logger", "url = ${url}")

            val body: RequestBody = RequestBody.create(
                "application/json; charset=utf-8".toMediaType(),
                item.toString()
            )

            val request: Request = Request.Builder()
                .header("Content-type", "application/json")
                .post(body)
                .url(url)
                .build()

            val response = client.newCall(request).execute()

            return response.body?.string()
        }

        fun getFavourites(): String? {
            val client = OkHttpClient()

            val url = getFavouritesUrl
            Log.i("logger", "url = ${url}")

            val request: Request = Request.Builder()
                .header("Content-type", "application/json")
                .url(url)
                .build()

            val response = client.newCall(request).execute()

            return response.body?.string()
        }

        fun deleteAllFavourites(): String? {
            val client = OkHttpClient()

            val url = deleteAllFavouritesUrl
            Log.i("logger", "url = ${url}")

            val request: Request = Request.Builder()
                .header("Content-type", "application/json")
                .delete()
                .url(url)
                .build()

            val response = client.newCall(request).execute()

            return response.body?.string()
        }

        fun deleteFavouritesById(id: String): String? {
            val client = OkHttpClient()

            val url = "%s/%s".format(deleteFavouritesByIdUrl, id)
            Log.i("logger", "url = ${url}")

            val request: Request = Request.Builder()
                .header("Content-type", "application/json")
                .delete()
                .url(url)
                .build()

            val response = client.newCall(request).execute()

            return response.body?.string()
        }
    }
}