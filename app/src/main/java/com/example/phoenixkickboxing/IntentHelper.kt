package com.example.phoenixkickboxing

import android.content.Context
import android.content.Intent

fun ClassIntent(context: Context, activityToOpen: Class<*>)
{
    val intent = Intent(context, activityToOpen)
    context.startActivity(intent)
}