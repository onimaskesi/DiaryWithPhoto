package com.onimaskesi.diarywithphoto

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_memories.*

class MemoriesFragment : Fragment() {

    var dateList = ArrayList<String>()
    var idList = ArrayList<Int>()
    lateinit var entriesAdapter: RecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_memories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RecyclerAdapter(dateList, idList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        sqlReceiveData()
    }

    fun sqlReceiveData(){
        try {

            activity?.let {
                val database = it.openOrCreateDatabase("Diary", Context.MODE_PRIVATE, null)

                val cursor = database.rawQuery("SELECT * FROM Entries",null)
                val idIndex = cursor.getColumnIndex("id")
                val dateTextIndex = cursor.getColumnIndex("date")

                dateList.clear()
                idList.clear()

                while (cursor.moveToNext()){

                    idList.add(cursor.getInt(idIndex))
                    dateList.add(cursor.getString(dateTextIndex))

                }

                entriesAdapter.notifyDataSetChanged()
                cursor.close()
            }

        } catch (e: Exception){
            e.printStackTrace()
        }
    }

}
