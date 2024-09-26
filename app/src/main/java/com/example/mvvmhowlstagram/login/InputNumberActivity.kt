package com.example.mvvmhowlstagram.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.mvvmhowlstagram.R
import com.example.mvvmhowlstagram.databinding.ActivityInputNumberBinding

class InputNumberActivity : AppCompatActivity() {
    lateinit var binding : ActivityInputNumberBinding
    val inputNumberViewModel : InputNumberViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply.setOnClickListener {
            inputNumberViewModel.inputNumber = binding.edittextPhonenumber.text.toString()
            inputNumberViewModel.savePhoneNumber()
        }
        observe()
    }
    fun observe(){
        inputNumberViewModel.nextPage.observe(this){
            if(it){
                finish()
                startActivity(Intent(this,LoginActivity::class.java))
            }
        }
    }
}