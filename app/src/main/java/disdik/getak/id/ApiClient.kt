package disdik.getak.id

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "https://getak.syntx.id/api/"

    val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Accept", "application/json") // ⬅ ini penting
                .build()
            chain.proceed(newRequest)
        }
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://getak.syntx.id/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()


    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val gson = GsonBuilder()
        .setLenient() // ✅ Izinkan format JSON yang longgar
        .create()


    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)

    }
}
