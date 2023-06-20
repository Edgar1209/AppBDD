package com.example.app_redes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_lost_pets.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray

class LostPets : AppCompatActivity() {
    var urlMascotas= "http://192.168.43.225:3000/mascotas/"
    val listaMascotas = mutableListOf<Mascota>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lost_pets)
        val idU = intent.getStringExtra("ID")
        val usuario = intent.getStringExtra("User")

        val bienvenida : TextView = findViewById(R.id.holaLost)
        bienvenida.text = "Hola $usuario"

        lifecycleScope.launch {
            getMascotasPerdidas()
            listaLost.setOnItemClickListener{ parent, view, position, id->
                val intent = Intent(this@LostPets, MascotaDetallada::class.java)
                intent.putExtra("ID", idU)
                intent.putExtra("User", usuario)
                intent.putExtra("Mascota", listaMascotas[position])
                startActivity(intent)
            }
        }

        val user: ImageView = findViewById(R.id.userLost)
        user.setOnClickListener(){
            val intent = Intent(this, UserInfo:: class.java)
            intent.putExtra("ID", idU)
            intent.putExtra("User", usuario)
            startActivity(intent)
        }

        val myPets: ImageView = findViewById(R.id.mypetsLost)
        myPets.setOnClickListener(){
            val intent = Intent(this, MyPets:: class.java)
            intent.putExtra("ID", idU)
            intent.putExtra("User", usuario)
            startActivity(intent)
        }

        val perdido: ImageView = findViewById(R.id.perdidosLost)
        perdido.setOnClickListener(){
            Toast.makeText(this, "Usted se encuentra en esa seccion", Toast.LENGTH_SHORT).show()
        }

        val adopcion: ImageView = findViewById(R.id.adopcionLost)
        adopcion.setOnClickListener(){
            val intent = Intent(this, AdoptionPets:: class.java)
            intent.putExtra("ID", idU)
            intent.putExtra("User", usuario)
            startActivity(intent)
        }
    }

    suspend fun getMascotasPerdidas() {
        val request = StringRequest(
            Request.Method.GET,
            urlMascotas,
            {response->
                val data = response.toString()
                var JArray = JSONArray(data)
                for (i in 0 until JArray.length()) {
                    val jsonObject = JArray.getJSONObject(i)
                    if (jsonObject["id_status"]==1) {
                        var raza=if (jsonObject["id_raza"]==1) {
                            "Golden Retriver"
                        } else if (jsonObject["id_raza"]==2) {
                            "Cocker Spaniel"
                        }else if (jsonObject["id_raza"]==3) {
                            "Pomerania"
                        } else if (jsonObject["id_raza"]==4) {
                            "Dalmata"
                        }else if (jsonObject["id_raza"]==5) {
                            "Pastor Aleman"
                        }else {"Bulldog"}
                        val aux= Mascota(jsonObject.getString("color"),jsonObject.getString("descripcion"),
                            jsonObject.getString("genero"), jsonObject.getInt("id"),raza,"Perdido",
                            jsonObject.getInt("id_usuario"),jsonObject.getString("nacimiento"),
                            jsonObject.getString("nombre"))
                        listaMascotas.add(aux)
                    }
                }
                listaLost.adapter= MascotaAdapter(this@LostPets, listaMascotas)
                Log.d("No Mascotas", listaMascotas.toString())
            },
            {error-> Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
                Log.e("Error", error.toString())
            }
        )
        val rQueue = Volley.newRequestQueue(this@LostPets)
        rQueue.add(request)
        delay(2000)
    }
}