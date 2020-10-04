package com.onimaskesi.diarywithphoto

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.graphics.decodeBitmap
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_inside_of_memories.*
import java.util.jar.Manifest

class InsideOfMemoriesFragment : Fragment() {

    var photo : Uri? = null
    var photoBitmap : Bitmap? = null

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

        choosePhoto.setOnClickListener {
            //galeri izni ve galeriden görsel çekmek

            activity?.let{

                if(ContextCompat.checkSelfPermission(it.applicationContext , android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    // izin verilmemişse izin iste
                    requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE) , 0)

                }else{

                    val galleryIntent = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galleryIntent,1)

                }

            }
        }

        saveButton.setOnClickListener {
            // görseli kaydet , yazıları kaydet ve bunları memories fregmentinde göster (SQLite)

            val dateText = dateText.text.toString()
            val textText = textText.text.toString()

            if(photo != null){

                val smallBitmap = makeSmallBitmap(photoBitmap!!,300)
                

            }

            // memories fragmentine geri dön
            val action = InsideOfMemoriesFragmentDirections.actionİnsideOfMemoriesFragmentToMemoriesFragment()
            Navigation.findNavController(it).navigate(action)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if(requestCode == 0) { // galeriye gitme izni

            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // izin verilmiştir
                val galleryIntent = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent,1)
            }

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(requestCode == 1 && resultCode == Activity.RESULT_OK && data != null){

            photo = data.data

            try {

                if(photo != null && context != null){

                    if(Build.VERSION.SDK_INT >= 28){

                        val source = ImageDecoder.createSource(context!!.contentResolver , photo!!)
                        photoBitmap = ImageDecoder.decodeBitmap(source)
                        choosePhoto.setImageBitmap(photoBitmap)

                    } else {

                        photoBitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver , photo)
                        choosePhoto.setImageBitmap(photoBitmap)

                    }

                }

            } catch ( e: Exception){
                e.printStackTrace()
            }

        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    fun makeSmallBitmap(photoBitmap: Bitmap , maximumSize : Int) : Bitmap{

        var width = photoBitmap.width
        var height = photoBitmap.height

        val bitmapRate : Double = width.toDouble() / height.toDouble()

        if(bitmapRate > 1){
            //görsel yataydır
            width = maximumSize
            val newHeight = width / bitmapRate
            height = newHeight.toInt()

        } else {
            //görsel dikeydir
            height = maximumSize
            val newWidth = height * bitmapRate
            width = newWidth.toInt()
        }

        return Bitmap.createScaledBitmap(photoBitmap , width , height ,true)
    }
}
