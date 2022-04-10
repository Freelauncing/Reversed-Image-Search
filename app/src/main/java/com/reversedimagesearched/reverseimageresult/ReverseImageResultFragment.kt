package com.reversedimagesearched.reverseimageresult

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.reversedimagesearched.R
import com.reversedimagesearched.databinding.FragmentReverseImageResultBinding
import com.reversedimagesearched.util.setupSnackbar


class ReverseImageResultFragment : Fragment(), AdapterView.OnItemSelectedListener  {

    private var reverseOnlineResourceList: MutableList<String> = mutableListOf<String>()

    private lateinit var viewDataBinding: FragmentReverseImageResultBinding

    private val viewModel by viewModels<ReverseImageResultViewModel>{
        ReverseImageResultViewModelFactory()
    }

    private var reverseImagesRecyclerViewAdapter: ReverseImagesRecyclerViewAdapter? = null

    private var selectedResource = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reverse_image_result, container, false)

        viewDataBinding = FragmentReverseImageResultBinding.bind(view).apply {
            this.viewmodel = viewModel
        }

        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        return viewDataBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reverseOnlineResourceList.add("None")
        reverseOnlineResourceList.add("Google")
        reverseOnlineResourceList.add("Bing")
        reverseOnlineResourceList.add("Tineye")

               reverseImagesRecyclerViewAdapter = ReverseImagesRecyclerViewAdapter(ArrayList(),viewModel,requireContext())
        viewDataBinding.productsList.layoutManager = GridLayoutManager(requireContext(),3)
        viewDataBinding.productsList.adapter = reverseImagesRecyclerViewAdapter
        (reverseImagesRecyclerViewAdapter as ReverseImagesRecyclerViewAdapter).notifyDataSetChanged()

        setUpObservers()

        setupSnackbar()

        viewDataBinding.textView34.onItemSelectedListener = this
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.server_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            viewDataBinding.textView34.adapter = adapter
        }
    }

    private fun setUpObservers() {
        viewModel.updateList.observe(viewLifecycleOwner, Observer {
            if(viewModel.reverseImageLists.value!!.size>0){
                Log.v("Hello","List")
                viewDataBinding.progressBar?.visibility = View.GONE
                reverseImagesRecyclerViewAdapter!!.swapList(viewModel.reverseImageLists.value!!)
            }else{
                reverseImagesRecyclerViewAdapter!!.swapList(ArrayList())
            }
        })

        viewModel.showLoading.observe(viewLifecycleOwner, Observer {
            if(it){
                viewDataBinding.progressBar?.visibility = View.VISIBLE
            }else{
                viewDataBinding.progressBar?.visibility = View.GONE
            }
        })
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
    }


    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        selectedResource = reverseOnlineResourceList[pos]
        if(selectedResource!="None"){
            viewModel.selectedMode.value = selectedResource
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }

}
