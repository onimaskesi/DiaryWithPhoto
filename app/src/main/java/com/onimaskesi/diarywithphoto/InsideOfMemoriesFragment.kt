package com.onimaskesi.diarywithphoto

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_inside_of_memories.*

class InsideOfMemoriesFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inside_of_memories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveButton.setOnClickListener {
            // görseli kaydet , yazıları kaydet ve bunları memories fregmentinde göster (SQLite)


            // memories fragmentine geri dön
            val action = InsideOfMemoriesFragmentDirections.actionİnsideOfMemoriesFragmentToMemoriesFragment()
            Navigation.findNavController(it).navigate(action)
        }
    }
}
