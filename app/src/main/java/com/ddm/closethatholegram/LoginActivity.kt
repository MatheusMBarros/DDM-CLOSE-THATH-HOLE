package com.ddm.closethatholegram

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ddm.closethatholegram.modelos.User
import com.ddm.closethatholegram.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            val email = binding.txtEmail.editText?.text.toString().trim()
            val password = binding.txtPassword.editText?.text.toString().trim()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this@LoginActivity, "Email or Password cannot be empty!", Toast.LENGTH_SHORT).show()
            } else {
                val user = User(email, password)

                auth.signInWithEmailAndPassword(user.email!!, user.password!!)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, task.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        binding.btnCreateAccount.setOnClickListener{
            startActivity(Intent(this@LoginActivity, SingUpActivity::class.java))

        }
    }
}
