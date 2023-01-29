package com.example.myapplication.models

class Transactions {
    var events : HashMap<String,Any>? = null
    var dailyLimit : String = ""
    var monthlyCount : String = ""
    var referralCode : String = ""
    var balance : String = ""
    var type : String = ""
    constructor()

    constructor(events : HashMap<String,Any> , dailyLimit : String , monthlyCount : String , referralCode : String , balance : String , type : String) {
        this.events = events
        this.dailyLimit = dailyLimit
        this.type = type
        this.referralCode = referralCode
        this.monthlyCount = monthlyCount
        this.balance = balance
    }

}