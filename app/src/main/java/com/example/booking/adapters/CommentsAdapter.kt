package com.example.booking.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.booking.R
import com.example.booking.models.Comment

class CommentsAdapter(private var comments: List<Comment>) : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {
    class CommentViewHolder(c: View) : RecyclerView.ViewHolder(c) {
        var email: TextView = c.findViewById(R.id.commentEmail)
        var leftAt: TextView = c.findViewById(R.id.commentDate)
        var content: TextView = c.findViewById(R.id.commentContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.ic_comment, parent, false))
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.email.text = comments[position].user.email
        holder.leftAt.text = comments[position].leftAt
        holder.content.text = comments[position].content
    }

    override fun getItemCount(): Int {
        return comments.size
    }
}