package com.example.chit_chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chit_chat.databinding.UserItemLayoutBinding

class AllUsersAdapter(val context: Context, val items:ArrayList<User>):RecyclerView.Adapter<AllUsersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(UserItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int=items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=items[position]
        holder.binding.tvUsername.text=item.name
        Glide.with(context).load(item.imageUrl).into(holder.binding.ivLayoutImage)
//        holder.binding.root.setOnClickListener {
//            val intent=Intent(context,ChatActivity::class.java)
//            context.startActivity(intent)
//        }
        holder.binding.root.setOnClickListener {
            val intent=Intent(context,ChatActivity::class.java)
            intent.putExtra("user",item)
            context.startActivity(intent)
            (context as Activity).finish()
        }
    }

    inner class ViewHolder(val binding: UserItemLayoutBinding):RecyclerView.ViewHolder(binding.root)

}