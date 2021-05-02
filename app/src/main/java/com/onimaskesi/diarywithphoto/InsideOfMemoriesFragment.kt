package com.onimaskesi.diarywithphoto

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.database.getBlobOrNull
import androidx.core.graphics.decodeBitmap
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_inside_of_memories.*
import java.io.ByteArrayOutputStream
import java.util.jar.Manifest

class InsideOfMemoriesFragment : Fragment() {

    var photo : Uri? = null
    var photoBitmap : Bitmap? = null

    var entryId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
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

        arguments?.let {

            val info = InsideOfMemoriesFragmentArgs.fromBundle(it).info
            entryId = InsideOfMemoriesFragmentArgs.fromBundle(it).id

            when (info) {

                "fromRecyclerForOpen" -> {

                    saveButton.visibility = View.GONE
                    getDataAndShowThem(false)

                }
                "fromRecyclerForEdit" -> {

                    getDataAndShowThem(true)
                    choosePhoto.setOnClickListener { choosePhotoClicked() }
                    saveButton.setOnClickListener(this::saveButtonClickedForEdit)

                }
                else -> { //fromMenuForAdd

                    choosePhoto.setOnClickListener { choosePhotoClicked() }
                    saveButton.setOnClickListener(this::saveButtonClickedForNew)

                }
            }

        }

    }

    fun saveButtonClickedForEdit(view: View){

        update()
        Navigation.findNavController(view).popBackStack(R.id.memoriesFragment, false)
    }

    fun saveButtonClickedForNew(view: View){

        insert()
        Navigation.findNavController(view).popBackStack(R.id.memoriesFragment, false)
    }

    fun getDataAndShowThem(makeTextsEditable : Boolean){

        context?.let{ context ->

            val database = context.openOrCreateDatabase("Diary", Context.MODE_PRIVATE, null)
            val cursor = database.rawQuery("SELECT * FROM Entries WHERE id = ?", arrayOf(entryId.toString()))

            val dateIndex = cursor.getColumnIndex("date")
            val textIndex = cursor.getColumnIndex("text")
            val photoIndex = cursor.getColumnIndex("photo")

            while(cursor.moveToNext()){

                dateText.setText(cursor.getString(dateIndex))
                textText.setText(cursor.getString(textIndex))

                if(!makeTextsEditable){

                    dateText.isEnabled = false
                    dateText.setTextColor(ContextCompat.getColor(context, R.color.colorBlack))
                    dateText.textSize = 25f

                    textText.isEnabled = false
                    textText.setTextColor(ContextCompat.getColor(context, R.color.colorBlack))
                }

                val photoByteArray = cursor.getBlob(photoIndex)
                val photoBitmap = BitmapFactory.decodeByteArray(photoByteArray,0, photoByteArray.size)
                choosePhoto.setImageBitmap(photoBitmap)

            }
        }
    }

    //SQLite'a bitmapi atabilmek için boyutunu küçültme işlemi
    //SQLite'a kaydetmek için bitmap formatındaki görseli veriye çevirme işlemi
    fun bitmapToBytArray(bitmap : Bitmap) : ByteArray{

        val smallBitmap = makeSmallBitmap(bitmap,300)
        val outputStream = ByteArrayOutputStream()
        smallBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)

        return outputStream.toByteArray()
    }

    fun insert(){

        val dateText = dateText.text.toString()
        val textText = textText.text.toString()

        if(photo != null){

            val byteArray = bitmapToBytArray(photoBitmap!!)

            try {

                context?.let {
                    val database = it.openOrCreateDatabase("Diary",Context.MODE_PRIVATE,null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS Entries (id INTEGER PRIMARY KEY, date VARCHAR, text VARCHAR, photo BLOB )")

                    val sqlString = "INSERT INTO Entries(date , text , photo) VALUES(? , ? , ?)"
                    val statement = database.compileStatement(sqlString)
                    statement.bindString(1, dateText)
                    statement.bindString(2,textText)
                    statement.bindBlob(3,byteArray)
                    statement.execute()

                }

            } catch (e: Exception){
                e.printStackTrace()
            }

        }
    }

    fun update(){

        val dateText = dateText.text.toString()
        val textText = textText.text.toString()

        val database = context?.openOrCreateDatabase("Diary",Context.MODE_PRIVATE,null)

        if(photo != null){

            val byteArray = bitmapToBytArray(photoBitmap!!)

            val sqlString = "UPDATE Entries SET date = ?, text = ?, photo = ? WHERE id = $entryId"
            val statement = database?.compileStatement(sqlString)
            statement?.bindString(1, dateText)
            statement?.bindString(2,textText)
            statement?.bindBlob(3,byteArray)
            statement?.execute()

            /*
            val cv = ContentValues()
            cv.put("date", dateText)
            cv.put("text", textText)
            cv.put("photo", byteArray)
            val whereclause = "id=?"
            val whereargs = arrayOf(entryId.toString())
            database.update("Entries", cv, whereclause, whereargs)
             */

        } else {

            val sqlString = "UPDATE Entries SET date = ?, text = ? WHERE id = $entryId"
            val statement = database?.compileStatement(sqlString)
            statement?.bindString(1, dateText)
            statement?.bindString(2,textText)
            statement?.execute()

        }
    }

    fun choosePhotoClicked(){
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

                        val source = ImageDecoder.createSource(requireContext().contentResolver , photo!!)
                        photoBitmap = ImageDecoder.decodeBitmap(source)
                        choosePhoto.setImageBitmap(photoBitmap)

                    } else {

                        photoBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver , photo)
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
