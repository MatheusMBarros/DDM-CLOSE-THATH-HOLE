package com.ddm.closethatholegram

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.ddm.closethatholegram.modelos.User
import com.ddm.closethatholegram.databinding.ActivitySingUpBinding
import com.ddm.closethatholegram.utils.USER_NODE
import com.ddm.closethatholegram.utils.USER_PROFILE_FOLDER
import com.ddm.closethatholegram.utils.uploadImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.squareup.picasso.Picasso

class SingUpActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySingUpBinding.inflate(layoutInflater)
    }
    private lateinit var user: User
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uploadImage(it, USER_PROFILE_FOLDER) { imageUrl ->
                if (imageUrl != null) {
                    Log.d("ImageUpload", "Image URL: $imageUrl")
                    user.image = imageUrl
                    Picasso.get().load(imageUrl).into(binding.profileImage)
                } else {
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        user = User()

        if (auth.currentUser != null && intent.hasExtra("MODE") && intent.getIntExtra("MODE", -1) == 1) {
            binding.btnSingUp.text = "Update Profile"
            firestore.collection(USER_NODE).document(auth.currentUser!!.uid).get()
                .addOnSuccessListener {
                    user = it.toObject<User>() ?: User()
                    binding.txtName.editText?.setText(user.name)
                    binding.txtEmail.editText?.setText(user.email)
                    binding.txtPassword.editText?.setText(user.password)
                    user.image?.let { imageUrl ->
                        Picasso.get().load(imageUrl).into(binding.profileImage)
                    }
                }
        }

        binding.btnSingUp.setOnClickListener {
            if (auth.currentUser != null && intent.hasExtra("MODE") && intent.getIntExtra("MODE", -1) == 1) {
                updateUserProfile()
            } else {
                registerNewUser()
            }
        }

        binding.addImageBtn.setOnClickListener {
            launcher.launch("image/*")
        }

        binding.login.setOnClickListener {
            startActivity(Intent(this@SingUpActivity, LoginActivity::class.java))
            finish()
        }
    }

    private fun updateUserProfile() {
        user.name = binding.txtName.editText?.text.toString()
        user.email = binding.txtEmail.editText?.text.toString()
        user.password = binding.txtPassword.editText?.text.toString()

        firestore.collection(USER_NODE)
            .document(auth.currentUser!!.uid)
            .set(user)
            .addOnSuccessListener {
                startActivity(Intent(this@SingUpActivity, HomeActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
    }

    private fun registerNewUser() {
        val name = binding.txtName.editText?.text.toString()
        val email = binding.txtEmail.editText?.text.toString()
        val password = binding.txtPassword.editText?.text.toString()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill the required fields!", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { result ->
            if (result.isSuccessful) {
                user.name = name
                user.email = email
                user.password = password
                firestore.collection(USER_NODE)
                    .document(auth.currentUser!!.uid)
                    .set(user)
                    .addOnSuccessListener {
                        startActivity(Intent(this@SingUpActivity, HomeActivity::class.java))
                        finish()
                    }
            } else {
                Toast.makeText(this, result.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
