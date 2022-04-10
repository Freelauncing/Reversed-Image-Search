package com.reversedimagesearched.allsavedresults

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.reversedimagesearched.R
import com.reversedimagesearched.data.database.DatabaseModel
import com.reversedimagesearched.data.database.ReverseDbHelper
import com.reversedimagesearched.databinding.FragmentAllResultsBinding
import com.reversedimagesearched.databinding.FragmentReverseImageResultBinding
import com.reversedimagesearched.reverseimageresult.ReverseImageResultViewModel
import com.reversedimagesearched.reverseimageresult.ReverseImageResultViewModelFactory
import com.reversedimagesearched.reverseimageresult.ReverseImagesRecyclerViewAdapter
import com.reversedimagesearched.util.setupSnackbar

class AllResultsFragment : Fragment() {

    private var allResultsAdapter: AllResultsAdapter? = null

    private lateinit var viewDataBinding: FragmentAllResultsBinding


    private val viewModel by viewModels<AllResultsViewModel>{
        AllResultsViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_all_results, container, false)

        viewDataBinding = FragmentAllResultsBinding.bind(view).apply {
            this.viewmodel = viewModel
        }

        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        return viewDataBinding.root

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_results, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            allResultsAdapter = AllResultsAdapter(ArrayList(), viewModel, requireContext())
            viewDataBinding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
            viewDataBinding.recyclerView.adapter = allResultsAdapter
            (allResultsAdapter as AllResultsAdapter).notifyDataSetChanged()

            setUpObservers()

            setupSnackbar()
        }catch (e:Exception){

        }

    }

    private fun setUpObservers() {

        viewModel.updateList.observe(viewLifecycleOwner, Observer {
            if(viewModel.reverseImageLists.value!!.size>0){
                Log.v("Hello","List")
                allResultsAdapter!!.swapList(viewModel.reverseImageLists.value!!)
            }else{
                allResultsAdapter!!.swapList(ArrayList())
            }
        })
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
    }
}