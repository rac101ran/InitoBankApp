package com.example.myapplication.utils

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.models.AccountInfo

class AccountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val accountImage = itemView.findViewById<ImageView>(R.id.account_image)
    private val accountId = itemView.findViewById<TextView>(R.id.account_id)
    private val accountCreatedOn = itemView.findViewById<TextView>(R.id.account_created_on)
    private val totalTransactions = itemView.findViewById<TextView>(R.id.total_transactions)
    private val currentBalance = itemView.findViewById<TextView>(R.id.current_balance)

    fun bind(account: AccountInfo) {
        accountId.text = "Account ID: ${account.accountid.toString().split("&")[0]}"
        accountCreatedOn.text = "Created on: ${account.createdOn}"
        totalTransactions.text = "Total Transactions: 0"
        currentBalance.text = "Current Balance: ${account.balance}"
    }
}
