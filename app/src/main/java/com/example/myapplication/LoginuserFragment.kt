package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.databinding.FragmentCreatesavingsaccfragmentBinding
import com.example.myapplication.databinding.FragmentLoginuserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginuserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginuserFragment : Fragment() {
   
    private lateinit var binding: FragmentLoginuserBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginuserBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().getReference("bank").child("users")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentManager = activity?.supportFragmentManager?.beginTransaction()
        binding.btlogin.setOnClickListener {

            binding.btlogin.isEnabled = false

            auth.signInWithEmailAndPassword(binding.etLoginEmailid.text.toString(),binding.etpassword.text.toString()).addOnSuccessListener {

                fragmentManager?.replace(R.id.fragmentContainerid,ListofTypeAccountsFragment())
                fragmentManager?.commit()

            }.addOnFailureListener {
                binding.btlogin.isEnabled = false
            }
        }
    }

}