package com.metinbatindincer.noteproject.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.metinbatindincer.noteproject.databinding.RecyclerRowBinding
import com.metinbatindincer.noteproject.model.Note
import com.metinbatindincer.noteproject.view.ListFragmentDirections

class NoteAdapter(val noteList: List<Note>):RecyclerView.Adapter<NoteAdapter.NoteHolder>() {
    class NoteHolder(val binding: RecyclerRowBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val recyclerRowBinding=RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NoteHolder(recyclerRowBinding)
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        holder.binding.recyclerViewTextView.text=noteList[position].title
        holder.itemView.setOnClickListener {
            val action=ListFragmentDirections.actionListFragmentToNoteFragment(info="old",id=noteList[position].id)
            Navigation.findNavController(it).navigate(action)
        }
    }
}