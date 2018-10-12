package polonium.com.kotlinexample.codeOverview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.vision.barcode.Barcode
import polonium.com.kotlinexample.MainActivity
import polonium.com.kotlinexample.databinding.FragmentCodeOverviwBinding
import polonium.com.kotlinexample.databinding.FragmentSmsviewBinding

class CodeOverviewFragment : Fragment() {
    private var barcode: Barcode? = null
    private lateinit var binding: FragmentCodeOverviwBinding
    private lateinit var viewModel: CodeOverviewViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            barcode = it.getParcelable(ARG_BARCODE)
        }
        if (barcode == null) throw RuntimeException("Barcode not found")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProviders.of(this, CodeOverviewViewModel.Factory(barcode!!)).get(CodeOverviewViewModel::class.java)
        (activity as MainActivity).appComponent.inject(this.viewModel)
        binding = FragmentCodeOverviwBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        if (viewModel.barcodeValueType == CodeOverviewViewModel.BarcodeValueType.SMS)
        FragmentSmsviewBinding.inflate(inflater,binding.parsedContent,true).viewModel = viewModel
        return binding.root
    }

    companion object {

        const val ARG_BARCODE = "barcode"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param barcode barcode to show overview.
         * @return A new instance of fragment CodeOverviewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(barcode: Barcode) =
                CodeOverviewFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_BARCODE, barcode)
                    }
                }
    }
}
