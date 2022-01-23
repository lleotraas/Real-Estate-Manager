package com.openclassrooms.realestatemanager.utils

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.openclassrooms.realestatemanager.R
import java.lang.IllegalStateException

class CategoryDialogBox : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            builder.setView(inflater.inflate(R.layout.category_dialog_box, null))
                .setPositiveButton(R.string.category_dialog_box_positive_btn,
                DialogInterface.OnClickListener { dialog, id ->
                    val fragment = activity!!.supportFragmentManager.findFragmentByTag("Fragment_add_image")
                })
                .setNegativeButton(R.string.category_dialog_box_negative_btn,
                DialogInterface.OnClickListener{ dialog, id ->

                })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}