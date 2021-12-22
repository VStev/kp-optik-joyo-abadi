package com.kp.optikjoyoabadi

import java.util.concurrent.Executors

const val NOTIFICATION_CHANNEL_NAME = "OJA_User"
const val NOTIFICATION_CHANNEL_ID = "notify-status"
const val NOTIFICATION_ID = 42
const val ID_REPEATING = 101

private val SINGLE_EXECUTOR = Executors.newSingleThreadExecutor()

fun executeThread(f: () -> Unit) {
    SINGLE_EXECUTOR.execute(f)
}