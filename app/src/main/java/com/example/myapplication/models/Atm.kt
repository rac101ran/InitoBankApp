package com.example.myapplication.models

class Atm {
    var pin: String = ""
    var validFrom: String = ""
    var validThru: String = ""
    var cvv: String = ""

    constructor()

    constructor(pin: String, validFrom: String, validThru: String, cvv: String) {
        this.pin = pin
        this.validFrom = validFrom
        this.validThru = validThru
        this.cvv = cvv
    }
}
