package com.moussa.ubicompproject.Model.Room

class RoomRepository private constructor(private val roomDao: RoomDao){
    fun getRooms() = roomDao.getRooms()

    companion object{
        @Volatile private var instance: RoomRepository? = null

        fun getInstance(roomDao: RoomDao) =
                instance ?: synchronized(this){
                    instance
                            ?: RoomRepository(roomDao).also{instance = it}
                }
    }
}