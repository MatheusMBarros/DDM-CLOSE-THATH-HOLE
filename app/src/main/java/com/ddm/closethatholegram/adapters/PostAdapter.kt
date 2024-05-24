package com.ddm.closethatholegram.adapters

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ddm.closethatholegram.databinding.ItemPostBinding
import com.ddm.closethatholegram.modelos.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class PostAdapter(private val context: Context) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    private var posts: MutableList<Post> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = posts[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    fun updatePosts(newPosts: List<Post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.apply {
                textDescription.text = post.description
                textLocation.text = "Clique para ver no mapa"

                // Load the post image if available
                post.imageUrl?.let { imageUrl ->
                    Picasso.get().load(imageUrl).into(binding.postImage)
                }

                // Extract latitude and longitude from the location string
                val (latitude, longitude) = extractLatLng(post.location)

                // Fetch the username associated with the userId
                fetchUserName(post.userId)

                // Create a clickable link to open Google Maps with the location
                textLocation.setOnClickListener {
                    val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=${post.location}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    if (mapIntent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(mapIntent)
                    } else {
                        Toast.makeText(
                            context,
                            "Google Maps app not installed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        private fun extractLatLng(location: String?): Pair<Double, Double> {
            // Split the location string to extract latitude and longitude
            val (latStr, lngStr) = location?.split(",") ?: listOf("", "") // provide default values if location is null
            val latitude = latStr.toDoubleOrNull() ?: 0.0
            val longitude = lngStr.toDoubleOrNull() ?: 0.0
            return Pair(latitude, longitude)
        }

        private fun fetchUserName(userId: String?) {
            // Fetch the username associated with the userId from Firestore
            userId?.let { userId ->
                val userDocRef = FirebaseFirestore.getInstance().collection("User").document(userId)
                userDocRef.get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val userName = documentSnapshot.getString("name")
                            userName?.let {
                                binding.userName.text = it
                            }
                        } else {
                            binding.userName.text = "User Not Found"
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Error fetching user name", exception)
                        binding.userName.text = "User Not Found"
                    }
            }
        }
    }

}

