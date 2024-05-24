package com.ddm.closethatholegram.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ddm.closethatholegram.R
import com.ddm.closethatholegram.adapters.PostAdapter
import com.ddm.closethatholegram.databinding.FragmentHomeBinding
import com.ddm.closethatholegram.modelos.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView and adapter
        postAdapter = PostAdapter(requireContext())
        binding.recyclerViewPosts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Retrieve posts from Firestore
        getPostsFromFirestore()
    }

    private fun getPostsFromFirestore() {
        firestore.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING) // Ordena pelo campo "timestamp" em ordem decrescente
            .get()
            .addOnSuccessListener { result ->
                val posts = mutableListOf<Post>()
                for (document in result) {
                    val post = document.toObject(Post::class.java)
                    posts.add(post)
                }
                // Update the adapter with retrieved posts
                postAdapter.updatePosts(posts)
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }


    companion object {
        private const val TAG = "HomeFragment"
    }

}