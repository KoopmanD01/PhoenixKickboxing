package com.example.phoenixkickboxing

object ValidatorClass {
    fun validateLoginInput(username: String, password: String): Boolean {

        return!(username.isEmpty() || password.isEmpty())
    }
}