package com.ddm.closethatholegram.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ddm.closethatholegram.modelos.User
import com.ddm.closethatholegram.SingUpActivity
import com.ddm.closethatholegram.adapters.ViewPageAdapter
import com.ddm.closethatholegram.databinding.FragmentProfileBinding
import com.ddm.closethatholegram.utils.USER_NODE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewPageAdapter: ViewPageAdapter
    private val firestore by lazy { Firebase.firestore }
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        viewPageAdapter=ViewPageAdapter(requireActivity().supportFragmentManager)
        viewPageAdapter.addFragments(MyPostFragment(), "My registers")
        binding.viewPager.adapter=viewPageAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection(USER_NODE).document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val user = document.toObject<User>()
                        if (user != null) {
                            binding.name.text = user.name ?: "No name"
                            binding.bio.text = user.email ?: "No bio"
                            user.image?.let { imageUrl ->
                                Picasso.get().load(imageUrl).into(binding.profileImage)
                            }
                            // Adiciona um listener de clique ao botão editProfileBtn
                            binding.editProfileBtn.setOnClickListener {
                                // Navega para a tela de edição de perfil
                                val intent = Intent(requireContext(), SingUpActivity::class.java)
                                intent.putExtra("MODE", 1) // Define o modo como 1 para edição de perfil
                                startActivity(intent)
                            }

                        } else {
                            Toast.makeText(requireContext(), "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Failed to load user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}
