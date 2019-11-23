package com.moussa.ubicompproject.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moussa.ubicompproject.Model.Room.Room
import com.moussa.ubicompproject.R
import kotlinx.android.synthetic.main.item_room.view.*

class RoomsAdapter(private val myDataset: List<Room>):
        RecyclerView.Adapter<RoomsAdapter.MyViewHolder>(){

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var txtRoomName = itemView.txtRoomName
        var viewAvailability = itemView.viewAvailability
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_room, parent, false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var room: Room = myDataset[position]

        holder.txtRoomName.setText(room.roomName)
        if(room.occupied)
            holder.viewAvailability.setBackgroundResource(R.color.occupied)
        else
            holder.viewAvailability.setBackgroundResource(R.color.empty)
    }

    override fun getItemCount() = myDataset.size

}