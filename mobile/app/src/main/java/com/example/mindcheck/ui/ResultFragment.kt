package com.example.mindcheck.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.mindcheck.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator

// Step 7: shows the ML model's result, a score per area and, if needed, crisis support.
class ResultFragment : Fragment(R.layout.fragment_result) {

    private val viewModel: ScreeningViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val result = viewModel.result
        if (result == null) {
            // Nothing to show (for example the app was restarted): go back to the start
            findNavController().popBackStack(R.id.welcomeFragment, false)
            return
        }

        // Safety question answered above "Not at all": show the support card under the result
        if (result.needsUrgentSupport) {
            view.findViewById<MaterialCardView>(R.id.crisis_card).visibility = View.VISIBLE
            view.findViewById<Button>(R.id.crisis_call_button).setOnClickListener { dial("1199") }
            view.findViewById<Button>(R.id.crisis_emergency_button).setOnClickListener { dial("999") }
        }

        // The level decides its own text, icon and colours (see RiskLevel)
        val level = result.riskLevel
        val context = requireContext()
        val textColor = ContextCompat.getColor(context, level.onContainerColorRes)
        view.findViewById<MaterialCardView>(R.id.result_card)
            .setCardBackgroundColor(ContextCompat.getColor(context, level.containerColorRes))
        view.findViewById<TextView>(R.id.result_emoji).text = level.emoji
        view.findViewById<TextView>(R.id.result_label).setTextColor(textColor)
        view.findViewById<TextView>(R.id.result_title).apply {
            setText(level.titleRes)
            setTextColor(textColor)
        }
        view.findViewById<TextView>(R.id.result_message).apply {
            setText(level.messageRes)
            setTextColor(textColor)
        }

        showSectionScores(view.findViewById(R.id.section_scores))

        view.findViewById<Button>(R.id.help_button).setOnClickListener {
            findNavController().navigate(R.id.action_result_to_help)
        }
        view.findViewById<Button>(R.id.restart_button).setOnClickListener {
            viewModel.reset()
            findNavController().navigate(R.id.action_result_to_welcome)
        }
    }

    private fun showSectionScores(container: LinearLayout) {
        container.removeAllViews()
        for (score in viewModel.sectionScores()) {
            val row = layoutInflater.inflate(R.layout.item_section_score, container, false)
            row.findViewById<TextView>(R.id.section_name).text = score.section
            row.findViewById<TextView>(R.id.section_value).text =
                getString(R.string.score_out_of, score.score, score.maxScore)
            row.findViewById<LinearProgressIndicator>(R.id.section_bar).apply {
                max = score.maxScore
                setProgressCompat(score.score, false)
            }
            container.addView(row)
        }
    }

    // Opens the phone's dialer with the number filled in (an implicit Intent, Lecture 6)
    private fun dial(number: String) {
        startActivity(Intent(Intent.ACTION_DIAL, "tel:$number".toUri()))
    }
}
