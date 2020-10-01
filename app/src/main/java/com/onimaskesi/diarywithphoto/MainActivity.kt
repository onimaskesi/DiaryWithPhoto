package com.onimaskesi.diarywithphoto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.Navigation
import androidx.navigation.NavigatorProvider
import androidx.navigation.fragment.FragmentNavigatorDestinationBuilder
import androidx.navigation.get
import java.util.zip.Inflater

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.add_memory_menu , menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.add_entry){
            val action = MemoriesFragmentDirections.actionMemoriesFragmentToİnsideOfMemoriesFragment()
            Navigation.findNavController(this, R.id.fragment).navigate(action)
        }

        return super.onOptionsItemSelected(item)
    }

}
