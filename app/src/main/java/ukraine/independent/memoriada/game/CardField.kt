package ukraine.independent.memoriada.game

import android.view.LayoutInflater
import android.view.View
import kotlin.random.Random

class CardField(val layoutInflater: LayoutInflater, val cardLayoutId: Int, val allImages: ArrayList<Int>) {

    fun generateCardLayouts(number: Int): ArrayList<View> {
        val cardLayoutArray = arrayListOf<View>()
        for (i in 1..number) {
            val cardLayout = layoutInflater.inflate(cardLayoutId, null)
            cardLayoutArray.add(cardLayout)
        }
        return cardLayoutArray
    }
    fun generateCardField(number: Int): ArrayList<Card> {
        val cardLayouts = generateCardLayouts(number)
        val cardArrayList = arrayListOf<Card>()
        val allImagesCopy = arrayListOf<Int>()
        allImagesCopy.addAll(allImages)
        for (i in 0 until cardLayouts.size) {
            if ( i%2 == 0) {
                val imageResourceIndex = Random.nextInt(0, allImagesCopy.size)
                val card1 = Card(allImagesCopy.get(imageResourceIndex), cardLayouts.get(i), Card.CLOSED)
                val card2 = Card(allImagesCopy.get(imageResourceIndex), cardLayouts.get(i + 1), Card.CLOSED)
                allImagesCopy.removeAt(imageResourceIndex)
                cardArrayList.add(card1)
                cardArrayList.add(card2)
            }
        }
        cardArrayList.shuffle()
        return cardArrayList
    }
}