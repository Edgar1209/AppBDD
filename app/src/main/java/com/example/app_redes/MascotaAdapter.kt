package com.example.app_redes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.item_mascota.view.*

class MascotaAdapter(private val adapterContext:Context, private val listaMascota: List<Mascota>):ArrayAdapter<Mascota>(adapterContext, 0, listaMascota) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layout = LayoutInflater.from(adapterContext).inflate(R.layout.item_mascota, parent, false)

        val mascota = listaMascota[position]

        layout.nombre.text = "Nombre: "+mascota.nombre
        layout.raza.text   = mascota.raza
        layout.Genero.text = "Genero: "+mascota.genero
        layout.Estado.text = "Status: "+mascota.status
        layout.Descripcion.text = mascota.descripcion
        layout.FechaNacimiento.text = mascota.nacimiento

        return layout
    }
}