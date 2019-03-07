package io.github.omisie11.spacexfollower.ui.capsules

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.model.Capsule
import kotlinx.android.synthetic.main.capsules_recycler_item.view.*


class CapsulesAdapter : RecyclerView.Adapter<CapsulesAdapter.ViewHolder>() {

    private var mExpandedPosition: Int = -1
    private var mCapsulesData: List<Capsule> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.capsules_recycler_item, parent, false)
        return ViewHolder(view).listen { position, type ->
            val item = mCapsulesData[position]
            Log.d("CapsulesAdapter", "Item clicked id: ${item.capsuleId}")
            // ToDo: handle item clicks
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Bind data to views from ViewHolder
        mCapsulesData.let {
            holder.capsuleIdTextView.text = mCapsulesData[position].capsuleId
            holder.capsuleSerialTextView.text = mCapsulesData[position].capsuleSerial
            holder.capsuleType.text = mCapsulesData[position].type
            holder.capsuleStatus.text = mCapsulesData[position].status
        }
    }

    override fun getItemCount(): Int = mCapsulesData.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val capsuleIdTextView: TextView = itemView.text_capsule_id
        val capsuleSerialTextView: TextView = itemView.text_capsule_serial
        val capsuleType: TextView = itemView.text_capsule_type
        val capsuleStatus: TextView = itemView.text_capsule_status
        //val capsuleLandings: TextView = itemView.text_landings
        //val capsuleMissionsTextView: TextView = itemView.text_missions
        //val groupExpanded: Group = itemView.group_expand
    }

    fun setData(data: List<Capsule>) {
        mCapsulesData = data
        notifyDataSetChanged()
    }
}

// ViewHolder extension for item click listener
fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
    itemView.setOnClickListener {
        event.invoke(adapterPosition, itemViewType)
    }
    return this
}