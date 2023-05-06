package com.example.chit_chat

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chit_chat.databinding.ActivityLatestMessagesBinding
import com.example.chit_chat.databinding.LatestMessageLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class LatestMessagesActivity : AppCompatActivity() {
    private val user=Firebase.auth.currentUser
    private var recentMessageList=ArrayList<RecentMessage>()
    private lateinit var binding:ActivityLatestMessagesBinding
    private lateinit var adapter:LatestMessageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLatestMessagesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title="Latest Messages"
        if(user==null){
            startActivity(Intent(this,RegisterActivity::class.java))
            finish()
        }
        setUplatestChatsView()
        binding.rvLatestMessages.layoutManager=LinearLayoutManager(this)
        adapter=LatestMessageAdapter(this,recentMessageList)
        binding.rvLatestMessages.adapter=adapter
    }
    val latestMessageHashMap=HashMap<String,RecentMessage>()
    private fun setUplatestChatsView() {
        val dbRef=FirebaseDatabase.getInstance().getReference("/latest_messages/${user?.uid}")
        dbRef.addChildEventListener(object:ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val recentMessage=snapshot.getValue(RecentMessage::class.java)
                recentMessageList.add(recentMessage!!)
                latestMessageHashMap[snapshot.key!!]=recentMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val recentMessage=snapshot.getValue(RecentMessage::class.java)
                recentMessageList.add(recentMessage!!)
                latestMessageHashMap[snapshot.key!!]=recentMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

//        dbRef.addValueEventListener(object:ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                for(postSnapshot in snapshot.children){
//                    val recentMessage=postSnapshot.getValue(RecentMessage::class.java)
//                    recentMessageList.add(recentMessage!!)
//                }
//                adapter.notifyDataSetChanged()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//        })
    }

    private fun refreshRecyclerViewMessages() {
        recentMessageList.clear()
        latestMessageHashMap.values.forEach{
            recentMessageList.add(it)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.signOut->{
                val alertDialog= AlertDialog.Builder(this)
                alertDialog.setTitle("Do you really want to Log out?")
                alertDialog.setPositiveButton("YES", DialogInterface.OnClickListener { dialog, which ->
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this,RegisterActivity::class.java))
                    finish()
                    dialog.dismiss()
                })
                alertDialog.setNegativeButton("NO", DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
                alertDialog.show()
            }
            R.id.newMessage->{
                startActivity(Intent(this,AllUsersActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}