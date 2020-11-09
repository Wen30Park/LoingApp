package com.albrivas.login_firebase

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.albrivas.login_firebase.utils.Modelo

class Login : AppCompatActivity(), View.OnClickListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var emailUsuario: EditText
    private lateinit var passUsuario: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnLoginGoogle: SignInButton
    private val RC_SIGN_IN: Int = 123
    private lateinit var modelo: Modelo


    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        instanciasFirebase()
        instanciasGoogle()
        instancias()
        acciones()
        //modelo.activityTransparent(this@Login)
    }

    override fun onStart() {
        super.onStart()
        if (mAuth.currentUser != null) {
            startActivity(Intent(this@Login, Principal::class.java))
            finish()
        }
    }

    private fun instanciasGoogle() {
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
    }

    private fun instanciasFirebase() {
        mAuth = FirebaseAuth.getInstance() //Autenticacion de Firebase
    }

    private fun instancias() {
        emailUsuario = findViewById(R.id.input_email)
        passUsuario = findViewById(R.id.input_password)
        btnLogin = findViewById(R.id.btn_login)
        btnLoginGoogle = findViewById(R.id.boton_login_google)
        modelo = Modelo()
    }

    private fun acciones() {
        btnLogin.setOnClickListener(this)
        btnLoginGoogle.setOnClickListener(this)
    }


    private fun signInFirebase(email: String, password: String) {

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(applicationContext, getString(R.string.email_introducir), Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(applicationContext, getString(R.string.pass_introducir), Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            passUsuario.error = getString(R.string.pass_longitud)
            return
        }

        modelo.showDialog(this@Login, getString(R.string.dialog_sesion))

        mAuth.signInWithEmailAndPassword(email, password).
                addOnCompleteListener(this) {task ->

                    if(task.isSuccessful) {
                        modelo.hideDialog()
                        startActivity(Intent(this@Login, Principal::class.java))
                        finish()
                    }
                    else {
                        modelo.hideDialog()
                        Toast.makeText(applicationContext,
                                getString(R.string.error_autenticacion_usuario), Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun signInGoogle() {
        val intent = mGoogleSignInClient.signInIntent
        startActivityForResult(intent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                //Inicio de sesion correcto, autenticamos con firebase
                val account= task.getResult(ApiException::class.java)

                firebaseAuthGooogle(account)

            } catch (e: ApiException) {
                Toast.makeText(this@Login, getString(R.string.error_conexion), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthGooogle(account: GoogleSignInAccount) {

        modelo.showDialog(this@Login, getString(R.string.dialog_sesion))

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        mAuth.signInWithCredential(credential).addOnCompleteListener(this) { task ->

            if(task.isSuccessful) {
                modelo.hideDialog()
                startActivity(Intent(this@Login, Principal::class.java))
                finish()
            }
            else {
                modelo.hideDialog()
                Toast.makeText(this@Login, getString(R.string.error_login), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onClick(p0: View?) {

        when (p0?.id) {

            R.id.btn_login -> {

                if(modelo.compruebaConexion(applicationContext)) {
                    signInFirebase(emailUsuario.text.toString(), passUsuario.text.toString())
                }
                else {
                    Toast.makeText(this@Login, getString(R.string.error_conexion), Toast.LENGTH_LONG).show()
                }
            }

            R.id.boton_login_google -> {

                if(modelo.compruebaConexion(applicationContext)) {
                    signInGoogle()
                }
                else {
                    Toast.makeText(this@Login, getString(R.string.error_conexion), Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
