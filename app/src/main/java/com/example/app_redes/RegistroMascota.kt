package com.example.app_redes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

class RegistroMascota : AppCompatActivity() {
    private lateinit var idUser:String
    private lateinit var user:String
    private lateinit var color: EditText
    private lateinit var descripcion: EditText
    private lateinit var genero: EditText
    private lateinit var raza: EditText
    private lateinit var status: EditText
    private lateinit var nacimiento: EditText
    private lateinit var nombre: EditText
    var urlAgregar= "http://192.168.43.225:3000/mascota/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_mascota)
        val id = intent.getStringExtra("ID")
        val usuario = intent.getStringExtra("User")
        if (id != null) {
            idUser=id
            if (usuario != null) {
                user=usuario
            }
        }
        val agregar:Button=findViewById(R.id.agregarMascota)
        color=findViewById(R.id.petColor)
        descripcion=findViewById(R.id.petDesc)
        genero=findViewById(R.id.petGenero)
        raza=findViewById(R.id.petRaza)
        status=findViewById(R.id.petStatus)
        nacimiento=findViewById(R.id.petNacimiento)
        nombre=findViewById(R.id.petNombre)
        agregar.setOnClickListener {
            lifecycleScope.launch {
                addPet(nombre.text.toString(), raza.text.toString(),color.text.toString(),descripcion.text.toString(),nacimiento.text.toString(), genero.text.toString(),status.text.toString(),idUser)
            }
        }


    }
    suspend fun addPet(nombre:String,raza:String,color:String,descripcion:String,nacimiento:String,genero:String,idS:String,idU:String){
        val params = HashMap<String, String>()
        params["nombre"] = nombre
        params["id_raza"] = raza
        params["color"] = color
        params["descripcion"] = descripcion
        params["nacimiento"] = nacimiento
        params["genero"] = genero
        params["id_status"] = idS
        params["id_usuario"] = idU
        val jsonObject = JSONObject(params as Map<*, *>)
        Log.d("Json: ", jsonObject.toString())

        val request = object : JsonObjectRequest(
            Method.PUT, urlAgregar, jsonObject,
            Response.Listener { response ->
                // manejar respuesta
                Log.d("Respuesta",response.toString())
                val intent = Intent(this, UserInfo:: class.java)
                intent.putExtra("ID", idUser)
                intent.putExtra("User", user)
                startActivity(intent)
            },
            Response.ErrorListener { error ->
                // manejar error
                Log.e("Error:", error.toString())
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        val rQueue = Volley.newRequestQueue(this@RegistroMascota)
        rQueue.add(request)

        delay(2000L)
    }
}