package com.ddm.closethatholegram

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.ddm.closethatholegram.databinding.ActivityPostBinding
import com.ddm.closethatholegram.modelos.Post
import com.ddm.closethatholegram.utils.uploadImage
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding
    private val storage by lazy { FirebaseStorage.getInstance() }
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var selectedLocation: LatLng? = null
    private var selectedImageUri: Uri? = null

    private val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            handleImageResult(it)
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            handleImageResult(it)
        }
    }

    private val locationLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val lat = result.data?.getDoubleExtra("latitude", 0.0)
            val lng = result.data?.getDoubleExtra("longitude", 0.0)
            if (lat != null && lng != null) {
                selectedLocation = LatLng(lat, lng)
                binding.location.text = "Location: $lat, $lng"
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.btnCamera.setOnClickListener {
            checkCameraPermissionAndOpenCamera()
        }

        binding.btnGallery.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.btnLocation.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            locationLauncher.launch(intent)
        }

        binding.btnPost.setOnClickListener {
            if (selectedLocation != null && selectedImageUri != null) {
                uploadImageAndPost()
            } else {
                Toast.makeText(this, "Please select an image and location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        cameraLauncher.launch(null)
    }

    private fun handleImageResult(bitmap: Bitmap) {
        binding.postImageView.setImageBitmap(bitmap) // Substitui o ícone pela imagem capturada
        val uri = getImageUriFromBitmap(bitmap)
        selectedImageUri = uri
    }

    private fun handleImageResult(uri: Uri) {
        binding.postImageView.setImageURI(uri) // Substitui o ícone pela imagem selecionada
        selectedImageUri = uri
    }

    private fun getImageUriFromBitmap(bitmap: Bitmap): Uri? {
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "New Post", null)
        return Uri.parse(path)
    }

    private fun uploadImageAndPost() {
        selectedImageUri?.let { uri ->
            uploadImage(uri, "posts") { imageUrl ->
                if (imageUrl != null) {
                    savePostToFirestore(imageUrl)
                } else {
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun savePostToFirestore(imageUrl: String) {
        val description = binding.descricao.editText?.text.toString()
        val userId = auth.currentUser?.uid
        val post = Post(
            userId,
            imageUrl,
            description,
            selectedLocation?.let { "${it.latitude},${it.longitude}" },
            System.currentTimeMillis()
        )

        firestore.collection("posts")
            .add(post)
            .addOnSuccessListener {
                Toast.makeText(this, "Post uploaded successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload post", Toast.LENGTH_SHORT).show()
            }
    }
}
