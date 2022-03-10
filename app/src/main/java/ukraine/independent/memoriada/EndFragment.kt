package ukraine.independent.memoriada

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SharedMemory
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import ukraine.independent.memoriada.databinding.FragmentEndBinding

class EndFragment : Fragment() {

    lateinit var binding: FragmentEndBinding

    lateinit var sp: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_end, container, false)

        binding.startAgainButton.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_endFragment_to_startFragment)
        }

        sp = activity!!.getPreferences(Context.MODE_PRIVATE)

        setUpResultsIntoResultsContainer()

        return binding.root
    }

    fun setUpResultsIntoResultsContainer() {
        val resultsText = sp.getString(GameFragment.RESULTS_TEXT_KEY, "error")
        binding.actualResults.text = resultsText
    }
}