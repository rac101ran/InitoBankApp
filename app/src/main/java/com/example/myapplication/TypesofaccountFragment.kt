package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.FragmentTypesofaccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class TypesofaccountFragment : Fragment() {



    private lateinit var binding : FragmentTypesofaccountBinding
    private var sharedPreferences : SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTypesofaccountBinding.inflate(layoutInflater)

        sharedPreferences = context?.getSharedPreferences(context?.packageName.toString(),Context.MODE_PRIVATE)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentManager = activity?.supportFragmentManager?.beginTransaction()

        val createsavingsaccfragment = Createsavingsaccfragment()

        binding.bttypesaving.setOnClickListener {
            sharedPreferences?.edit()?.putString("current_type_cache","savings")?.apply()
            sharedPreferences?.edit()?.putString("nrv","1000000")?.apply()
            sharedPreferences?.edit()?.putString("interest","0.6")?.apply()
            fragmentManager?.replace(R.id.fragmentContainerid,createsavingsaccfragment)
            fragmentManager?.commit()
        }

        binding.bttypecurrent.setOnClickListener {
            sharedPreferences?.edit()?.putString("current_type_cache","current")?.apply()
            sharedPreferences?.edit()?.putString("nrv","5000000")?.apply()
            sharedPreferences?.edit()?.putString("interest","0.0")?.apply()
            fragmentManager?.replace(R.id.fragmentContainerid,createsavingsaccfragment)
            fragmentManager?.commit()

        }

        binding.bttypeloan.setOnClickListener {
            val auth = FirebaseAuth.getInstance().currentUser
            if(auth!=null) {
                 lifecycleScope.launch(Dispatchers.IO) {
                        val userPath = FirebaseDatabase.getInstance().getReference("bank").child("users").child(auth.uid.toString()).child("accounts")
                        if(userPath.get().await().hasChild("savings") || userPath.get().await().hasChild("current")) {
                            withContext(Dispatchers.Main) {
//                                Toast.makeText(context,"You don't have a savings or current account",Toast.LENGTH_SHORT).show()
                                sharedPreferences?.edit()?.putString("current_type_cache","loan")?.apply()
                                sharedPreferences?.edit()?.putString("homeloan","7.0")?.apply()
                                sharedPreferences?.edit()?.putString("carloan","8.0")?.apply()
                                sharedPreferences?.edit()?.putString("businessloan","15.0")?.apply()
                                sharedPreferences?.edit()?.putString("personalloan","12.0")?.apply()
                                startActivity(Intent(activity,Account::class.java))
                            }
                        }else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context,"You don't have a savings or current account",Toast.LENGTH_SHORT).show()
                            }
                        }
                 }
            }
        }

    }
}