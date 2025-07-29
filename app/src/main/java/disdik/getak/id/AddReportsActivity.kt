package disdik.getak.id

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.util.*

class AddReportsActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private var imageUri: Uri? = null
    private var selectedImageFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reports)

        val inputTanggal = findViewById<EditText>(R.id.inputTanggalWaktu)
        val btnUpload = findViewById<Button>(R.id.btnPilihGambar)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        imageView = findViewById(R.id.imagePriview)

        inputTanggal.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    inputTanggal.setText("$year-${month + 1}-$day")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        btnUpload.setOnClickListener { pickImage() }
        btnSubmit.setOnClickListener { submitReport() }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK && data?.data != null) {
            imageUri = data.data
            imageView.setImageURI(imageUri)
            prepareImagePart()
        }
    }

    private fun prepareImagePart() {
        try {
            imageUri?.let { uri ->
                val file = createFileFromUri(uri)
                selectedImageFile = file
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal memuat gambar: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createFileFromUri(uri: Uri): File {
        val fileName = getFileName(uri)
        val file = File(cacheDir, fileName)
        val inputStream = contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        inputStream?.use { input -> outputStream.use { output -> input.copyTo(output) } }
        return file
    }

    private fun getFileName(uri: Uri): String {
        var name = "image.jpg"
        val returnCursor = contentResolver.query(uri, null, null, null, null)
        returnCursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst()) {
                name = it.getString(nameIndex)
            }
        }
        return name
    }

    private fun submitReport() {
        val dataMap = mutableMapOf<String, RequestBody>()

        val userId = "123" // Ganti dengan dari SharedPreferences jika ada
        val referenceNumber = UUID.randomUUID().toString()

        dataMap["user_id"] = userId.toRequestBody("text/plain".toMediaTypeOrNull())
        dataMap["reference_number"] = referenceNumber.toRequestBody("text/plain".toMediaTypeOrNull())

        val fieldPairs = listOf(
            "status_pelapor" to R.id.inputStatusPelapor,
            "nama_korban" to R.id.inputNamaKorban,
            "nama_sanksi" to R.id.inputNamaSanksi,
            "nama_terlapor" to R.id.inputNamaTerlapor,
            "kontak" to R.id.inputKontak,
            "frekuensi" to R.id.inputFrekuensi,
            "judul" to R.id.inputJudul,
            "bentuk" to R.id.inputBentuk,
            "tanggal" to R.id.inputTanggalWaktu,
            "tempat" to R.id.inputTempat,
            "kronologi" to R.id.inputKronologi
        )

        try {
            fieldPairs.forEach { (key, viewId) ->
                val value = findViewById<EditText>(viewId).text.toString().trim()
                if (value.isNotEmpty()) {
                    dataMap[key] = value.toRequestBody("text/plain".toMediaTypeOrNull())
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal membaca input: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            return
        }

        val imageFile = selectedImageFile
        if (imageFile == null || !imageFile.exists()) {
            Toast.makeText(this, "Gambar belum dipilih!", Toast.LENGTH_SHORT).show()
            return
        }

        val imageRequestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("gambar", imageFile.name, imageRequestBody)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService.sendReport(dataMap, imagePart)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        Toast.makeText(
                            this@AddReportsActivity,
                            body?.message ?: "Laporan berhasil dikirim.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("API_ERROR", errorBody ?: "Error body kosong")
                        Toast.makeText(
                            this@AddReportsActivity,
                            "Gagal kirim: ${response.code()} ${response.message()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddReportsActivity, "HTTP Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("API_EXCEPTION", e.toString())
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddReportsActivity, "Kesalahan: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
