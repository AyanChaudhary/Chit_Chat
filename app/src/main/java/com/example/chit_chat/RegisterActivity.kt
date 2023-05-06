package com.example.chit_chat

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.example.chit_chat.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.net.URI
import java.util.UUID

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var mAuth:FirebaseAuth
    lateinit var imageUri:Uri
    private val contract= registerForActivityResult(ActivityResultContracts.GetContent()){
        imageUri=it!!
        binding.ivPhoto.setImageURI(imageUri)
        binding.tvPhoto.visibility= View.GONE
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth= FirebaseAuth.getInstance()
        binding.tvLogin.setOnClickListener {
            val intent=Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
        binding.flPhoto.setOnClickListener {
            Toast.makeText(this, "Photo clicked", Toast.LENGTH_SHORT).show()
            contract.launch("image/*")
        }
        binding.btnRegister.setOnClickListener {
            signUpUser()
        }
    }

    private fun signUpUser(){
        val email=binding.etEmail.text.toString()
        val password=binding.etPassword.text.toString()
        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                this@RegisterActivity,
                "Email/Password field is empty",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val progress=ProgressDialog(this)

        progress.setTitle("Please wait")
        progress.setMessage("Adding User To Database")
        progress.show()
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(this@RegisterActivity, "User Created Succesfully", Toast.LENGTH_SHORT).show()
                uploadImageToFirebaseStorage()
                progress.dismiss()
            }
            else{
                Toast.makeText(this@RegisterActivity, "Authentication Failed", Toast.LENGTH_SHORT).show()
                progress.dismiss()
            }
        }
            .addOnFailureListener {
                Toast.makeText(this@RegisterActivity, "Failed to create a user", Toast.LENGTH_SHORT).show()
                progress.dismiss()
            }
    }
    private fun uploadImageToFirebaseStorage() {
        if(imageUri==null)return
        val uid=UUID.randomUUID().toString()
        val ref=FirebaseStorage.getInstance().getReference("/images/$uid")
        ref.putFile(imageUri).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {
                it.toString()
                addUserToDataBase(it.toString())
                Log.d("###", "uploadImageToFirebaseStorage: $it")
            }
        }
    }
    private fun addUserToDataBase(imageUri: String) {
        val uid=FirebaseAuth.getInstance().uid?:""
        val ref= Firebase.database.getReference("/users/$uid")
        val user=User(imageUri,binding.etUsername.text.toString(),uid.toString())
        ref.setValue(user)
        binding.etEmail.text.clear()
        binding.etPassword.text.clear()
        binding.etUsername.text.clear()
        startActivity(Intent(this,LatestMessagesActivity::class.java))
        finish()
    }
}