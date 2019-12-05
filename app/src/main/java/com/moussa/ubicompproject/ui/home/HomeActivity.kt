package com.moussa.ubicompproject.ui.home

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gimbal.android.*
import com.google.firebase.firestore.SetOptions
import com.moussa.ubicompproject.BuildConfig
import com.moussa.ubicompproject.Model.Room.FirestoreDatabase
import com.moussa.ubicompproject.Model.Room.Room
import com.moussa.ubicompproject.Model.Room.RoomRepository
import com.moussa.ubicompproject.R
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    internal var TAG = "HomeActivity"

    internal var RSSI_THRESHOLD = -90

    internal var gimbalEventReceiver: GimbalEventReceiver? = null
    internal var placeEventListener: PlaceEventListener? = null

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var roomList: MutableList<Room>

    private lateinit var viewModel: RoomsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        roomList = ArrayList()

        initializeUi()


    }

    private fun initializeUi() {

        //Permissions
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.INTERNET)
        requestPermissions(permissions, 0)

        //Setup toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Rooms"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Setup gimbal beacons
        Gimbal.setApiKey(application, BuildConfig.API_KEY_GIMBAL)
        setupGimbalPlaceManager()
        Gimbal.start()

        //RecyclerView setup
        viewManager = LinearLayoutManager(this)
        viewAdapter = RoomsAdapter(roomList)

        rvRooms.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        //ViewModel setup
        val factory = RoomsViewModelFactory(RoomRepository.getInstance(FirestoreDatabase.getInstance().roomDao))
        viewModel = ViewModelProviders.of(this, factory).get(RoomsViewModel::class.java)

        viewModel.getRooms().observe(this, Observer { rooms ->
            roomList.clear()
            roomList.addAll(rooms)
            rvRooms.adapter?.notifyDataSetChanged()
        })

    }

    private fun setupGimbalPlaceManager() {
        placeEventListener = object : PlaceEventListener() {
            override fun onVisitStart(visit: Visit?) {

                Log.d(TAG, "Visit Start: " + visit!!.place.name)
//                mBeaconName.setText("You're at: " + visit.place.name)
                for(room in roomList){
                    room.updateOccupied(room.roomName == visit.place.name)
                    updateDb(room)
                }

                rvRooms.adapter?.notifyDataSetChanged()

                val intent = Intent()
                intent.action = "GIMBAL_EVENT_ACTION"
                sendBroadcast(intent)
            }

            override fun onVisitEnd(visit: Visit?) {

                Log.d(TAG, "Visit End: " + visit!!.place.name)

//                mBeaconName.setText("You're are leaving: " + visit.place.name)

                for(room in roomList){
                    if(room.roomName == visit.place.name){
                        room.updateOccupied(false)
                        updateDb(room)
                    }
                }

                rvRooms.adapter?.notifyDataSetChanged()

                val intent = Intent()
                intent.action = "GIMBAL_EVENT_ACTION"
                sendBroadcast(intent)
            }

            override fun onBeaconSighting(beaconSighting: BeaconSighting?, list: List<Visit>?) {
                Log.d(TAG, "Beacon sighting: " + beaconSighting!!.beacon.name + " RSSI: " + beaconSighting.rssi!!.toString())

//                mBeaconRssi.setText(beaconSighting.rssi!!.toString())

                if (beaconSighting.rssi > RSSI_THRESHOLD) {
//                    mBeaconName.setText("You're at: " + beaconSighting.beacon.name)
                    Log.d(TAG, "You're at: " + beaconSighting.beacon.name)
                    for(room in roomList){
                        room.updateOccupied(room.beaconName == beaconSighting.beacon.name)
                        updateDb(room)
                    }
                } else {
//                    mBeaconName.setText("You're leaving: " + beaconSighting.beacon.name)
                    Log.d(TAG, "You're leaving: " + beaconSighting.beacon.name)
                    for(room in roomList){
                        if(room.beaconName == beaconSighting.beacon.name){
                            room.updateOccupied(false)
                            updateDb(room)
                        }
                    }
                }

                rvRooms.adapter?.notifyDataSetChanged()

            }
        }
        PlaceManager.getInstance().addListener(placeEventListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {
        R.id.action_settings -> {
            true
        }

        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun updateDb(room: Room){
        if(room.pendingUpdate){
            val data = hashMapOf("occupied" to room.occupied)
            FirestoreDatabase.getInstance().db.collection("Rooms")
                    .document(room.roomId)
                    .set(data, SetOptions.merge())

            room.pendingUpdate = false
            Log.d(TAG, "Updating room: " + room.roomName)
        }

    }

    internal inner class GimbalEventReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != null) {
                if (intent.action!!.compareTo("GIMBAL_EVENT_ACTION") == 0) {
                    Log.d(TAG, "An event!!")
                }
            }
        }
    }
}
