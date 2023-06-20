package com.example.app_redes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

class Registro : AppCompatActivity() {
    var urlRegistro = "http://192.168.43.225:3000/usuario/"

    override fun onCreate(savedInstanceState: Bundle?) {
        var direccion = intent.getStringExtra("Direccion") as String
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        val continuar : Button=findViewById(R.id.signupContinue)
        val nombre: EditText = findViewById(R.id.signupNombre)
        val ap: EditText = findViewById(R.id.signupPaterno)
        val am: EditText = findViewById(R.id.signupMaterno)
        val telefono: EditText = findViewById(R.id.signupTelefono)
        val email: EditText = findViewById(R.id.signupMail)
        val password: EditText = findViewById(R.id.signupContraseña)

        continuar.setOnClickListener{
            lifecycleScope.launch {
                registrar(nombre.text.toString(),ap.text.toString(),am.text.toString(),telefono.text.toString(),email.text.toString(),password.text.toString(),direccion)
            }
        }
    }
    suspend fun registrar(nombre:String,ap:String,am:String,telefono:String,email:String,password:String,direccion:String){
        val params = HashMap<String, String>()
        params["nombre"] = nombre
        params["apellido_p"] = ap
        params["apellido_m"] = am
        params["telefono"] = telefono
        params["email"] = email
        params["password"] = password
        params["direccion"] = direccion
        params["status_conectado"] = "0"
        val jsonObject = JSONObject(params as Map<*, *>)
        Log.d("Json: ", jsonObject.toString())

        val request = object : JsonObjectRequest(
            Method.PUT, urlRegistro, jsonObject,
            Response.Listener { response ->
                // manejar respuesta
                Log.d("Respuesta",response.toString())
                Toast.makeText(this, "Bienvenido $nombre", Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener { error ->
                // manejar error
                Log.e("Error:", error.toString())
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        val rQueue = Volley.newRequestQueue(this@Registro)
        rQueue.add(request)

        delay(2000)
    }
}