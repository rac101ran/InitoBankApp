package com.example.myapplication

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentListofTypeAccountsBinding
import com.example.myapplication.databinding.FragmentTypesofaccountBinding
import com.example.myapplication.models.AccountInfo
import com.example.myapplication.utils.AccountAdapter
import com.example.myapplication.utils.ProgressDialogHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ListofTypeAccountsFragment : Fragment() {

    private lateinit var binding : FragmentListofTypeAccountsBinding
    private var alertDialog : AlertDialog? = null
    private var animation : Animation? = null
    private lateinit var adapter : AccountAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentListofTypeAccountsBinding.inflate(layoutInflater)
        alertDialog = context?.let { ProgressDialogHelper.getDialog(it,layoutInflater) }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = AccountAdapter(context)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        animation = AnimationUtils.loadAnimation(context, R.anim.animcard)

        binding.savingAccount.setOnClickListener {
              it.startAnimation(animation)
              openTypeAccount("savings")
        }
        binding.currentAccount.setOnClickListener {
            it.startAnimation(animation)
            openTypeAccount("current")
        }
        binding.loanAccount.setOnClickListener {
            it.startAnimation(animation)
            openTypeAccount("loan")
        }

    }

    fun openTypeAccount(type : String) {
        alertDialog?.show()
        lifecycleScope.launch(Dispatchers.IO) {
            val auth = FirebaseAuth.getInstance().currentUser?.uid

            val name = FirebaseDatabase.getInstance().getReference("bank").child("users").child(auth.toString()).child("personal").child("name").get().await().getValue(String::class.java)
            val accountList = FirebaseDatabase.getInstance().getReference("bank").child("users").child(auth.toString())
                .child("accounts").child(type).get().await().children

            val accounts = mutableListOf<AccountInfo>()
            accountList.forEach {
                val newAccount = AccountInfo(
                    accountid = it.child("accountid").getValue(String::class.java).toString() + "&" + type,
                    createdOn = it.child("createdOn").getValue(String::class.java).toString(),
                    balance = it.child("balance").getValue(String::class.java).toString(),
                    atm = null,
                    interest = null,
                    referralCode = null,
                    nrv = null)
                newAccount.nameInside = name.toString()
                accounts.add(newAccount)
            }


            Log.e("accounts",accounts.toString())
            withContext(Dispatchers.Main) {
                adapter.updateAccounts(accounts)
                alertDialog?.dismiss()
            }
        }
    }
}



