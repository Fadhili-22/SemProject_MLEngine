package com.example.mindcheck.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.mindcheck.R
import com.example.mindcheck.data.HelpResource
import com.example.mindcheck.data.HelpResources

/**
 * Step 8 (Lecture 7): list of support options with a detail pane.
 * Tablets show the list and the detail side by side.
 * Phones show the list, and the detail slides over it when an option is tapped.
 */
class HelpFragment : Fragment(R.layout.fragment_help) {

    private var selectedIndex = 0

    private lateinit var detailEmoji: TextView
    private lateinit var detailName: TextView
    private lateinit var detailHours: TextView
    private lateinit var detailDescription: TextView
    private lateinit var callButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectedIndex = savedInstanceState?.getInt(KEY_SELECTED) ?: 0

        val slidingPane = view.findViewById<SlidingPaneLayout>(R.id.sliding_pane)
        val list = view.findViewById<LinearLayout>(R.id.resource_list)
        detailEmoji = view.findViewById(R.id.detail_emoji)
        detailName = view.findViewById(R.id.detail_name)
        detailHours = view.findViewById(R.id.detail_hours)
        detailDescription = view.findViewById(R.id.detail_description)
        callButton = view.findViewById(R.id.call_button)

        // Only open the detail by tapping, not by swiping
        slidingPane.lockMode = SlidingPaneLayout.LOCK_MODE_LOCKED

        // On phones, the back button closes the detail and shows the list again
        val backCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                slidingPane.closePane()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backCallback)
        slidingPane.addPanelSlideListener(object : SlidingPaneLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View, slideOffset: Float) {}
            override fun onPanelOpened(panel: View) {
                backCallback.isEnabled = slidingPane.isSlideable
            }
            override fun onPanelClosed(panel: View) {
                backCallback.isEnabled = false
            }
        })
        slidingPane.doOnLayout {
            backCallback.isEnabled = slidingPane.isSlideable && slidingPane.isOpen
        }

        // One card (layout/item_help_resource.xml) per support option
        HelpResources.all.forEachIndexed { index, resource ->
            val item = layoutInflater.inflate(R.layout.item_help_resource, list, false)
            item.findViewById<TextView>(R.id.resource_emoji).text = resource.emoji
            item.findViewById<TextView>(R.id.resource_name).text = resource.name
            item.findViewById<TextView>(R.id.resource_summary).text = resource.summary
            item.setOnClickListener {
                selectedIndex = index
                showDetail(resource)
                slidingPane.openPane()
            }
            list.addView(item)
        }

        showDetail(HelpResources.all[selectedIndex])
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_SELECTED, selectedIndex)
    }

    private fun showDetail(resource: HelpResource) {
        detailEmoji.text = resource.emoji
        detailName.text = resource.name
        detailHours.text = resource.hours
        detailDescription.text = resource.description

        val phone = resource.phone
        if (phone == null) {
            callButton.visibility = View.GONE
        } else {
            callButton.visibility = View.VISIBLE
            callButton.text = getString(R.string.call_number, phone)
            // Opens the phone's dialer with the number filled in (an implicit Intent)
            callButton.setOnClickListener {
                startActivity(Intent(Intent.ACTION_DIAL, "tel:$phone".toUri()))
            }
        }
    }

    private companion object {
        const val KEY_SELECTED = "selected_resource"
    }
}
