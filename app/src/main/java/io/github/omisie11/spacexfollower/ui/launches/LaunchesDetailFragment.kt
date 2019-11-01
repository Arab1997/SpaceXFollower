package io.github.omisie11.spacexfollower.ui.launches

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import io.github.omisie11.spacexfollower.R
import io.github.omisie11.spacexfollower.data.model.launch.Launch
import io.github.omisie11.spacexfollower.util.getLocalTimeFromUnix
import io.github.omisie11.spacexfollower.util.toggleVisibility
import kotlinx.android.synthetic.main.fragment_launches_detail.*
import kotlinx.android.synthetic.main.launch_cores.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LaunchesDetailFragment : Fragment() {

    private lateinit var payloadsRecyclerViewAdapter: PayloadsRecyclerAdapter
    private val viewModel by sharedViewModel<LaunchesViewModel>()
    // Variable used in animating expand/collapse icon
    private var coresIconRotationAngle = 0f
    private var payloadsIconRotationAngle = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_launches_detail, container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val safeArgs = arguments?.let { LaunchesDetailFragmentArgs.fromBundle(it) }
        val selectedLaunchId: Int = safeArgs?.itemId ?: 0

        payloadsRecyclerViewAdapter = PayloadsRecyclerAdapter()
        payloads_recycler.apply {
            addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = payloadsRecyclerViewAdapter
        }

        viewModel.getAllLaunches()
            .observe(viewLifecycleOwner, Observer<List<Launch>> { launches ->
                if (launches != null) {
                    val launch = launches[selectedLaunchId]

                    text_flight_number.text = launch.flightNumber.toString()
                    text_mission_name.text = launch.missionName
                    text_launch_date.text = if (launch.launchDateUnix != null)
                        getLocalTimeFromUnix(launch.launchDateUnix) else
                        getString(R.string.launch_date_null)
                    text_launch_site_name.text = launch.launch_site.siteName

                    // Dynamically add views for Cores used in flight
                    if (launch.rocket.first_stage.cores == null) {
                        val noCoresTextView = TextView(activity).apply {
                            text = context.getString(R.string.no_cores_used_in_launch)
                        }
                        frame_cores_list.addView(noCoresTextView)
                    } else {
                        for (core in launch.rocket.first_stage.cores) {
                            if (core.core_serial.isNullOrEmpty() || core.block == null ||
                                core.flight == null
                            ) {
                                val noDataTextView = TextView(activity).apply {
                                    text = context.getString(R.string.no_precision_info)
                                }
                                frame_cores_list.addView(noDataTextView)
                            } else {
                                val coreLinearLayout = layoutInflater.inflate(
                                    R.layout.launch_cores,
                                    frame_cores_list, false
                                )
                                frame_cores_list.addView(coreLinearLayout)
                                coreLinearLayout.text_lib_name.text = core.core_serial
                                coreLinearLayout.text_core_block.text =
                                    resources.getString(R.string.block_number_template, core.block)
                                coreLinearLayout.text_core_flight.text =
                                    resources.getString(
                                        R.string.flight_number_template,
                                        core.flight
                                    )
                            }
                        }
                    }
                    payloadsRecyclerViewAdapter.setData(launch.rocket.second_stage.payloads)
                }
            })

        val expandCardTransition = AutoTransition().apply { duration = 200 }
        card_cores_list.setOnClickListener {
            TransitionManager.beginDelayedTransition(card_cores_list, expandCardTransition)

            frame_cores_list.toggleVisibility()
            coresIconRotationAngle = if (coresIconRotationAngle == 0f) 180f else 0f
            image_cores_expand_arrow.animate().rotation(coresIconRotationAngle).setDuration(250)
                .start()
        }

        card_payloads.setOnClickListener {
            TransitionManager.beginDelayedTransition(card_payloads, expandCardTransition)

            frame_payloads.toggleVisibility()
            payloadsIconRotationAngle = if (payloadsIconRotationAngle == 0f) 180f else 0f
            image_payloads_expand_arrow.animate().rotation(payloadsIconRotationAngle)
                .setDuration(250).start()
        }
    }
}