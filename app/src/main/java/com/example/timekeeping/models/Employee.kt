package com.example.timekeeping.models

import com.google.firebase.firestore.Exclude

class Employee (
    @Exclude
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var phone: String = "",
    var address: String = "",
) {
}
