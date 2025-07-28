import disdik.getak.id.Laporan
import retrofit2.http.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Multipart
import retrofit2.http.Part
import kotlin.jvm.JvmSuppressWildcards
import retrofit2.http.PartMap

interface ApiService {

    @GET("laporan")
    suspend fun getAllLaporan(): List<Laporan>

    @POST("laporan")
    suspend fun createLaporan(@Body laporan: Laporan): Response<Laporan>

    @PUT("laporan/{id}")
    suspend fun updateLaporan(@Path("id") id: Int, @Body laporan: Laporan): Response<Laporan>

    @DELETE("laporan/{id}")
    suspend fun deleteLaporan(@Path("id") id: Int): Response<Unit>

    @Multipart
    @POST("send-data") // hanya endpoint-nya
    suspend fun createLaporan(
        @PartMap data: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part image: MultipartBody.Part?
    ): Response<ResponseBody>

    @GET("laporan/{id}")
    suspend fun getLaporan(@Path("id") id: Int): Laporan
}
