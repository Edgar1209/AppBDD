package com.example.app_redes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_my_pets.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray

class MyPets : AppCompatActivity() {
    var urlMascotasUsuario= "http://192.168.43.225:3000/mascota/usuario/"
    val listaMascotas = mutableListOf<Mascota>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_pets)
        val idU = intent.getStringExtra("ID")
        val usuario = intent.getStringExtra("User")
        val agregarPet:Button=findViewById(R.id.addPet)
        urlMascotasUsuario+= idU

        val bienvenida : TextView = findViewById(R.id.holaMy)
        bienvenida.setText("Hola $usuario")

        lifecycleScope.launch {
            getMascotas()
            listaMy.setOnItemClickListener{ parent, view, position, id->
                val intent = Intent(this@MyPets, MascotaDetallada::class.java)
                intent.putExtra("ID", idU)
                intent.putExtra("User", usuario)
                intent.putExtra("Mascota", listaMascotas[position])
                startActivity(intent)
            }
        }
        agregarPet.setOnClickListener {
            val intent = Intent(this@MyPets, RegistroMascota::class.java)
            intent.putExtra("ID", idU)
            intent.putExtra("User", usuario)
            Log.d("ID y User","$idU,$usuario")
            startActivity(intent)
        }
        val user: ImageView = findViewById(R.id.userMy)
        user.setOnClickListener(){
            val intent = Intent(this, UserInfo:: class.java)
            intent.putExtra("ID", idU)
            intent.putExtra("User", usuario)
            startActivity(intent)
        }

        val myPets: ImageView = findViewById(R.id.mypetsMy)
        myPets.setOnClickListener(){
            Toast.makeText(this, "Usted se encuentra en esa seccion", Toast.LENGTH_SHORT).show()
        }

        val perdido: ImageView = findViewById(R.id.perdidosMy)
        perdido.setOnClickListener(){
            val intent = Intent(this, LostPets:: class.java)
            intent.putExtra("ID", idU)
            intent.putExtra("User", usuario)
            startActivity(intent)
        }

        val adopcion: ImageView = findViewById(R.id.adopcionMy)
        adopcion.setOnClickListener(){
            val intent = Intent(this, AdoptionPets:: class.java)
            intent.putExtra("ID", idU)
            intent.putExtra("User", usuario)
            startActivity(intent)
        }
    }

    suspend fun getMascotas() {
        val request = StringRequest(
            Request.Method.GET,
            urlMascotasUsuario,
            {response->
                val data = response.toString()
                var JArray = JSONArray(data)
                for (i in 0 until JArray.length()) {
                    val jsonObject = JArray.getJSONObject(i)
                    var estado=if (jsonObject["id_status"]==1) "Perdido"
                    else "Adopcion"
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
                    Log.d("Raza y estado","$raza y $estado")
                    val aux= Mascota(jsonObject.getString("color"),jsonObject.getString("descripcion"),
                        jsonObject.getString("genero"), jsonObject.getInt("id"),raza,estado,
                        jsonObject.getInt("id_usuario"),jsonObject.getString("nacimiento"),
                        jsonObject.getString("nombre"))
                    Log.d("Mascota", aux.toString())
                    listaMascotas.add(aux)

                }
                listaMy.adapter = MascotaAdapter(this@MyPets, listaMascotas)
                Log.d("No Mascotas", listaMascotas.toString())
            },
            {error-> Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
                Log.e("Error", error.toString())
            }
        )
        val rQueue = Volley.newRequestQueue(this@MyPets)
        rQueue.add(request)
        delay(2000)
    }
}