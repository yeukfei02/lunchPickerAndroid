package com.donaldwu.lunchpickerandroid.server

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

        fun addToFavourites(ip: String, item: JSONObject): String? {
            val client = OkHttpClient()

            val urlBuilder = HttpUrl.Builder()
            urlBuilder.scheme(scheme)
            urlBuilder.host(host)
            urlBuilder.addPathSegment("api")
            urlBuilder.addPathSegment("favourites")
            urlBuilder.addPathSegment("add-to-favourites")
            val url = urlBuilder.build().toString()
            Log.i("logger", "url = ${url}")

            val data = JSONObject()
            data.put("ip", ip)
            data.put("item", item)

            val body: RequestBody = RequestBody.create(
                "application/json; charset=utf-8".toMediaType(),
                data.toString()
            )

            val request: Request = Request.Builder()
                .header("Content-type", "application/json")
                .post(body)
                .url(url)
                .build()

            val response = client.newCall(request).execute()

            return response.body?.string()
        }

        fun getFavourites(ip: String): String? {
            val client = OkHttpClient()

            val urlBuilder = HttpUrl.Builder()
            urlBuilder.scheme(scheme)
            urlBuilder.host(host)
            urlBuilder.addPathSegment("api")
            urlBuilder.addPathSegment("favourites")
            urlBuilder.addPathSegment("get-favourites")
            urlBuilder.addQueryParameter("ip", ip)
            val url = urlBuilder.build().toString()
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

        fun addTokenToServer(currentToken: String, refreshedToken: String): String? {
            val client = OkHttpClient()

            val urlBuilder = HttpUrl.Builder()
            urlBuilder.scheme(scheme)
            urlBuilder.host(host)
            urlBuilder.addPathSegment("api")
            urlBuilder.addPathSegment("firebase")
            urlBuilder.addPathSegment("add-token-to-com.donaldwu.lunchpickerandroid.server")
            val url = urlBuilder.build().toString()
            Log.i("logger", "url = ${url}")

            val data = JSONObject()
            data.put("currentToken", currentToken)
            data.put("refreshedToken", refreshedToken)

            val body: RequestBody = RequestBody.create(
                "application/json; charset=utf-8".toMediaType(),
                data.toString()
            )

            val request: Request = Request.Builder()
                .header("Content-type", "application/json")
                .post(body)
                .url(url)
                .build()

            val response = client.newCall(request).execute()

            return response.body?.string()
        }

        fun subscribeTopic(currentTokenList: ArrayList<String>): String? {
            val client = OkHttpClient()

            val urlBuilder = HttpUrl.Builder()
            urlBuilder.scheme(scheme)
            urlBuilder.host(host)
            urlBuilder.addPathSegment("api")
            urlBuilder.addPathSegment("firebase")
            urlBuilder.addPathSegment("subscribe-topic")
            val url = urlBuilder.build().toString()
            Log.i("logger", "url = ${url}")

            val data = JSONObject()
            data.put("currentTokenList", currentTokenList)
            data.put("topic", "all")

            val body: RequestBody = RequestBody.create(
                "application/json; charset=utf-8".toMediaType(),
                data.toString()
            )

            val request: Request = Request.Builder()
                .header("Content-type", "application/json")
                .post(body)
                .url(url)
                .build()

            val response = client.newCall(request).execute()

            return response.body?.string()
        }

        fun unsubscribeTopic(currentTokenList: ArrayList<String>): String? {
            val client = OkHttpClient()

            val urlBuilder = HttpUrl.Builder()
            urlBuilder.scheme(scheme)
            urlBuilder.host(host)
            urlBuilder.addPathSegment("api")
            urlBuilder.addPathSegment("firebase")
            urlBuilder.addPathSegment("unsubscribe-topic")
            val url = urlBuilder.build().toString()
            Log.i("logger", "url = ${url}")

            val data = JSONObject()
            data.put("currentTokenList", currentTokenList)
            data.put("topic", "all")

            val body: RequestBody = RequestBody.create(
                "application/json; charset=utf-8".toMediaType(),
                data.toString()
            )

            val request: Request = Request.Builder()
                .header("Content-type", "application/json")
                .post(body)
                .url(url)
                .build()

            val response = client.newCall(request).execute()

            return response.body?.string()
        }

        fun creditCardPayment(amount: Double, currency: String, token: String, card: JSONObject): String? {
            val client = OkHttpClient()

            val urlBuilder = HttpUrl.Builder()
            urlBuilder.scheme(scheme)
            urlBuilder.host(host)
            urlBuilder.addPathSegment("api")
            urlBuilder.addPathSegment("stripe")
            urlBuilder.addPathSegment("credit-card-payment")
            val url = urlBuilder.build().toString()
            Log.i("logger", "url = ${url}")

            val data = JSONObject()
            data.put("amount", amount)
            data.put("currency", currency)
            data.put("token", token)
            data.put("card", card)

            val body: RequestBody = RequestBody.create(
                "application/json; charset=utf-8".toMediaType(),
                data.toString()
            )

            val request: Request = Request.Builder()
                .header("Content-type", "application/json")
                .post(body)
                .url(url)
                .build()

            val response = client.newCall(request).execute()

            return response.body?.string()
        }

        fun getRestaurantDetails(id: String): String? {
            val client = OkHttpClient()

            val urlBuilder = HttpUrl.Builder()
            urlBuilder.scheme(scheme)
            urlBuilder.host(host)
            urlBuilder.addPathSegment("api")
            urlBuilder.addPathSegment("restaurant")
            urlBuilder.addPathSegment("get-restaurant-details")
            urlBuilder.addPathSegment(id)
            val url = urlBuilder.build().toString()
            Log.i("logger", "url = ${url}")

            val request: Request = Request.Builder()
                .header("Content-type", "application/json")
                .url(url)
                .build()

            val response = client.newCall(request).execute()

            return response.body?.string()
        }
    }
}