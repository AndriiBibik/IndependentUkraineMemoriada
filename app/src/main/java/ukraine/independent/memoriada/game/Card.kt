package ukraine.independent.memoriada.game

import android.view.View

class Card(val imageResource: Int, val cardLayout: View, var status: Int) {

    companion object {
        const val CLOSED = 0
        const val OPEN = 1
        const val GUESSED = 2
    }

    override fun equals(other: Any?) =
        (other is Card)
                && imageResource == other.imageResource

}