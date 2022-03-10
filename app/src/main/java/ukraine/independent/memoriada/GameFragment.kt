package ukraine.independent.memoriada

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.flexbox.FlexboxLayout
import ukraine.independent.memoriada.databinding.FragmentGameBinding
import ukraine.independent.memoriada.game.Card
import ukraine.independent.memoriada.game.CardField
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.*

class GameFragment : Fragment() {

    companion object {
        const val LEVEL_KEY = "level_key"
        const val YOUR_TOTAL_STEPS_KEY = "your_total_steps_key"
        const val RESULTS_TEXT_KEY = "results_text_key"
    }

    val allImages = arrayListOf<Int>()

    lateinit var binding: FragmentGameBinding

    lateinit var sp: SharedPreferences

    var yourStepsCount = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        allImages.add(R.drawable.card_ic_1_tryzub_ua)
        allImages.add(R.drawable.card_ic_2_heart_ua_colors_5_11)
        allImages.add(R.drawable.card_ic_3_flag_ua)
        allImages.add(R.drawable.card_ic_4_cherries)
        allImages.add(R.drawable.card_ic_5_ua_eu_family)
        allImages.add(R.drawable.card_ic_6_chestnut_kyiv)
        allImages.add(R.drawable.card_ic_7_sunflower)
        allImages.add(R.drawable.card_ic_8_sofia_square)
        allImages.add(R.drawable.card_ic_9_kyiv_founders)
        allImages.add(R.drawable.card_ic_10_freedom_monument)
        allImages.add(R.drawable.card_ic_11_fc_dynamo_kyiv_logo)
        allImages.add(R.drawable.card_ic_12_odesa)
        allImages.add(R.drawable.card_ic_13_symbol_of_charkiv)
        allImages.add(R.drawable.card_ic_14_mariupol)
        allImages.add(R.drawable.card_ic_15_chmelnyckyj)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_game, container, false
        )

        sp = activity!!.getPreferences(Context.MODE_PRIVATE)
        val level = sp.getInt(LEVEL_KEY, 1)


        val cardsNumber = level * 2 + 2
        var rows = getNumberOfRowsAndColumns(cardsNumber)
        var columns = rows

        binding.gridLayoutGuessCells.rowCount = rows
        binding.gridLayoutGuessCells.columnCount = columns

        refreshCardFieldAndGridLayoutWhenLevelUpdate(binding.gridLayoutGuessCells, inflater)

        return binding.root
    }

    fun getCardsNumber(level: Int) = level * 2 + 2

    fun updateYourStepsCount() {
        binding.yourStepsCount.text = yourStepsCount.toString()
    }

    fun setUpOnClickListenerOnCards(
        gridLayout: GridLayout,
        cards: ArrayList<Card>,
        level: Int
    ) {
        for (card in cards) {
            gridLayout.addView(card.cardLayout)
            card.cardLayout.setOnClickListener { view ->
                yourStepsCount += 1; updateYourStepsCount()
                if (card.status == Card.CLOSED) {
                    if (isThereOpenedCard(cards)) {
                        val openedCard = findOpenedCard(cards)
                        if (card.equals(openedCard)) {
                            openCard(card)
                            card.status = Card.GUESSED
                            openedCard.status = Card.GUESSED
                            if (isAllCardsGuessed(cards)) {
                                if ((level + 1) < allImages.size) {
                                    sp.edit().putInt(LEVEL_KEY, level + 1).apply()
                                    refreshTotalSteps()
                                    refreshCardFieldAndGridLayoutWhenLevelUpdate(
                                        gridLayout,
                                        LayoutInflater.from(activity)
                                    )
                                } else {
                                    sp.edit().remove(LEVEL_KEY).apply()
                                    refreshTotalStepsSaveResultsAndRemoveTotalStepsFromPrefs()
                                    view.findNavController().navigate(R.id.action_gameFragment_to_endFragment)
                                }
                            }
                        } else {
                            openedCard?.let { closeCard(openedCard) }
                        }
                    } else {
                        openCard(card)
                    }
                }
            }

        }
    }

    fun refreshTotalStepsSaveResultsAndRemoveTotalStepsFromPrefs() {
        refreshTotalSteps()
        saveResults()
        removeTotalStepsFromPrefs()
    }
    fun saveResults() {
        var results: String = sp.getString(RESULTS_TEXT_KEY, "")!!
        val numberOfResults = calculateNumberRowsInString(results)
        val totalYourSteps = sp.getInt(YOUR_TOTAL_STEPS_KEY, 0)
        val resultLine = "${numberOfResults+1}. Min steps: ${calculateTotalMinSteps()} | Your steps: ${totalYourSteps}\n"
        results = resultLine + results
        sp.edit().remove(RESULTS_TEXT_KEY).apply()
        sp.edit().putString(RESULTS_TEXT_KEY, results).apply()
        removeTotalStepsFromPrefs()
    }
    fun removeTotalStepsFromPrefs() {
        sp.edit().remove(YOUR_TOTAL_STEPS_KEY).apply()
    }
    fun calculateTotalMinSteps(): Int {
        var totalMinSteps = 0
        for (i in 2..allImages.size) {
            totalMinSteps += i
        }
        return totalMinSteps*2
    }
    fun calculateNumberRowsInString(input: String): Int {
        val m: Matcher = Pattern.compile("\r\n|\r|\n").matcher(input)
        var lines = 0
        while (m.find()) {
            lines++
        }
        return lines
    }

    fun refreshTotalSteps() {
        val totalSteps = sp.getInt(YOUR_TOTAL_STEPS_KEY, 0) + yourStepsCount
        sp.edit().putInt(YOUR_TOTAL_STEPS_KEY, totalSteps).apply()
    }

    fun isAllCardsGuessed(cards: ArrayList<Card>): Boolean {
        for (card in cards) {
            if (card.status != Card.GUESSED)
                return false
        }
        return true
    }

    fun refreshCardFieldAndGridLayoutWhenLevelUpdate(
        gridLayout: GridLayout,
        inflater: LayoutInflater
    ) {

        gridLayout.removeAllViews()
        val sharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE)
        val level = sharedPreferences!!.getInt(LEVEL_KEY, 1)

        if (binding.levelSymbolsContainer.childCount != 0) {
            addImageAsLevelCount()
        } else {
            if (level > 1) {
                for (i in 2..level) {
                    addImageAsLevelCount()
                }
            }
        }

        val cardsNumber = getCardsNumber(level)
        gridLayout.rowCount = getNumberOfRowsAndColumns(cardsNumber)
        gridLayout.columnCount = getNumberOfRowsAndColumns(cardsNumber)
        val cards = CardField(
            inflater,
            R.layout.guess_card_layout,
            allImages
        ).generateCardField(cardsNumber)
        binding.minStepsCount.text = cardsNumber.toString()
        resetYourStepsCount(); binding.yourStepsCount.text = yourStepsCount.toString()

        setUpOnClickListenerOnCards(gridLayout, cards, level)
    }

    fun resetYourStepsCount() {
        yourStepsCount = 0
    }

    fun addImageAsLevelCount() {
        val imageView = ImageView(context)
        imageView.setImageResource(R.drawable.ic_heart_ua_colors)
        val imParams = FlexboxLayout.LayoutParams(90, 90)
        imParams.setMargins(8, 16, 8, 16)
        imageView.layoutParams = imParams
        binding.levelSymbolsContainer.addView(imageView)
    }

    fun getNumberOfRowsAndColumns(cardsNumber: Int): Int {
        var rowsColumns = sqrt(cardsNumber.toDouble()).toInt()
        if (rowsColumns * rowsColumns != cardsNumber) {
            rowsColumns += 1
        }
        return rowsColumns
    }

    fun isThereOpenedCard(cards: ArrayList<Card>): Boolean {
        for (card in cards) {
            if (card.status == Card.OPEN)
                return true
        }
        return false
    }

    fun findOpenedCard(cards: ArrayList<Card>): Card? {
        for (card in cards) {
            if (card.status == Card.OPEN)
                return card
        }
        return null
    }

    fun openCard(card: Card) {
        val cardImageView = card.cardLayout.findViewById<ImageView>(R.id.guess_card_image_view)
        cardImageView.setImageResource(card.imageResource)
        card.status = Card.OPEN
    }

    fun closeCard(card: Card) {
        val cardImageView = card.cardLayout.findViewById<ImageView>(R.id.guess_card_image_view)
        cardImageView.setImageResource(R.drawable.card_ic_question_mark)
        card.status = Card.CLOSED
    }
}