package com.tuna.yemektariflerisqlite

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_tarif.*
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.net.URI
import java.util.jar.Manifest




class TarifFragment : Fragment() {
    var secilenGorsel : Uri?=null
    var secilenBitmap : Bitmap? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tarif, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        
        super.onViewCreated(view, savedInstanceState)
        Save.setOnClickListener {
            kaydet(it)
        }
        imageView.setOnClickListener {
            gorselSec(it)
        }
        arguments?.let {
            var gelenBilgi=TarifFragmentArgs.fromBundle(it).bilgi
            if(gelenBilgi.equals("menudengeldim")){
                //yeni bir yemek eklemye geldi
                yemekIsmiText.setText("")
                yemekTarifiText.setText("")
                Save.visibility=View.VISIBLE
                val GorselSecmeArkaplanı=BitmapFactory.decodeResource(context?.resources,R.drawable.download)
                imageView.setImageBitmap(GorselSecmeArkaplanı)


            }else{
                Save.visibility=View.INVISIBLE
               // daha önceki yemeği görmeye geldi
                val secilenId=TarifFragmentArgs.fromBundle(it).id
                context?.let {
                    try {

                        val database=it.openOrCreateDatabase("yemekler",Context.MODE_PRIVATE,null)
                        val cursor=database.rawQuery("SELECT * FROM yemekler WHERE id=?", arrayOf(secilenId.toString()))
                        val yemekIsmiIndex=cursor.getColumnIndex("yemekismi")
                        val yemekMalzemeIndex=cursor.getColumnIndex("yemekmalzemesi")
                        val yemekGorseli=cursor.getColumnIndex("gorsel")
                        while (cursor.moveToNext()){
                            yemekIsmiText.setText(cursor.getString(yemekIsmiIndex))
                            yemekTarifiText.setText(cursor.getString(yemekMalzemeIndex))
                           val byteDizisi=cursor.getBlob(yemekGorseli)
                            val bitmap=BitmapFactory.decodeByteArray(byteDizisi,0,byteDizisi.size)
                            imageView.setImageBitmap(bitmap)
                        }
                        cursor.close()

                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun kaydet(view: View){
        //sqlite a kaydetme
        val yemekIsmi=yemekIsmiText.text.toString()
        val yemekMalzemeleri=yemekTarifiText.text.toString()
        if(secilenBitmap!=null){
            val kucukBitmap=kucukBitmapOlustur(secilenBitmap!!,300)
            val outputStream=ByteArrayOutputStream()
            if (kucukBitmap != null) {
                kucukBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            }
            val byteDizisi=outputStream.toByteArray()
            try {
                context?.let {
                    val database=it.openOrCreateDatabase("yemekler", Context.MODE_PRIVATE,null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS yemekler (id INTEGER PRIMARY KEY,yemekismi VARCHAR,yemekmalzemesi VARCHAR,gorsel BLOB)")
                    val sqlString="INSERT INTO yemekler(yemekismi,yemekmalzemesi,gorsel)VALUES(?,?,?)"
                    val statement=database.compileStatement(sqlString)
                    statement.bindString(1,yemekIsmi)
                    statement.bindString(2,yemekMalzemeleri)
                    statement.bindBlob(3,byteDizisi)
                    statement.execute()

                }


            }catch (e:Exception){
                e.printStackTrace()

            }
            val action=TarifFragmentDirections.actionTarifFragmentToListeFragment()
            Navigation.findNavController(view).navigate(action)
        }

    }

fun gorselSec(view: View){
    activity?.let {

        if(ContextCompat.checkSelfPermission(it.applicationContext,android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            //izin verilmedi izin istememiz gerekiyor
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)

        }else{
            //izin verilmeden galeriye git
            val galeriIntent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galeriIntent,2)
        }
    }


}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==1){
            if(grantResults.size>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED ){
                //izni aldık
                val galeriIntent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent,2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(requestCode==2&& resultCode== Activity.RESULT_OK&& data!= null){
            secilenGorsel=data.data


            try {
                context?.let {
                    if(secilenGorsel!=null){
                        if(Build.VERSION.SDK_INT>= 28){
                            val source =ImageDecoder.createSource(it.contentResolver,secilenGorsel!!)
                            secilenBitmap=ImageDecoder.decodeBitmap(source)
                            imageView.setImageBitmap(secilenBitmap)
                        }else{
                            secilenBitmap=MediaStore.Images.Media.getBitmap(it.contentResolver,secilenGorsel)

                        }

                    }
                }



            }catch (e:Exception){
                e.printStackTrace()
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    fun kucukBitmapOlustur(kullanicininSectigiBitmap:Bitmap,maximumBoyut:Int): Bitmap? {
        var widht =kullanicininSectigiBitmap.width
        var height=kullanicininSectigiBitmap.height
        val bitmapOranı:Double=widht.toDouble()/height.toDouble()
        if (bitmapOranı>1){
            // görsel yatay
            widht =maximumBoyut
            val kisaltilmisHeight=widht/bitmapOranı
            height=kisaltilmisHeight.toInt()

        }else{
            //görsel dikey
            height =maximumBoyut
            val kisaltilmisWidth=height* bitmapOranı
            widht=kisaltilmisWidth.toInt()
        }

        return Bitmap.createScaledBitmap(kullanicininSectigiBitmap,widht,height,true)

    }
}