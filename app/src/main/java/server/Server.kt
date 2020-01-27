package server

import okhttp3.OkHttpClient
import okhttp3.Request

class Server {
    companion object {
        private const val rootUrl = "https://lunch-picker-api.herokuapp.com/api"
        private val getCategoriesUrl = "%s/category/get-categories".format(rootUrl)

        fun getCategories(): String? {
            val client = OkHttpClient()

            val request: Request = Request.Builder()
                .header("Content-type", "application/json")
                .url(getCategoriesUrl)
                .build()

            val response = client.newCall(request).execute()

            return response.body?.string()
        }
    }
}