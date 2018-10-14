package polonium.com.kotlinexample.codeOverview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.vision.barcode.Barcode
import polonium.com.kotlinexample.MainActivity
import polonium.com.kotlinexample.R
import polonium.com.kotlinexample.databinding.FragmentCodeOverviwBinding
import polonium.com.kotlinexample.databinding.FragmentSmsviewBinding
import polonium.com.kotlinexample.codeOverview.CodeOverviewViewModel.BarcodeValueType.*

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
        viewModel = ViewModelProviders.of(this, CodeOverviewViewModel.Factory(barcode!!)).get(CodeOverviewViewModel::class.java)
        (activity as MainActivity).appComponent.inject(this.viewModel)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentCodeOverviwBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        //Adding specific view for some bar/qr code content types
        when (viewModel.barcodeValueType){
            SMS ->FragmentSmsviewBinding.inflate(inflater,binding.parsedContent,true).viewModel = viewModel
            ERR -> Toast.makeText(context, R.string.err,Toast.LENGTH_LONG).show()
            CONTACT_INFO -> TODO()
            EMAIL -> TODO()
            ISBN -> TODO()
            PHONE -> TODO()
            PRODUCT -> TODO()
            TEXT -> TODO()
            URL -> TODO()
            WIFI -> TODO()
            GEO -> TODO()
            CALENDAR_EVENT -> TODO()
            DRIVER_LICENSE -> TODO()
        }
        return binding.root
    }

    companion object {

        const val ARG_BARCODE = "barcode"

        @JvmStatic
        fun newInstance(barcode: Barcode) =
                CodeOverviewFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_BARCODE, barcode)
                    }
                }
    }
}
