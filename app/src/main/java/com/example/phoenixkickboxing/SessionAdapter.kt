package com.example.phoenixkickboxing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SessionAdapter(private val sessions: List<SessionData>) :
    RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {

    class SessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val membersTextView: TextView = itemView.findViewById(R.id.membersTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.session_item, parent, false)
        return SessionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val session = sessions[position]
        holder.dateTextView.text = session.date
        holder.membersTextView.text = session.members
    }

    override fun getItemCount(): Int {
        return sessions.size
    }
}