package com.moussa.ubicompproject.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moussa.ubicompproject.Model.Room.FirestoreDatabase
import com.moussa.ubicompproject.Model.Room.Room
import com.moussa.ubicompproject.Model.Room.RoomRepository
import com.moussa.ubicompproject.R
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var roomList: MutableList<Room>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        roomList = ArrayList()

        initializeUi()


    }

    private fun initializeUi() {
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
        val viewModel = ViewModelProviders.of(this, factory).get(RoomsViewModel::class.java)

        viewModel.getRooms().observe(this, Observer { rooms ->
            roomList.clear()
            roomList.addAll(rooms)
            rvRooms.adapter?.notifyDataSetChanged()
        })

    }
}
