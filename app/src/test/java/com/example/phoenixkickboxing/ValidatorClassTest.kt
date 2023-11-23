package com.example.phoenixkickboxing



import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ValidatorClassTest{

    @Test
    fun whenInputIsValidLogin() {
        assertThat(ValidatorClass.validateLoginInput("username", "password"))
    }

    @Test
    fun whenInputIsValidBooking() {
        assertThat(ValidatorClass.validateBookingInput("2023/11/23", "user"))
    }

}