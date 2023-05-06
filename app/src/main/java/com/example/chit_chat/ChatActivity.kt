package com.example.chit_chat

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chit_chat.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.runBlocking

class ChatActivity : AppCompatActivity() {
    var senderRoom:String?=null
    var receiverRoom:String?=null
    var messageList=ArrayList<Message>()

    lateinit var adapter:ChatAdapter
    private lateinit var binding:ActivityChatBinding
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user=intent.getParcelableExtra<User>("user")
        val name=user?.name
        supportActionBar?.title=name.toString()
        var currentUser:User?=null
        runBlocking {
            val myUid=FirebaseAuth.getInstance().currentUser?.uid.toString()
            val ref=FirebaseDatabase.getInstance().getReference("/users")
            ref.addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(postSnapshot in snapshot.children){
                        val user=postSnapshot.getValue(User::class.java)
                        if(user?.uid==myUid){
                            Log.d("TAG", "onDataChange: myUid = $myUid and users uid is ${user.uid}")
                            currentUser= user
                            Log.d("$$$$$", "in global scope: current user is ${currentUser?.name} and photo is ${currentUser?.imageUrl}")

                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }


        senderRoom=user?.uid+FirebaseAuth.getInstance().currentUser?.uid
        receiverRoom=FirebaseAuth.getInstance().currentUser?.uid + user?.uid



//        updateTheUI(user,currentUser)
        val dbRef=FirebaseDatabase.getInstance().getReference("/chats/${senderRoom}/messages")
        dbRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                for(postSnapshot in snapshot.children){
                    val message=postSnapshot.getValue(Message::class.java)
                    messageList.add(message!!)
                }
                adapter.notifyDataSetChanged()
                binding.rvChat.scrollToPosition(adapter.itemCount-1)
            }
            override fun onCancelled(error: DatabaseError) {}
        })



        binding.btnSend.setOnClickListener {
            val txt=binding.etMessage.text.toString()
            if(txt.isNotEmpty()){
                val senderId=FirebaseAuth.getInstance().currentUser?.uid
                val message=Message(txt,senderId!!,currentUser?.imageUrl!!)
                val dbRef=FirebaseDatabase.getInstance().getReference("/chats")
                dbRef.child(senderRoom!!).child("messages").push()
                    .setValue(message).addOnSuccessListener {
                        dbRef.child(receiverRoom!!).child("messages").push().setValue(message)
                            .addOnSuccessListener { binding.etMessage.text.clear()

                                val latestDbRef=FirebaseDatabase.getInstance().getReference("/latest_messages")
                                latestDbRef.child(senderId).child(user!!.uid).setValue(RecentMessage(txt,user.imageUrl,user.name,user.uid))
                                latestDbRef.child(user.uid).child(senderId).setValue(RecentMessage(txt,currentUser!!.imageUrl,currentUser!!.name,currentUser!!.uid))
                            }
                    }
            }
        }
        adapter=ChatAdapter(this,messageList,user!!)
        binding.rvChat.layoutManager=LinearLayoutManager(this)
        binding.rvChat.adapter=adapter
    }

//    private fun sendMessage(txt: String, currentUser: User?) {
//        val senderId=FirebaseAuth.getInstance().currentUser?.uid
//        val message=Message(txt,senderId!!,currentUser?.imageUrl!!)
//        val dbRef=FirebaseDatabase.getInstance().getReference("/chats")
//        dbRef.child(senderRoom!!).child("messages").push()
//            .setValue(message).addOnSuccessListener {
//                dbRef.child(receiverRoom!!).child("messages").push().setValue(message)
//                    .addOnSuccessListener { binding.etMessage.text.clear() }
//            }
//    }

//    private fun updateTheUI(user: User?, currentUser: User?) {
//
//        val dbRef=FirebaseDatabase.getInstance().getReference("/chats/${senderRoom}/messages")
//        dbRef.addValueEventListener(object:ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                messageList.clear()
//                for(postSnapshot in snapshot.children){
//                    val message=postSnapshot.getValue(Message::class.java)
//                    messageList.add(message!!)
//                }
//                adapter.notifyDataSetChanged()
//            }
//            override fun onCancelled(error: DatabaseError) {}
//        })
//    }

//    private fun getCurrentUser(): User? {
//
//        runBlocking {
//            val myUid=FirebaseAuth.getInstance().currentUser?.uid.toString()
//            val ref=FirebaseDatabase.getInstance().getReference("/users")
//            ref.addValueEventListener(object:ValueEventListener{
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    for(postSnapshot in snapshot.children){
//                        val user=postSnapshot.getValue(User::class.java)
//                        if(user?.uid==myUid){
//                            Log.d("TAG", "onDataChange: myUid = $myUid and users uid is ${user.uid}")
//                            currentUser= user
//                            Log.d("$$$$$", "in global scope: current user is ${currentUser?.name} and photo is ${currentUser?.imageUrl}")
//
//                        }
//                    }
//                }
//                override fun onCancelled(error: DatabaseError) {
//                }
//            })
//        }
//        return currentUser
//    }


}