package com.example.app_redes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import org.json.JSONObject

class UserInfo : AppCompatActivity() {
    private lateinit var idUser:String
    private lateinit var direccion:String
    private lateinit var nombre:EditText
    private lateinit var ap: EditText
    private lateinit var am: EditText
    private lateinit var telefono: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    var urlUsuario= "http://192.168.43.225:3000/usuario/"
    var urlCambio= "http://192.168.43.225:3000/usuario/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)
        val id = intent.getStringExtra("ID")
        val usuario = intent.getStringExtra("User")
        urlUsuario+=id
        nombre=findViewById(R.id.UserNombre)
        ap=findViewById(R.id.UserPaterno)
        am=findViewById(R.id.UserMaterno)
        telefono=findViewById(R.id.UserTelefono)
        email=findViewById(R.id.UserMail)
        password=findViewById(R.id.UserContraseña)
        val continuar:Button=findViewById(R.id.UserContinue)
        val bienvenida : TextView = findViewById(R.id.holaUser)
        bienvenida.setText("Hola $usuario")


        lifecycleScope.launch {
            getUsuario()
        }
        continuar.setOnClickListener {
            lifecycleScope.launch {
                CambiarDatos(nombre.text.toString(),ap.text.toString(),am.text.toString(),telefono.text.toString(),email.text.toString(),password.text.toString())
            }

        }
        val user: ImageView = findViewById(R.id.userUser)
        user.setOnClickListener(){
            Toast.makeText(this, "Usted se encuentra en esa seccion", Toast.LENGTH_SHORT).show()
        }

        val myPets: ImageView = findViewById(R.id.mypetsUser)
        myPets.setOnClickListener(){
            val intent = Intent(this, MyPets:: class.java)
            intent.putExtra("ID", id)
            intent.putExtra("User", usuario)
            startActivity(intent)
        }

        val perdido: ImageView = findViewById(R.id.perdidosUser)
        perdido.setOnClickListener(){
            val intent = Intent(this, LostPets:: class.java)
            intent.putExtra("ID", id)
            intent.putExtra("User", usuario)
            startActivity(intent)
        }

        val adopcion: ImageView = findViewById(R.id.adopcionUser)
        adopcion.setOnClickListener(){
            val intent = Intent(this, AdoptionPets:: class.java)
            intent.putExtra("ID", id)
            intent.putExtra("User", usuario)
            startActivity(intent)
        }
    }
    suspend fun getUsuario() {
        val request = StringRequest(
            Request.Method.GET,
            urlUsuario,
            {response->
                val data = response.toString()
                val jsonObject = JSONObject(data)
                nombre.hint=jsonObject["nombre"].toString()
                ap.hint=jsonObject["apellido_p"].toString()
                am.hint=jsonObject["apellido_m"].toString()
                email.hint=jsonObject["email"].toString()
                password.hint=jsonObject["password"].toString()
                telefono.hint=jsonObject["telefono"].toString()
                direccion=jsonObject["direccion"].toString()
                idUser=jsonObject["id"].toString()
            },
            {error-> Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
                Log.e("Error", error.toString())
            }
        )
        val rQueue = Volley.newRequestQueue(this@UserInfo)
        rQueue.add(request)
        delay(2000)
    }
    suspend fun CambiarDatos(nombre:String,ap:String,am:String,telefono:String,email:String,password:String){
        val params = HashMap<String, String>()
        params["id"] = idUser
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
            Method.POST, urlCambio, jsonObject,
            Response.Listener { response ->
                // manejar respuesta
                Log.d("Respuesta",response.toString())
                val intent = Intent(this, UserInfo:: class.java)
                intent.putExtra("ID", idUser)
                intent.putExtra("User", nombre)
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

        val rQueue = Volley.newRequestQueue(this@UserInfo)
        rQueue.add(request)

        delay(2000L)
    }
}