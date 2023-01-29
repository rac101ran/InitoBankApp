package com.example.myapplication.models

data class AccountInfo(val accountid : String? , val balance : String? , val createdOn : String? , val atm : Atm? , val interest : String? , val nrv : String?,
val referralCode : String?) {
    var nameInside : String = ""
}
