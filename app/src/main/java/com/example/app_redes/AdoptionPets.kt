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
import kotlinx.android.synthetic.main.activity_adoption_pets.*

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray

class AdoptionPets : AppCompatActivity() {
    var urlMascotas= "http://192.168.43.225:3000/mascotas/"
    val listaMascotas = mutableListOf<Mascota>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adoption_pets)
        val idU = intent.getStringExtra("ID")
        val usuario = intent.getStringExtra("User")

        val bienvenida : TextView = findViewById(R.id.holaAdoption)
        bienvenida.text = "Hola $usuario"

        lifecycleScope.launch {
            getMascotasAdopcion()
            listaAdoption.setOnItemClickListener{ parent, view, position, id->
                val intent = Intent(this@AdoptionPets, MascotaDetallada::class.java)
                intent.putExtra("ID", idU)
                intent.putExtra("User", usuario)
                intent.putExtra("Mascota", listaMascotas[position])
                startActivity(intent)
            }
        }
        val user: ImageView = findViewById(R.id.userAdoption)
        user.setOnClickListener(){
            val intent = Intent(this, UserInfo:: class.java)
            intent.putExtra("ID", idU)
            intent.putExtra("User", usuario)
            startActivity(intent)
        }

        val myPets: ImageView = findViewById(R.id.mypetsAdoption)
        myPets.setOnClickListener(){
            val intent = Intent(this, MyPets:: class.java)
            intent.putExtra("ID", idU)
            intent.putExtra("User", usuario)
            startActivity(intent)
        }

        val perdido: ImageView = findViewById(R.id.perdidosAdoption)
        perdido.setOnClickListener(){
            val intent = Intent(this, LostPets:: class.java)
            intent.putExtra("ID", idU)
            intent.putExtra("User", usuario)
            startActivity(intent)
        }

        val adopcion: ImageView = findViewById(R.id.adopcionAdoption)
        adopcion.setOnClickListener(){
            Toast.makeText(this, "Usted se encuentra en esa seccion", Toast.LENGTH_SHORT).show()
        }
    }

    suspend fun getMascotasAdopcion() {
        val request = StringRequest(
            Request.Method.GET,
            urlMascotas,
            {response->
                val data = response.toString()
                var JArray = JSONArray(data)
                for (i in 0 until JArray.length()) {
                    val jsonObject = JArray.getJSONObject(i)
                    if (jsonObject["id_status"]==2) {
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
                            jsonObject.getString("genero"), jsonObject.getInt("id"),raza,"Adopcion",
                            jsonObject.getInt("id_usuario"),jsonObject.getString("nacimiento"),
                            jsonObject.getString("nombre"))
                        listaMascotas.add(aux)
                    }
                }
                listaAdoption.adapter= MascotaAdapter(this@AdoptionPets, listaMascotas)
                Log.d("No Mascotas", listaMascotas.toString())
            },
            {error-> Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
                Log.e("Error", error.toString())
            }
        )
        val rQueue = Volley.newRequestQueue(this@AdoptionPets)
        rQueue.add(request)
        delay(2000)
    }
}