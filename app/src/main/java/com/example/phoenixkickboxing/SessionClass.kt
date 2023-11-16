package com.example.phoenixkickboxing

class SessionClass(
    var date: String? = null,
    var name: String? = null,

){
    // No-argument constructor required for Firebase serialization
    constructor() : this(null, null,)
}