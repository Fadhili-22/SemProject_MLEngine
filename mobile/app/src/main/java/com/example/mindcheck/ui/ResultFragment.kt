package com.example.mindcheck.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mindcheck.R

// Step 3: placeholder Result screen. Step 7 shows the model's result.
class ResultFragment : Fragment(R.layout.fragment_result) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.help_button).setOnClickListener {
            findNavController().navigate(R.id.action_result_to_help)
        }
        view.findViewById<Button>(R.id.restart_button).setOnClickListener {
            findNavController().navigate(R.id.action_result_to_welcome)
        }
    }
}
