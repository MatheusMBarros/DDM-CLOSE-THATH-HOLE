package com.ddm.closethatholegram

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.ddm.closethatholegram.utils.USER_NODE
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.statusBarColor = Color.TRANSPARENT

        Handler(Looper.getMainLooper()).postDelayed({
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                // Usuário não está autenticado, abrir a página de registro
                startActivity(Intent(this, SingUpActivity::class.java))
                finish()
            } else {
                // Usuário está autenticado, verificar se os dados do perfil existem
                Firebase.firestore.collection(USER_NODE).document(currentUser.uid).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            // Perfil existe, abrir a página inicial
                            startActivity(Intent(this, HomeActivity::class.java))
                        } else {
                            // Perfil não existe, abrir a página de registro
                            startActivity(Intent(this, SingUpActivity::class.java))
                        }
                        finish()
                    }
                    .addOnFailureListener {
                        // Em caso de falha ao acessar Firestore, abrir a página de registro
                        startActivity(Intent(this, SingUpActivity::class.java))
                        finish()
                    }
            }
        }, 3000)
    }
}
