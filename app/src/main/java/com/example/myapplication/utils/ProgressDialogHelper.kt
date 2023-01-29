package com.example.myapplication.utils

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.view.LayoutInflater
import com.example.myapplication.R

object ProgressDialogHelper {
    fun getDialog(context: Context,layoutInflater : LayoutInflater) : AlertDialog {
        val builder = AlertDialog.Builder(context)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        return builder.create()
    }
}