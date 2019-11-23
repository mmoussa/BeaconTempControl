package com.moussa.ubicompproject.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.moussa.ubicompproject.Model.Room.RoomRepository

class RoomsViewModelFactory (private val roomRepository: RoomRepository)
    :ViewModelProvider.NewInstanceFactory(){

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RoomsViewModel(roomRepository) as T
    }
}