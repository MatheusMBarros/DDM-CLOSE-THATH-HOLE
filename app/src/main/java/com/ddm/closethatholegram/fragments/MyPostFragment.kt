package com.ddm.closethatholegram.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.ddm.closethatholegram.R
import com.ddm.closethatholegram.adapters.PostAdapter
import com.ddm.closethatholegram.databinding.FragmentMyPostBinding
import com.ddm.closethatholegram.modelos.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyPostFragment : Fragment() {
    private var _binding: FragmentMyPostBinding? = null
    private val binding get() = _binding!!

    private lateinit var postAdapter: PostAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase components
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Configure RecyclerView with a grid layout
        binding.recyclerViewMyPosts.layoutManager = GridLayoutManager(requireContext(), 2)
        postAdapter = PostAdapter(requireContext())
        binding.recyclerViewMyPosts.adapter = postAdapter
    }

    override fun onResume() {
        super.onResume()
        // Load user's posts when the fragment is resumed
        loadUserPosts()
    }

    private fun loadUserPosts() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("posts")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { result ->
                    val posts = mutableListOf<Post>()
                    for (document in result) {
                        val post = document.toObject(Post::class.java)
                        posts.add(post)
                    }
                    postAdapter.updatePosts(posts)
                }
                .addOnFailureListener { exception ->
                    // Handle errors
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}