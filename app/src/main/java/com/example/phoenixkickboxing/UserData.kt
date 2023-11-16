package com.example.phoenixkickboxing

class UserData (

    var useruid: String? = null,
    var fullName: String? = null,
    var email: String? = null,
    var cellphone: String? = null,
    var dateOB: String? = null,

    ) {
    // No-argument constructor required for Firebase serialization
    constructor() : this(null, null, null, null, null)
}