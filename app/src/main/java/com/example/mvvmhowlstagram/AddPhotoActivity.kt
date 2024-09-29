package com.example.mvvmhowlstagram

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.mvvmhowlstagram.databinding.ActivityAddPhotoBinding
import com.example.mvvmhowlstagram.model.ContentModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date

class AddPhotoActivity : AppCompatActivity() {
    var photoUri : Uri? = null
    var photoResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            photoUri = it.data?.data
            binding.uploadImageview.setImageURI(photoUri)
        }
    }
    var storage = FirebaseStorage.getInstance()
    var auth = FirebaseAuth.getInstance()
    var firestore = FirebaseFirestore.getInstance()
    lateinit var binding : ActivityAddPhotoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addphotoUploadBtn.setOnClickListener {
            contentUpload()
        }

        var i = Intent(Intent.ACTION_PICK)
        i.type = "image/*"
        photoResult.launch(i)
    }
    fun contentUpload(){
        var timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timeStamp + ".png"
        var storagePath = storage.reference.child("images").child(imageFileName)
        storagePath.putFile(photoUri!!).continueWithTask{
            return@continueWithTask storagePath.downloadUrl
        }.addOnCompleteListener {downloadUrl ->
            var contentModel = ContentModel()
            contentModel.imagUrl = downloadUrl.result.toString()
            contentModel.explain = binding.addphotoEditEdittext.text.toString()
            contentModel.uid = auth.uid
            contentModel.userId = auth.currentUser?.email
            contentModel.timestamp = System.currentTimeMillis()

            firestore.collection("images").document().set(contentModel).addOnCompleteListener {
                Toast.makeText(this,"업로드 성공",Toast.LENGTH_SHORT).show()
                finish()
            }

        }

    }
}