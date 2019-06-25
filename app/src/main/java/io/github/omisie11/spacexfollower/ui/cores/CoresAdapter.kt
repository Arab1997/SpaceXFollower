package io.github.omisie11.spacexfollower.ui.cores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.model.Core
import io.github.omisie11.spacexfollower.util.getLocalTimeFromUnix
import kotlinx.android.synthetic.main.cores_recycler_item.view.*

class CoresAdapter : RecyclerView.Adapter<CoresAdapter.ViewHolder>() {

    private var coresList: List<Core> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cores_recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Bind data to views from ViewHolder
        coresList.let {
            holder.coreBlockTextView.text = if (coresList[position].block != null)
                holder.itemView.context.resources.getString(R.string.core_block, coresList[position].block)
            else holder.itemView.context.resources.getString(R.string.core_block_null)
            holder.coreSerialTextView.text = coresList[position].coreSerial
            holder.coreLaunchTextView.text = if (coresList[position].originalLaunchUnix != null)
                getLocalTimeFromUnix(coresList[position].originalLaunchUnix!!) else
                holder.itemView.context.getString(R.string.launch_date_null)
            holder.coreStatusTextView.text = coresList[position].status
        }
    }

    override fun getItemCount(): Int = coresList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val coreBlockTextView: TextView = itemView.text_core_block
        val coreSerialTextView: TextView = itemView.text_core_serial
        val coreLaunchTextView: TextView = itemView.text_core_launch
        val coreStatusTextView: TextView = itemView.text_core_status
    }

    fun setData(data: List<Core>) {
        coresList = data
        notifyDataSetChanged()
    }
}