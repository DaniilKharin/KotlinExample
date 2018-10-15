package polonium.com.kotlinexample.history

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import polonium.com.kotlinexample.MainActivity

import polonium.com.kotlinexample.R
import polonium.com.kotlinexample.databinding.FragmentCodeOverviwBinding
import polonium.com.kotlinexample.databinding.HistoryFragmentBinding

class HistoryFragment : Fragment() {

    companion object {
        fun newInstance() = HistoryFragment()
    }

    private lateinit var viewModel: HistoryViewModel
    private lateinit var binding: HistoryFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = HistoryFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HistoryViewModel::class.java)
        (activity as MainActivity).appComponent.inject(viewModel)
        binding.viewModel = viewModel
    }

}
