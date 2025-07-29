package disdik.getak.id


import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @Multipart
    @POST("send-data")
    suspend fun sendReport(
        @PartMap data: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part image: MultipartBody.Part
    ): Response<ApiResponse>



}
