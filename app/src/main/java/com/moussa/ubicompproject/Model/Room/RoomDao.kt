package com.moussa.ubicompproject.Model.Room

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore

class RoomDao{

    val tag = "RoomDao"
    private val roomList = mutableListOf<Room>()
    private val rooms = MutableLiveData<List<Room>>()

    init{
        rooms.value = roomList
    }

    fun getRooms(): LiveData<List<Room>>{

        val db = FirestoreDatabase.getInstance().db

        db.collection("Rooms")
                .addSnapshotListener{value, e ->
                    if(e != null){
                        Log.w(tag, "Failed to listen: ", e)
                        return@addSnapshotListener
                    }

                    roomList.clear()
                    for(doc in value!!){
                        var room: Room = doc.toObject(Room::class.java)
                        roomList.add(room)
                        rooms.value = roomList

                    }
                }

        return rooms
    }
}