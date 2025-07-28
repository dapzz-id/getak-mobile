package disdik.getak.id

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.*

class AddReportsActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_reports)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imageView = findViewById(R.id.imagePriview)
        val btnUploadImage = findViewById<Button>(R.id.btnPilihGambar)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val inputTanggal = findViewById<EditText>(R.id.inputTanggalWaktu)

        // Tanggal picker
        inputTanggal.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val tanggal = String.format("%02d-%02d-%d", selectedDay, selectedMonth + 1, selectedYear)
                inputTanggal.setText(tanggal)
            }, year, month, day)
            datePicker.show()
        }

        // Upload Image Intent
        btnUploadImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Submit laporan
        btnSubmit.setOnClickListener {
            val laporan = Laporan(
                status_pelapor = findViewById<EditText>(R.id.inputStatusPelapor).text.toString(),
                nama_korban = findViewById<EditText>(R.id.inputNamaKorban).text.toString(),
                nama_sanksi = findViewById<EditText>(R.id.inputNamaSanksi).text.toString(),
                nama_terlapor = findViewById<EditText>(R.id.inputNamaTerlapor).text.toString(),
                kontak = findViewById<EditText>(R.id.inputKontak).text.toString(),
                judul = findViewById<EditText>(R.id.inputJudul).text.toString(),
                bentuk = findViewById<EditText>(R.id.inputBentuk).text.toString(),
                tanggal = inputTanggal.text.toString(),
                tempat = findViewById<EditText>(R.id.inputTempat).text.toString(),
                frekuensi = findViewById<EditText>(R.id.inputFrekuensi).text.toString(),
                kronologi = findViewById<EditText>(R.id.inputKronologi).text.toString()
            )

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val map = HashMap<String, RequestBody>().apply {
                        put("status_pelapor", laporan.status_pelapor.toRequestBody("text/plain".toMediaTypeOrNull()))
                        put("nama_korban", laporan.nama_korban.toRequestBody("text/plain".toMediaTypeOrNull()))
                        put("nama_sanksi", laporan.nama_sanksi.toRequestBody("text/plain".toMediaTypeOrNull()))
                        put("nama_terlapor", laporan.nama_terlapor.toRequestBody("text/plain".toMediaTypeOrNull()))
                        put("kontak", laporan.kontak.toRequestBody("text/plain".toMediaTypeOrNull()))
                        put("judul", laporan.judul.toRequestBody("text/plain".toMediaTypeOrNull()))
                        put("bentuk", laporan.bentuk.toRequestBody("text/plain".toMediaTypeOrNull()))
                        put("tanggal", laporan.tanggal.toRequestBody("text/plain".toMediaTypeOrNull()))
                        put("tempat", laporan.tempat.toRequestBody("text/plain".toMediaTypeOrNull()))
                        put("frekuensi", laporan.frekuensi.toRequestBody("text/plain".toMediaTypeOrNull()))
                        put("kronologi", laporan.kronologi.toRequestBody("text/plain".toMediaTypeOrNull()))
                    }

                    val imagePart = imageUri?.let {
                        val file = File(getRealPathFromURI(it))
                        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                        MultipartBody.Part.createFormData("image", file.name, requestFile)
                    }

                    val response = ApiClient.instance.createLaporan(map, imagePart)

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@AddReportsActivity, "Laporan berhasil dikirim", Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            Toast.makeText(this@AddReportsActivity, "Gagal: ${response.code()}", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AddReportsActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }

        }
    }

    // Ambil gambar dari galeri
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            Glide.with(this).load(imageUri).into(imageView)
        }
    }

    // Mendapatkan path file dari Uri (lebih aman)
    private fun getRealPathFromURI(uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        contentResolver.query(uri, projection, null, null, null).use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return cursor.getString(columnIndex)
            }
        }
        return ""
    }
}
