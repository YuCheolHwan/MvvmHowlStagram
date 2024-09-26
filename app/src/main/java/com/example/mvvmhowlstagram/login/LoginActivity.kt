package com.example.mvvmhowlstagram.login

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.mvvmhowlstagram.databinding.ActivityLoginBinding
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Arrays


class LoginActivity : AppCompatActivity() {
    val TAG = "LoginActivity"
    lateinit var binding : ActivityLoginBinding
    lateinit var auth : FirebaseAuth
    val loginViewModel : LoginViewModel by viewModels()
    lateinit var callbackManager : CallbackManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()
        observe()
        printHashKey(this)

        binding.emailLoginButton.setOnClickListener {
            loginViewModel.id.value = binding.edittextId.text.toString()
            loginViewModel.password.value = binding.edittextPassword.text.toString()
            loginViewModel.loginWithSignUpEmail()
        }
        binding.findIdPasswordButton.setOnClickListener {
            findId()
        }
        binding.googleLoginButton.setOnClickListener {
            loginViewModel.loginGoogle(binding.root)
        }
        binding.facebookLoginButton.setOnClickListener {
            loginFacebook()
        }
    }

    fun loginFacebook(){
        var loginManager = LoginManager.getInstance()
        loginManager.loginBehavior = LoginBehavior.WEB_ONLY
        loginManager.logInWithReadPermissions(this, Arrays.asList("email"))
        loginManager.registerCallback(callbackManager,object : FacebookCallback<LoginResult>{
            override fun onCancel() {
                TODO("Not yet implemented")
            }

            override fun onError(error: FacebookException) {
                TODO("Not yet implemented")
            }

            override fun onSuccess(result: LoginResult) {
                val token = result.accessToken
                loginViewModel.firebaseAuthWithFacebook(token)
            }

        })
    }

    fun printHashKey(pContext: Context) {
        try {
            val info = pContext.packageManager.getPackageInfo(
                pContext.packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.i(TAG, "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "printHashKey()", e)
        } catch (e: Exception) {
            Log.e(TAG, "printHashKey()", e)
        }
    }
    fun observe(){
        loginViewModel.showInputNumberActivity.observe(this){
            if(it){
                finish()
                startActivity(Intent(this,InputNumberActivity::class.java))
            }
        }
        loginViewModel.showFindIdActivity.observe(this){
            if (it){
                startActivity(Intent(this,FindIdActivity::class.java))
            }
        }
    }

    fun findId(){
        println("FindId")
        loginViewModel.showFindIdActivity.value = true
    }


    // 구글로그인이 성공한 결과값 변환 함수
    var googleLoginResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val data = result.data
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            loginViewModel.firebaseAuthWithGoogle(account.idToken)
        } catch (e: ApiException) {
            // 로그인을 실패한 경우 로그 처리
            Log.w("GoogleSignIn", "Google sign-in failed", e)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode,resultCode,data)
    }

}