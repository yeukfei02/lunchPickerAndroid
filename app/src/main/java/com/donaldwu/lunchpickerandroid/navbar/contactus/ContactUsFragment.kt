package com.donaldwu.lunchpickerandroid.navbar.contactus

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import com.donaldwu.lunchpickerandroid.R


class ContactUsFragment : Fragment() {

    private lateinit var contactUsViewModel: ContactUsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_contact_us, container, false)

        contactUsViewModel = ViewModelProviders.of(this).get(ContactUsViewModel::class.java)
        val textView: TextView = root.findViewById(R.id.text_contact_us)
        contactUsViewModel.text.observe(this, Observer {
            textView.text = it
        })

        return root
    }

}
