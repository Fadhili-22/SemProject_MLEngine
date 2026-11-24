package com.example.mindcheck.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.mindcheck.R
import com.example.mindcheck.data.Question
import com.google.android.material.card.MaterialCardView
import com.google.android.material.color.MaterialColors
import com.google.android.material.progressindicator.LinearProgressIndicator

// Step 5: shows one question at a time with its answer choices as cards.
class CheckInFragment : Fragment(R.layout.fragment_checkin) {

    private val viewModel: ScreeningViewModel by activityViewModels()

    private lateinit var progress: LinearProgressIndicator
    private lateinit var counterText: TextView
    private lateinit var sectionText: TextView
    private lateinit var promptText: TextView
    private lateinit var questionText: TextView
    private lateinit var optionsContainer: LinearLayout
    private lateinit var backButton: Button
    private lateinit var nextButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progress = view.findViewById(R.id.progress)
        counterText = view.findViewById(R.id.counter_text)
        sectionText = view.findViewById(R.id.section_text)
        promptText = view.findViewById(R.id.prompt_text)
        questionText = view.findViewById(R.id.question_text)
        optionsContainer = view.findViewById(R.id.options_container)
        backButton = view.findViewById(R.id.back_button)
        nextButton = view.findViewById(R.id.next_button)

        backButton.setOnClickListener { goBack() }
        nextButton.setOnClickListener { goNext() }

        // The phone's back button goes to the previous question instead of leaving the check-in
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { goBack() }

        progress.max = viewModel.questions.size
        showQuestion()
    }

    private fun showQuestion() {
        val index = viewModel.currentIndex
        val question = viewModel.questions[index]

        progress.setProgressCompat(index + 1, true)
        counterText.text = getString(R.string.question_counter, index + 1, viewModel.questions.size)
        sectionText.text = question.section
        promptText.text = question.prompt
        questionText.text = question.text

        showOptions(question)

        nextButton.isEnabled = viewModel.answerFor(question.id) != null
        nextButton.setText(if (viewModel.isLastQuestion()) R.string.see_result else R.string.next)
    }

    // Builds one card (layout/item_option.xml) per answer choice
    private fun showOptions(question: Question) {
        optionsContainer.removeAllViews()
        val cards = mutableListOf<MaterialCardView>()

        for (option in question.options) {
            val card = layoutInflater.inflate(R.layout.item_option, optionsContainer, false) as MaterialCardView
            card.findViewById<TextView>(R.id.option_label).text = option.label
            card.setOnClickListener {
                viewModel.recordAnswer(question.id, option.value)
                // highlight only the card that was tapped
                for ((i, c) in cards.withIndex()) {
                    styleOption(c, selected = question.options[i].value == option.value)
                }
                nextButton.isEnabled = true
            }
            cards.add(card)
            optionsContainer.addView(card)
        }

        // Show the saved answer when coming back to a question
        val saved = viewModel.answerFor(question.id)
        for ((i, card) in cards.withIndex()) {
            styleOption(card, selected = question.options[i].value == saved)
        }
    }

    // Selected card: purple border + light purple background. Others: thin grey border on white.
    private fun styleOption(card: MaterialCardView, selected: Boolean) {
        card.findViewById<RadioButton>(R.id.option_radio).isChecked = selected
        val border = if (selected) androidx.appcompat.R.attr.colorPrimary
        else com.google.android.material.R.attr.colorOutlineVariant
        val fill = if (selected) com.google.android.material.R.attr.colorPrimaryContainer
        else com.google.android.material.R.attr.colorSurface
        card.strokeColor = MaterialColors.getColor(card, border)
        card.setCardBackgroundColor(MaterialColors.getColor(card, fill))
        card.strokeWidth = dp(if (selected) 2 else 1)
    }

    private fun goNext() {
        if (!viewModel.isLastQuestion()) {
            viewModel.currentIndex++
            showQuestion()
            return
        }
        // Last question: make sure everything is answered, then run the ML model
        val missing = viewModel.firstUnansweredIndex()
        if (missing != -1) {
            Toast.makeText(requireContext(), R.string.answer_all_questions, Toast.LENGTH_SHORT).show()
            viewModel.currentIndex = missing
            showQuestion()
            return
        }
        try {
            viewModel.finish()
            findNavController().navigate(R.id.action_checkin_to_result)
        } catch (e: IllegalArgumentException) {
            // Safety net: the model rejects missing or out-of-range answers
            Toast.makeText(requireContext(), R.string.answer_all_questions, Toast.LENGTH_SHORT).show()
        }
    }

    private fun goBack() {
        if (viewModel.currentIndex > 0) {
            viewModel.currentIndex--
            showQuestion()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
