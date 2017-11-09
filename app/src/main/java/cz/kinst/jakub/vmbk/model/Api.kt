package cz.kinst.jakub.vmbk.model

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object Api {
    val WEATHER by lazy {
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val originalRequest = chain.request()
            val url = originalRequest.url().newBuilder()
                    .addQueryParameter("appid", "23e05b6422b0b6eb4cd10fa0a9032875")
                    .addQueryParameter("units", "metric")
                    .build()
            val newRequest = originalRequest.newBuilder().url(url).build()
            chain.proceed(newRequest)
        }.build()

        Retrofit.Builder()
                .client(client)
                .baseUrl("http://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherApi::class.java)
    }
}