package com.jsync.gdriverestapi

import android.app.Activity
import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var signInAccount: GoogleSignInAccount? = null
    companion object{
        private const val REQUEST_SIGN_IN = 321
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun checkSignIn() : Boolean{
        signInAccount = GoogleSignIn.getLastSignedInAccount(this)
        return signInAccount != null
    }

    private fun signIn(){
        val googleSignInOptions: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(Scopes.DRIVE_APPFOLDER))
            .build()

        val signInClient: GoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        val signInIntent = signInClient.signInIntent
        startActivityForResult(signInIntent, REQUEST_SIGN_IN)
    }

    private fun initViews(){
        if(!checkSignIn()) {
            val txtView = signInButton.getChildAt(0) as TextView
            txtView.text = getString(R.string.sign_with_google)
            signInButton.setOnClickListener {
                signIn()
            }
        }else {
            signInButton.visibility = View.GONE
            btnSelectFiles.isEnabled = true
        }


        btnSelectFiles.setOnClickListener {

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_SIGN_IN){
          if(resultCode == Activity.RESULT_OK){
              val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
              signInAccount = task.result
              btnSelectFiles.isEnabled = true
              signInButton.visibility = View.GONE
          }else
              Toast.makeText(this, "You have to grant access to your google account!", Toast.LENGTH_SHORT).show()
        } else
            super.onActivityResult(requestCode, resultCode, data)

    }
}
