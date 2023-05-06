package com.example.chit_chat

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.chit_chat.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth=FirebaseAuth.getInstance()
        binding.tvRegister.setOnClickListener {
            finish()
        }
        binding.btnLogin.setOnClickListener {
            loginUser()
            binding.etEmail.text.clear()
            binding.etPassword.text.clear()
        }
    }

    private fun loginUser() {
        val progressDialog=ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Logging in")
        progressDialog.show()
        val email=binding.etEmail.text.toString()
        val password=binding.etPassword.text.toString()
        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                this@LoginActivity,
                "Email/Password field is empty",
                Toast.LENGTH_SHORT
            ).show()
            progressDialog.dismiss()
            return
        }
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
            if(it.isSuccessful) {
                Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
                startActivity(Intent(this,LatestMessagesActivity::class.java))
                progressDialog.dismiss()
                finish()
            }
            else {
                Toast.makeText(this@LoginActivity, "Error", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        }

    }
}