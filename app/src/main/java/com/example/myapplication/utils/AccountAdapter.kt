package com.example.myapplication.utils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Account
import com.example.myapplication.R
import com.example.myapplication.models.AccountInfo
import com.google.android.gms.common.internal.Objects.ToStringHelper


class AccountAdapter(val context : Context?) : RecyclerView.Adapter<AccountViewHolder>() {
    private val accounts = mutableListOf<AccountInfo>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.account_item, parent, false)
        return AccountViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        holder.bind(accounts[position])
        holder.itemView.setOnClickListener {
              val intent = Intent(context,Account::class.java)
              intent.putExtra("accountid",accounts[position].accountid.toString().split("&")[0])
              intent.putExtra("name",accounts[position].nameInside)
              intent.putExtra("type",accounts[position].accountid.toString().split("&")[1])
              context?.startActivity(intent)
             // Toast.makeText(holder.itemView.context,accounts[position].accountid.toString(),Toast.LENGTH_SHORT).show()
        }

//        setOnClickListener(object : OnClickListener() {
//            override fun onClick(view: View?) {
//                Toast.makeText(context, "Recycle Click$position", Toast.LENGTH_SHORT).show()
//            }
//        })
    }

    class OnClickListener(val clickListener: (account: AccountInfo) -> Unit) {
        fun onClick(account: AccountInfo) = clickListener(account)
    }


    override fun getItemCount(): Int {
        return accounts.size
    }

    fun updateAccounts(newAccounts: List<AccountInfo>) {
        accounts.clear()
        accounts.addAll(newAccounts)
        notifyDataSetChanged()
    }
}
