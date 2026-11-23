package com.example.mindcheck.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mindcheck.R

// Step 3: placeholder Check-in screen. Step 5 adds the questions.
class CheckInFragment : Fragment(R.layout.fragment_checkin) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Temporary: jump straight to the Result screen to test navigation
        view.findViewById<Button>(R.id.finish_button).setOnClickListener {
            findNavController().navigate(R.id.action_checkin_to_result)
        }
    }
}
