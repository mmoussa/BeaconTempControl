package com.moussa.ubicompproject.ui.home

import androidx.lifecycle.ViewModel
import com.moussa.ubicompproject.Model.Room.RoomRepository

class RoomsViewModel (private val roomRepository: RoomRepository)
    : ViewModel(){

    fun getRooms() = roomRepository.getRooms()
}