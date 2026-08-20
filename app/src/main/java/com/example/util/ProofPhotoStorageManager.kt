package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ProofPhotoStorageManager {
    private const val TAG = "ProofPhotoStorage"

    /**
     * Copy picked image URI to app's persistent internal storage
     * and compress slightly for fast loading and low storage footprint.
     */
    suspend fun saveLocalReceiptPhoto(context: Context, sourceUri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val dir = File(context.filesDir, "receipt_photos").apply {
                if (!exists()) mkdirs()
            }
            val fileName = "proof_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.jpg"
            val destFile = File(dir, fileName)

            context.contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                if (originalBitmap != null) {
                    // Resize if larger than 1600px
                    val maxDimension = 1600
                    val width = originalBitmap.width
                    val height = originalBitmap.height
                    val scaledBitmap = if (width > maxDimension || height > maxDimension) {
                        val ratio = width.toFloat() / height.toFloat()
                        val newWidth = if (width > height) maxDimension else (maxDimension * ratio).toInt()
                        val newHeight = if (height > width) maxDimension else (maxDimension / ratio).toInt()
                        Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
                    } else {
                        originalBitmap
                    }

                    FileOutputStream(destFile).use { out ->
                        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
                        out.flush()
                    }
                    if (scaledBitmap != originalBitmap) {
                        scaledBitmap.recycle()
                    }
                    originalBitmap.recycle()
                    destFile.absolutePath
                } else {
                    null
                }
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Error saving local receipt photo: ${e.message}", e)
            null
        }
    }

    /**
     * Upload local receipt photo to Firebase Storage and get download URL.
     */
    suspend fun uploadReceiptToFirebaseStorage(
        context: Context,
        localPhotoPathOrUri: String,
        roomCode: String,
        transactionSyncId: String
    ): String? = withContext(Dispatchers.IO) {
        try {
            val storage = FirebaseStorage.getInstance()
            val cleanRoomCode = roomCode.replace(Regex("[^a-zA-Z0-9_-]"), "_")
            val storageRef = storage.reference.child("rt_receipts/$cleanRoomCode/${transactionSyncId}_${System.currentTimeMillis()}.jpg")

            val file = File(localPhotoPathOrUri)
            val bytes = if (file.exists()) {
                file.readBytes()
            } else {
                // If path is a content URI string
                val uri = Uri.parse(localPhotoPathOrUri)
                val baos = ByteArrayOutputStream()
                context.contentResolver.openInputStream(uri)?.use { input ->
                    input.copyTo(baos)
                }
                baos.toByteArray()
            }

            if (bytes.isEmpty()) {
                Log.w(TAG, "Image bytes empty for: $localPhotoPathOrUri")
                return@withContext null
            }

            val metadata = StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .setCustomMetadata("roomCode", roomCode)
                .setCustomMetadata("syncId", transactionSyncId)
                .build()

            val uploadTask = storageRef.putBytes(bytes, metadata).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await().toString()
            Log.d(TAG, "Successfully uploaded to Firebase Storage: $downloadUrl")
            downloadUrl
        } catch (e: Throwable) {
            Log.w(TAG, "Firebase Storage upload notice (operating in local photo cache mode): ${e.message}")
            null
        }
    }
}
