package com.example.phoenixkickboxing

object ValidatorClass {
    fun validateLoginInput(username: String, password: String): Boolean {

        return!(username.isEmpty() || password.isEmpty())
    }
    fun validateBookingInput(date: String, user: String): Boolean {
        // Check if date and user are not empty
        if (date.isEmpty() || user.isEmpty()) {
            return false
        }

        // Define the regex pattern for the "yyyy/mm/dd" format
        val datePattern = Regex("""^\d{4}/\d{2}/\d{2}$""")

        // Check if the date matches the expected format
        if (!date.matches(datePattern)) {
            return false
        }

        return true
    }

}