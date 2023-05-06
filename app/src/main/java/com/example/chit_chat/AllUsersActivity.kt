package com.example.chit_chat

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chit_chat.databinding.ActivityAllUsersBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AllUsersActivity : AppCompatActivity(){
    private lateinit var binding: ActivityAllUsersBinding
    private lateinit var mAuth:FirebaseAuth
    lateinit var adapter:AllUsersAdapter
    private val list=ArrayList<User>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAllUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title="All Users"
        mAuth=FirebaseAuth.getInstance()
        val actionBar=supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        binding.rv.layoutManager=LinearLayoutManager(this)
        adapter=AllUsersAdapter(this,list)
        binding.rv.adapter=adapter
        fetchData(list)
    }

    private fun fetchData(list: ArrayList<User>) {
        val ref=FirebaseDatabase.getInstance().getReference("/users")
        ref.addValueEventListener(object: ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for(postSnapshot in snapshot.children){
                    val user=postSnapshot.getValue(User::class.java)
                    if(user?.uid!=mAuth.currentUser?.uid)list.add(user!!)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}