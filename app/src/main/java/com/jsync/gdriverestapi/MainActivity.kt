package com.jsync.gdriverestapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
        private const val PICK_FILES = 313
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
            val intentFilePicker = Intent(Intent.ACTION_GET_CONTENT)
            intentFilePicker.type = "*/*"
            intentFilePicker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(Intent.createChooser(intentFilePicker, "Select files"), PICK_FILES)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            REQUEST_SIGN_IN -> {
                if (resultCode == Activity.RESULT_OK) {
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(data)
                    signInAccount = task.result
                    btnSelectFiles.isEnabled = true
                    signInButton.visibility = View.GONE
                } else
                    Toast.makeText(
                        this,
                        "You have to grant access to your google account!",
                        Toast.LENGTH_SHORT
                    ).show()
            }

            PICK_FILES -> {
                if (resultCode == Activity.RESULT_OK) {
                    if(data?.clipData != null){
                        data.clipData?.let {
                            txtFiles.text = ""
                            for (i in 0 until it.itemCount) {
                                txtFiles.text = txtFiles.text.toString() + it.getItemAt(i).uri.toString() + "\n\n"
                            }
                        }
                    }else{
                        txtFiles.text = ""
                        txtFiles.text = "${data?.data}"
                        Toast.makeText(
                            this,
                            "Single file selected!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }else{
                    Toast.makeText(
                        this,
                        "No files selected!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            else -> super.onActivityResult(requestCode, resultCode, data)
        }

    }
}
