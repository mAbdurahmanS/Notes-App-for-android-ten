package com.example.notes.ui.update

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notes.R
import com.example.notes.databinding.FragmentUpdateBinding
import com.example.notes.model.Priority
import com.example.notes.model.ToDoData
import com.example.notes.ui.SharedViewModel
import com.example.notes.viewmodel.ToDoViewModel
import kotlinx.android.synthetic.main.fragment_update.*
import kotlinx.android.synthetic.main.fragment_update.view.*
import java.nio.file.Files.delete

class UpdateFragment : Fragment() {

    private val args: UpdateFragmentArgs by navArgs()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private val mToDoViewModel: ToDoViewModel by viewModels()
    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //data binding
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
        binding.args = args
        binding.spinnerPrioritiesCurrent.onItemSelectedListener = mSharedViewModel.listener
        setHasOptionsMenu(true)

        return binding?.root

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.menu_save -> updateItem()
            R.id.menu_delete -> confirmDeleteItem()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun confirmDeleteItem() {
        AlertDialog.Builder(requireContext())
                .setTitle("Delete '${args.currentitem.title}'?")
                .setMessage("Are you sure want to remove '${args.currentitem.title}'?")
                .setPositiveButton("Yes"){_, _ ->
                    mToDoViewModel.deleteItem(args.currentitem)
                    Toast.makeText(requireContext(), "Succesfully Removed: ${args.currentitem.title}", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_updateFragment_to_listFragment)
                }
                .setNegativeButton("No", null)
                .create()
                .show()
    }

    private fun updateItem() {

        val title = edt_title_current.text.toString()
        val description = edt_description_current.text.toString()
        val getPriority = spinner_priorities_current.selectedItem.toString()

        val validation = mSharedViewModel.verifyDataFromUser(title,description)
        if (validation){
            // update current item
            val updateItem = ToDoData(
                    args.currentitem.id, title,
                    mSharedViewModel.parsePriority(getPriority), description
            )
            mToDoViewModel.updateData(updateItem)
            Toast.makeText(requireContext(), "Succesfully Updated!", Toast.LENGTH_SHORT).show()
            // navigate back
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }else{
            Toast.makeText(requireContext(), "Please fill out all fields!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}