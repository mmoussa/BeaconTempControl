package com.moussa.ubicompproject.Model.Room

import com.google.firebase.firestore.FirebaseFirestore

class FirestoreDatabase{

    val db = FirebaseFirestore.getInstance()
    val roomDao = RoomDao()

    companion object{
        @Volatile private var instance: FirestoreDatabase? = null

        fun getInstance() =
                instance ?: synchronized(this){
                    instance ?: FirestoreDatabase().also{instance = it}
                }
    }
}