package com.reversedimagesearched.reverseimageresult

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.reversedimagesearched.R
import com.reversedimagesearched.data.model.CommonResponse
import com.reversedimagesearched.databinding.FragmentHomeBinding
import com.reversedimagesearched.databinding.FragmentReverseImageResultBinding
import com.reversedimagesearched.home.AddProductViewModelFactory
import com.reversedimagesearched.home.HomeViewModel
import com.reversedimagesearched.util.setupSnackbar


class ReverseImageResultFragment : Fragment() {

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

               reverseImagesRecyclerViewAdapter = ReverseImagesRecyclerViewAdapter(ArrayList(),requireContext())
        viewDataBinding.productsList.layoutManager = GridLayoutManager(requireContext(),3)
        viewDataBinding.productsList.adapter = reverseImagesRecyclerViewAdapter
        (reverseImagesRecyclerViewAdapter as ReverseImagesRecyclerViewAdapter).notifyDataSetChanged()

        setUpSpinner()

        setUpObservers()

        setupSnackbar()
    }

    private fun setUpObservers() {
        viewModel.updateList.observe(viewLifecycleOwner, Observer {
            if(viewModel.reverseImageLists.value!!.size>0){
                Log.v("Hello","List")
                viewDataBinding.progressBar.visibility = View.GONE
                reverseImagesRecyclerViewAdapter!!.swapList(viewModel.reverseImageLists.value!!)
            }else{
                reverseImagesRecyclerViewAdapter!!.swapList(ArrayList())
            }
        })

        viewModel.showLoading.observe(viewLifecycleOwner, Observer {
            if(it){
                viewDataBinding.progressBar.visibility = View.VISIBLE
            }else{
                viewDataBinding.progressBar.visibility = View.GONE
            }
        })
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
    }

    private fun setUpSpinner() {

        var adapter1 = ArrayAdapter(requireContext(), R.layout.item_spinner, reverseOnlineResourceList)

        requireActivity().findViewById<Spinner>(R.id.textView34).adapter = adapter1

        requireActivity().findViewById<Spinner>(R.id.textView34).onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                selectedResource = reverseOnlineResourceList[position]
                if(selectedResource!="None"){
                    viewModel.selectedMode.value = selectedResource
                }
                Toast.makeText(requireContext(), "" + reverseOnlineResourceList[position], Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

    }

}
