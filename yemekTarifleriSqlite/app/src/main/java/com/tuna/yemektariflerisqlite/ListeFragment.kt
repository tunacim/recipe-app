package com.tuna.yemektariflerisqlite

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_liste.*
import java.lang.Exception


class ListeFragment : Fragment() {
    var yemekIsmiListesi=ArrayList<String>()
    var yemekIdListesi=ArrayList<Int>()
    private lateinit var ListeAdapter:listeRecylerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_liste, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ListeAdapter= listeRecylerAdapter(yemekIsmiListesi,yemekIdListesi)
        RecylerView.layoutManager=LinearLayoutManager(context)
        RecylerView.adapter=ListeAdapter

        super.onViewCreated(view, savedInstanceState)
        sqlverialma()

    }
    fun sqlverialma(){
        try {
            activity?.let {
                val database=it.openOrCreateDatabase("yemekler", Context.MODE_PRIVATE,null)

                val cursor=database.rawQuery("SELECT * FROM yemekler",null)
                val yemekismiIndex=cursor.getColumnIndex("yemekismi")
                val yemekIdIndex=cursor.getColumnIndex("id")
                yemekIsmiListesi.clear()
                yemekIdListesi.clear()
                while (cursor.moveToNext()){
                  yemekIsmiListesi.add(cursor.getString(yemekismiIndex))
                    yemekIdListesi.add(cursor.getInt(yemekIdIndex))
                }
                ListeAdapter.notifyDataSetChanged()
                cursor.close()

            }



        }catch (e:Exception){
            e.printStackTrace()


        }
    }


}