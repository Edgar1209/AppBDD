package com.example.app_redes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainActivity : AppCompatActivity(){
    var urlLogin = "http://192.168.43.225:3000/usuario/login/"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Toast.makeText(this, "Wait a second", Toast.LENGTH_LONG).show()


        val btnLogin  : Button = findViewById(R.id.inicioSesion)
        val btnregistro : Button = findViewById(R.id.registro)

        val nombre: EditText = findViewById(R.id.nombre)
        val contraseña: EditText = findViewById(R.id.contraseña)


        btnLogin.setOnClickListener{
            lifecycleScope.launch {
                login(nombre.text.toString(),contraseña.text.toString())
            }
        }

        btnregistro.setOnClickListener {
            val intent = Intent(this, Direccion::class.java)
            startActivity(intent)
        }
    }
    suspend fun login(nombre:String,contraseña:String){
        val params = HashMap<String, String>()
        params["nombre"] = nombre
        params["password"] = contraseña
        val jsonObject = JSONObject(params as Map<*, *>)
        Log.d("Json: ", jsonObject.toString())

        val request = object : JsonObjectRequest(
            Method.POST, urlLogin, jsonObject,
            Response.Listener { response ->
                // manejar respuesta
                Log.d("Respuesta",response.toString())
                val id = "${response["id"]}"
                val intent = Intent(this, MyPets::class.java)
                intent.putExtra("ID", id)
                intent.putExtra("User", nombre)
                startActivity(intent)
            },
            Response.ErrorListener { error ->
                // manejar error
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                Log.e("Error:", error.toString())
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        val rQueue = Volley.newRequestQueue(this@MainActivity)
        rQueue.add(request)

        delay(2000)
    }
}