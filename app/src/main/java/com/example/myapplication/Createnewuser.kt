package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.example.myapplication.databinding.ActivityCreatenewuserBinding

class Createnewuser : AppCompatActivity() {
    lateinit var binding : ActivityCreatenewuserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreatenewuserBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val fragmentManager = supportFragmentManager.beginTransaction()

        if(intent.getStringExtra("operate").toString() == "Signup") {
            val fragment  = TypesofaccountFragment()
            fragmentManager.replace(binding.fragmentContainerid.id,fragment)
            fragmentManager.commit()
        }else {
            val fragment = LoginuserFragment()
            fragmentManager.replace(binding.fragmentContainerid.id,fragment)
            fragmentManager.commit()
        }

    }
}