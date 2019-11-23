package com.moussa.ubicompproject.Model.Room

data class Room(val roomName: String = "",
                var currentTemp: Float = 0.0f,
                var desiredTemp: Float = 0.0f,
                var occupied: Boolean = false){

}