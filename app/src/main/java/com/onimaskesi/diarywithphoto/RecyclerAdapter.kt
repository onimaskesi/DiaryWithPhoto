package com.onimaskesi.diarywithphoto

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_raw.view.*

class RecyclerAdapter(val dateList : ArrayList<String>, val idList : ArrayList<Int>, val deleteBtnListener : DeleteBtnListener) : RecyclerView.Adapter<RecyclerAdapter.EntryHolder>() {


    class EntryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_raw, parent, false)
        return EntryHolder(view)
    }

    override fun getItemCount(): Int {
        return idList.size
    }

    override fun onBindViewHolder(holder: EntryHolder, position: Int) {

        holder.itemView.entryDate.text = dateList.get(position)
        holder.itemView.deleteBtn.setOnClickListener {

            val id = idList.get(position)
            deleteBtnListener.deleteClicked(id)

            idList.removeAt(position)
            dateList.removeAt(position)
            notifyDataSetChanged()

        }

        holder.itemView.setOnClickListener {

            val action = MemoriesFragmentDirections.actionMemoriesFragmentToİnsideOfMemoriesFragment("fromRecycler", idList.get(position))
            Navigation.findNavController(it).navigate(action)

        }
    }

    interface DeleteBtnListener{
        fun deleteClicked(id : Int)
    }

}