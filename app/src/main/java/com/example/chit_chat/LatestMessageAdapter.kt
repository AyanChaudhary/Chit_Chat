package com.example.chit_chat

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chit_chat.databinding.LatestMessageLayoutBinding

class LatestMessageAdapter(val context: Context, val items:ArrayList<RecentMessage>):RecyclerView.Adapter<LatestMessageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LatestMessageLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int=items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=items[position]
        holder.binding.tvUsername.text=item.recepientName
        holder.binding.tvRecentMessage.text=item.textMessage
        Glide.with(context).load(item.recepientImage).into(holder.binding.ivRecentMessage)
        holder.binding.root.setOnClickListener {
            val user=User(item.recepientImage,item.recepientName,item.recepientUid)
            val intent= Intent(context,ChatActivity::class.java)
            intent.putExtra("user",user)
            context.startActivity(intent)
        }
    }

    inner class ViewHolder(val binding:LatestMessageLayoutBinding):RecyclerView.ViewHolder(binding.root)

}