package com.example.mvvmhowlstagram.login

import android.app.Application
import android.view.View
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mvvmhowlstagram.R
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginViewModel(application: Application) :AndroidViewModel(application) {
    var auth = FirebaseAuth.getInstance()
    var id : MutableLiveData<String> = MutableLiveData("")
    var password : MutableLiveData<String> = MutableLiveData("")

    var showInputNumberActivity : MutableLiveData<Boolean> = MutableLiveData(false)
    var showFindIdActivity : MutableLiveData<Boolean> = MutableLiveData(false)
    var showMainActivity : MutableLiveData<Boolean> = MutableLiveData(false)

    var context = getApplication<Application>().applicationContext

    var googleSignInClient : GoogleSignInClient

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(context,R.string.default_web_client_id))  // 여기서 Web Client ID 확인
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context,gso)
    }

    fun loginWithSignUpEmail(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                showInputNumberActivity.value = true
            }else{
                loginEmail(email,password)
            }
        }
    }
    fun loginEmail(email : String, password : String){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.result.user?.isEmailVerified == true) {
                showMainActivity.value = true
            } else {
                showInputNumberActivity.value = true
            }
        }
    }
    fun loginGoogle(view : View){
        var i = googleSignInClient.signInIntent
        (view.context as? LoginActivity)?.googleLoginResult?.launch(i)
    }
    fun firebaseAuthWithGoogle(idToken : String?){
        var credential = GoogleAuthProvider.getCredential(idToken,null)
        auth.signInWithCredential(credential).addOnCompleteListener{
            if(it.isSuccessful){
                if (it.result.user?.isEmailVerified == true) {
                    showMainActivity.value = true
                } else {
                    showInputNumberActivity.value = true
                }
            }
        }
    }
    fun firebaseAuthWithFacebook(accessToken: AccessToken){
        var credential = FacebookAuthProvider.getCredential(accessToken.token)
        auth.signInWithCredential(credential).addOnCompleteListener{
            if(it.isSuccessful){
                if (it.result.user?.isEmailVerified == true) {
                    showMainActivity.value = true
                } else {
                    showInputNumberActivity.value = true
                }
            }
        }
    }
}