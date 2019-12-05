package com.moussa.ubicompproject.Model.Room

data class Room(var roomId: String = "",
                val roomName: String = "",
                var currentTemp: Float = 0.0f,
                var desiredTemp: Float = 0.0f,
                var occupied: Boolean = false,
                var beaconName: String = ""){

    var pendingUpdate = false
    fun updateOccupied(occ: Boolean){
        if(occupied != occ){
            occupied = occ
            pendingUpdate = true
        }
    }

}