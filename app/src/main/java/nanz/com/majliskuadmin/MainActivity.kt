package nanz.com.majliskuadmin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val storage_path: String = "poster/"
    //    private val database_path: String =
    private val imageRequestCode: Int = 7
    private var filePathUri: Uri? = null
    private var storageReference: StorageReference? = null
    private var databaseReference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        storageReference = FirebaseStorage.getInstance().getReference()
        databaseReference = FirebaseDatabase.getInstance().getReference()

        progressBar.visibility = View.GONE
        btn_save.visibility = View.VISIBLE

        btn_choose_img.setOnClickListener { view ->
            var intent = Intent()

            // Setting intent type as image to select image from phone storage.
            intent?.setType("image/*")
            intent?.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Please Select Image"), imageRequestCode)
        }

        btn_save.setOnClickListener { view ->
            UploadImageFileToFirebaseStorage()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == imageRequestCode && resultCode == Activity.RESULT_OK && data != null && data.data != null){
            filePathUri = data.data

            try {
                // Getting selected image into Bitmap.
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePathUri)

                // Setting up bitmap selected image into ImageView.
                iv_preview_selecter_img.setImageBitmap(bitmap)

                // After selecting image change choose button above text.
//                btn_choose_img.text = "Image Selected"
//                btn_save.visibility = View.GONE
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    // Creating Method to get the selected image file Extension from File Path URI.
    fun getFileExtension(uri: Uri): String {
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    // Creating UploadImageFileToFirebaseStorage method to upload image on storage.
    fun UploadImageFileToFirebaseStorage(){

        // Checking whether FilePathUri Is empty or not.
        if (filePathUri != null){
            progressBar.visibility = View.VISIBLE
            btn_save.visibility = View.GONE

            // Creating second StorageReference.
            val storageReference2nd = storageReference?.child(storage_path + System.currentTimeMillis() + "." + getFileExtension(filePathUri!!))

            // Adding addOnSuccessListener to second StorageReference.

            storageReference2nd?.putFile(filePathUri!!)
                    ?.continueWithTask(object : Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
                        override fun then(task: Task<UploadTask.TaskSnapshot>): Task<Uri> {
                            return storageReference2nd.downloadUrl
                        }
                    })?.addOnSuccessListener { taskSnapshot ->

                    }
                    ?.addOnFailureListener {
                        val exception: Exception? = null
                        Toast.makeText(this, exception?.message.toString(), Toast.LENGTH_SHORT).show()
                    }?.addOnCompleteListener { task ->
                        progressBar.visibility = View.GONE
                        btn_save.visibility = View.VISIBLE

                        // Adding addOnSuccessListener to second StorageReference.
                        Toast.makeText(this, "Data telah tersimpan", Toast.LENGTH_SHORT).show()

                        val uri = task.result

                        // Getting image upload ID.
//                        val imageUploadId: String = databaseReference?.push()?.key.toString()

                        databaseReference?.child("jadwal")?.push()?.setValue(jadwal_model(uri.toString(),
                                et_judul_kegiatan.text.toString(), et_pemateri.text.toString(), et_alamat.text.toString(),
                                et_jadwal.text.toString()))
                    }
        }else{
            Toast.makeText(this, "Please Select Image or Add Image Name", Toast.LENGTH_SHORT).show()
        }
    }
}
