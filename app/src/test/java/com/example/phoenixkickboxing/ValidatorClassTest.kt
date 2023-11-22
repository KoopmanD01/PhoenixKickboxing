package com.example.phoenixkickboxing



import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ValidatorClassTest{


    @Test
    fun whenInputIsValidLogin(user:String, pass:String){
        val result = ValidatorClass.validateLoginInput(user,pass);
        assertThat(result).isEqualTo(true)
    }

}