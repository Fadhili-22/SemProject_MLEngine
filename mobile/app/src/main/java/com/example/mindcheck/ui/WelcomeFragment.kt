package com.example.mindcheck.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.mindcheck.R

// The Welcome screen as a Fragment (Lecture 6).
// Fragment(R.layout.fragment_welcome) tells Android which XML layout to show.
class WelcomeFragment : Fragment(R.layout.fragment_welcome) {

    // Shared with the Check-in and Result screens (one ViewModel for the whole Activity)
    private val viewModel: ScreeningViewModel by activityViewModels()

    // onViewCreated runs once the layout is on screen, so findViewById works here
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // The ids of the actions come from nav_graph.xml
        view.findViewById<Button>(R.id.start_button).setOnClickListener {
            viewModel.reset() // start every check-in with no answers
            findNavController().navigate(R.id.action_welcome_to_checkin)
        }
        view.findViewById<Button>(R.id.help_button).setOnClickListener {
            findNavController().navigate(R.id.action_welcome_to_help)
        }
    }
}
