package com.example.mvvmhowlstagram.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.example.mvvmhowlstagram.R
import com.example.mvvmhowlstagram.databinding.ActivityFindIdBinding

class FindIdActivity : AppCompatActivity() {
    lateinit var binding : ActivityFindIdBinding
    val findIdViewModel : FindIdViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindIdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observe()


        binding.findIdBtn.setOnClickListener {
            var phoneNumber = binding.edittextPhonenumber.text.toString()
            findIdViewModel.findMyId(phoneNumber)
        }
        binding.findPasswordBtn.setOnClickListener {
            var id = binding.edittextEmail.text.toString()
            findIdViewModel.findMyPassword(id)
        }
        binding.dismissBtn.setOnClickListener {
            finish()
        }
    }
    fun observe(){
        findIdViewModel.toastMessage.observe(this){
            if(it.isNotEmpty()){
                Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
            }
        }
    }
}