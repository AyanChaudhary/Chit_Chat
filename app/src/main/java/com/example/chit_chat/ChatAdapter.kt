package com.example.chit_chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chit_chat.databinding.ItemMessageRecievedLayoutBinding
import com.example.chit_chat.databinding.ItemMessageSentLayoutBinding
import com.google.firebase.auth.FirebaseAuth

class ChatAdapter(val context:Context, val items:ArrayList<Message>, val receiver:User):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_SENT=1
    val ITEM_RECEIVE=2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType==1)return senderViewHolder(ItemMessageSentLayoutBinding.inflate(LayoutInflater.from(context),parent,false))
        else return receiverViewHolder(ItemMessageRecievedLayoutBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val cMessage=items[position]
        if(holder.javaClass==senderViewHolder::class.java){
            holder as senderViewHolder
            holder.binding.tvSenderMessage.text=cMessage.messageText
            Glide.with(context).load(cMessage.senderImageURI).into(holder.binding.ivChatSelf)
        }
        else{
            holder as receiverViewHolder
            holder.binding.tvReceiveMessage.text=cMessage.messageText
            Glide.with(context).load(receiver.imageUrl).into(holder.binding.ivChatReceive)
        }

    }

    override fun getItemViewType(position: Int): Int {
        val cMessage=items[position]
        if(FirebaseAuth.getInstance().currentUser?.uid==cMessage.senderId)return ITEM_SENT
        else return ITEM_RECEIVE
    }

    override fun getItemCount(): Int=items.size

    inner class senderViewHolder(val binding: ItemMessageSentLayoutBinding):RecyclerView.ViewHolder(binding.root)
    inner class receiverViewHolder(val binding: ItemMessageRecievedLayoutBinding):RecyclerView.ViewHolder(binding.root)

}