package com.example.smartcaneapp

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.util.Collections
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger


class TextToSpeechHandler(private val context: Context) : TextToSpeech.OnInitListener {
    private var textToSpeech: TextToSpeech? = null
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference.child("audio_files")
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        textToSpeech = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.language = Locale.US
        } else {
            Log.d("TextToSpeech", "Initialization Failed")
        }
    }

    fun saveTextListToAudioFiles(textList: List<String>, baseFileName: String, onUploadComplete: (Boolean, String) -> Unit) {
        deleteOldFiles {
            Toast.makeText(context, "All old files deleted successfully", Toast.LENGTH_SHORT).show()
            Log.d("TextToSpeech", "All old files deleted. Starting new file upload.")
            generateAndUploadFiles(textList, baseFileName, onUploadComplete)
        }
    }

    private fun deleteOldFiles(onComplete: () -> Unit) {
        storageReference.listAll()
            .addOnSuccessListener { listResult ->
                val filesToDelete = listResult.items

                if (filesToDelete.isEmpty()) {
                    Log.d("FirebaseStorage", "No files to delete. Proceeding with upload.")
                    onComplete()
                    return@addOnSuccessListener
                }

                var deletedCount = 0
                val totalFiles = filesToDelete.size

                for (file in filesToDelete) {
                    file.delete()
                        .addOnSuccessListener {
                            synchronized(this) {
                                deletedCount++
                                if (deletedCount == totalFiles) {
                                    Log.d("FirebaseStorage", "All files deleted successfully.")
                                    onComplete()
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.d("FirebaseStorage", "Failed to delete file: ${file.name}, ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.d("FirebaseStorage", "Failed to list files: ${e.message}")
                onComplete() // Continue even if deletion fails
            }
    }

    private fun generateAndUploadFiles(textList: List<String>, baseFileName: String, onUploadComplete: (Boolean, String) -> Unit) {
        val uploadedFiles = Collections.synchronizedList(mutableListOf<Pair<Int, String>>()) // Ensure thread safety
        val totalFiles = textList.size
        val failedUploads = AtomicInteger(0) // Track failures

        if (totalFiles == 0) {
            onUploadComplete(false, "No files to upload")
            return
        }

        for ((index, text) in textList.withIndex()) {
            val fileName = "$baseFileName${index + 1}.wav"
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)

            val params = Bundle()
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "tts_utterance_$index")

            val result = textToSpeech?.synthesizeToFile(text, params, file, "tts_utterance_$index")
            if (result == TextToSpeech.SUCCESS) {
                Log.d("TextToSpeech", "File saved successfully: ${file.absolutePath}")

                uploadToFirebase(file, fileName) { success, downloadUrl ->
                    synchronized(uploadedFiles) {
                        if (success) {
                            uploadedFiles.add(index to downloadUrl)
                        } else {
                            failedUploads.incrementAndGet()
                        }

                        if (uploadedFiles.size + failedUploads.get() == totalFiles) {
                            // All uploads attempted, finalize process
                            if (failedUploads.get() == 0) {
                                saveUrlsToFirestore(uploadedFiles, onUploadComplete)
                            } else {
                                onUploadComplete(false, "Some uploads failed")
                            }
                        }
                    }
                }
            } else {
                Log.d("TextToSpeech", "File saving failed for index $index")
                failedUploads.incrementAndGet()

                if (failedUploads.get() == totalFiles) {
                    onUploadComplete(false, "File saving failed for all files")
                }
            }
        }
    }


    private fun uploadToFirebase(file: File, fileName: String, onUploadComplete: (Boolean, String) -> Unit) {
        val fileUri = Uri.fromFile(file)
        val fileRef = storageReference.child(fileName)

        fileRef.putFile(fileUri)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    Log.d("FirebaseStorage", "Download URL: $downloadUrl")
                    onUploadComplete(true, downloadUrl)
                }.addOnFailureListener { e ->
                    Log.d("FirebaseStorage", "Failed to get download URL: ${e.message}")
                    onUploadComplete(false, "Failed to get download URL")
                }
            }
            .addOnFailureListener { e ->
                Log.d("FirebaseStorage", "File upload failed: $fileName, ${e.message}")
                onUploadComplete(false, "File upload failed: $fileName")
            }
    }

    private fun saveUrlsToFirestore(uploadedFiles: List<Pair<Int, String>>, onUploadComplete: (Boolean, String) -> Unit) {
        val db = firestore
        val documentRef = db.collection("Users").document("user001")
        val flagref = FirebaseDatabase.getInstance().getReference("Sensors").child("gps").child("Nav_Flag")
        /*firestore.collection("Users")
            .document("user001")
            .collection("Canes")
            .document("cane001")
            .collection("Route")
            .document("RouteDetails")
*/
        documentRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val stepsList = documentSnapshot.get("Routedetails") as? MutableList<Map<String, Any>>
                if (stepsList != null) {
                    for ((index, url) in uploadedFiles) {
                        if (index < stepsList.size) {
                            val updatedStep = stepsList[index].toMutableMap()
                            updatedStep["audioContent"] = url
                            stepsList[index] = updatedStep
                        }
                    }


                    documentRef.update("Routedetails", stepsList)
                        .addOnSuccessListener {
                            Log.d("Firestore", "All audio URLs updated successfully and Nav_Flag set to true")
                            onUploadComplete(true, "All files uploaded and Firestore updated")

                        }
                        .addOnFailureListener { e ->
                            Log.d("Firestore", "Failed to update Firestore: ${e.message}")
                            onUploadComplete(false, "Failed to update Firestore")
                        }


                } else {
                    Log.d("Firestore", "Invalid steps list format")
                    onUploadComplete(false, "Invalid steps list format")
                }
            } else {
                Log.d("Firestore", "Document does not exist")
                onUploadComplete(false, "Document does not exist")
            }
        }.addOnFailureListener { e ->
            Log.d("Firestore", "Error retrieving document: ${e.message}")
            onUploadComplete(false, "Error retrieving document")
        }
    }

}

