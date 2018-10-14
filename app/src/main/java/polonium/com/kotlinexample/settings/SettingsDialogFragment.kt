package polonium.com.kotlinexample.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import polonium.com.kotlinexample.MainActivity
import polonium.com.kotlinexample.R
import polonium.com.kotlinexample.databinding.FragmentCodeOverviwBinding
import polonium.com.kotlinexample.databinding.FragmentSettingsDialogBinding


class SettingsDialogFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var binding: FragmentSettingsDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SettingsViewModel()::class.java)
        (activity as MainActivity).appComponent.inject(this.viewModel)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentSettingsDialogBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        return binding.root
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

}
