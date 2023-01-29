package com.example.myapplication

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.FragmentReferralcodeBinding
import com.example.myapplication.utils.ProgressDialogHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class referralcodeFragment : Fragment() {


    private lateinit var binding : FragmentReferralcodeBinding
    private var alertDialog : AlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReferralcodeBinding.inflate(layoutInflater)
        alertDialog = context?.let { ProgressDialogHelper.getDialog(it,layoutInflater) }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btreferralcode.setOnClickListener {
              when {
                  binding.etReferralCode.text.isNullOrEmpty() -> binding.etReferralCode.requestFocus()
                  else -> {
                        val accountNumber = binding.etReferralCode.text.toString()
                        binding.btreferralcode.isEnabled = false
                        alertDialog?.show()
                        lifecycleScope.launch(Dispatchers.IO) {
                             val accountInfoPath = FirebaseDatabase.getInstance().getReference("bank").child("accounts").get().await()
                             if(accountInfoPath.hasChild(accountNumber)) {
                                  val accountReferral = accountInfoPath.child(accountNumber).child("referralCode").getValue(String::class.java).toString()
                                  withContext(Dispatchers.Main) {
                                        binding.tvreferralcode.text = accountReferral
                                        alertDialog?.dismiss()
                                  }
                             }else {
                                 withContext(Dispatchers.Main) {
                                     binding.btreferralcode.isEnabled = true
                                     Toast.makeText(context,"Account Number does not exist",Toast.LENGTH_SHORT).show()
                                     alertDialog?.dismiss()
                                 }
                             }
                        }
                  }
              }
        }
    }
}