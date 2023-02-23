package com.tuna.yemektariflerisqlite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.tuna.yemektariflerisqlite.listeRecylerAdapter.YemekHolder
import kotlinx.android.synthetic.main.fragment_liste.view.*
import kotlinx.android.synthetic.main.reyciler_row.view.*

class listeRecylerAdapter (val yemekListesi:ArrayList<String>,val idListesi:ArrayList<Int>):RecyclerView.Adapter<YemekHolder>(){
    class YemekHolder(ItemView:View):RecyclerView.ViewHolder(ItemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YemekHolder {
        val inflater=LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.reyciler_row,parent,false)
        return YemekHolder(view)

    }

    override fun onBindViewHolder(holder: YemekHolder, position: Int) {
        holder.itemView.recycler_row_text.text=yemekListesi[position]
        holder.itemView.setOnClickListener {
            val action=ListeFragmentDirections.actionListeFragmentToTarifFragment()
            Navigation.findNavController(it).navigate(action)

        }


    }

    override fun getItemCount(): Int {
        return yemekListesi.size
    }

}