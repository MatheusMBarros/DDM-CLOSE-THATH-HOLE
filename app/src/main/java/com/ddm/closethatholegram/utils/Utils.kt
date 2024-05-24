package com.ddm.closethatholegram.utils

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

fun uploadImage(uri: Uri, folderName: String, callback: (String?) -> Unit) {
    val storageReference = FirebaseStorage.getInstance().getReference("$folderName/${UUID.randomUUID()}")
    storageReference.putFile(uri)
        .addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                callback(uri.toString())
            }.addOnFailureListener {
                callback(null)
            }
        }
        .addOnFailureListener {
            callback(null)
        }
}
