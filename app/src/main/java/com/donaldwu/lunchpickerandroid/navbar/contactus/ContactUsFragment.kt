package com.donaldwu.lunchpickerandroid.navbar.contactus

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import com.donaldwu.lunchpickerandroid.R

class ContactUsFragment : Fragment() {

    private val currencyList: ArrayList<String> = arrayListOf(
        "Hong Kong Dollar (HKD)",
        "Singapore Dollar (SGD)",
        "British Dollar Pound (GBP)",
        "Chinese Renminbi Yuan (CNY)",
        "US Dollar (USD)"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_contact_us, container, false)

        handleEmailIconClick(root)

        handleGithubIconClick(root)

        handleDonorboxRadioButton(root)

        handleStripeRadioButton(root)

        handleDonateButton(root)

        handleAmountChange(root)

        handleCurrencyChange(root)

        handlePayNowButton(root)

        return root
    }

    private fun handleEmailIconClick(root: View) {
        val emailIcon: ImageView = root.findViewById(R.id.email_icon)
        emailIcon.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("mailto:yeukfei02@gmail.com")
            startActivity(i)
        }
    }

    private fun handleGithubIconClick(root: View) {
        val githubIcon: ImageView = root.findViewById(R.id.github_icon)
        githubIcon.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("https://github.com/yeukfei02")
            startActivity(i)
        }
    }

    private fun handleDonorboxRadioButton(root: View) {
        val donorboxRadioButton: RadioButton = root.findViewById(R.id.donorbox_radio_button)
        donorboxRadioButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val donorboxLinearLayout: LinearLayout = root.findViewById(R.id.donorbox_linearLayout)
                donorboxLinearLayout.visibility = View.VISIBLE

                val stripeLinearLayout: LinearLayout = root.findViewById(R.id.stripe_linearLayout)
                stripeLinearLayout.visibility = View.GONE
            }
        }
    }

    private fun handleStripeRadioButton(root: View) {
        val stripeRadioButton: RadioButton = root.findViewById(R.id.stripe_radio_button)
        stripeRadioButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val donorboxLinearLayout: LinearLayout = root.findViewById(R.id.donorbox_linearLayout)
                donorboxLinearLayout.visibility = View.GONE

                val stripeLinearLayout: LinearLayout = root.findViewById(R.id.stripe_linearLayout)
                stripeLinearLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun handleDonateButton(root: View) {
        val donateButton: Button = root.findViewById(R.id.donate_button)
        donateButton.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("https://donorbox.org/donate-for-lunch-picker-better-features-and-development")
            startActivity(i)
        }
    }

    private fun handleAmountChange(root: View) {
        val amount: EditText = root.findViewById(R.id.amount)
        amount.addTextChangedListener {

        }
    }

    private fun handleCurrencyChange(root: View) {
        val spinner: Spinner = root.findViewById(R.id.currency_dropdown)
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            root.context,
            android.R.layout.simple_spinner_dropdown_item,
            currencyList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()

                val amount: EditText = root.findViewById(R.id.amount)
                if (selectedItem == "Hong Kong Dollar (HKD)" || selectedItem == "Chinese Renminbi Yuan (CNY)") {
                    amount.setText("3", TextView.BufferType.EDITABLE)
                } else {
                    amount.setText("1", TextView.BufferType.EDITABLE)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun handlePayNowButton(root: View) {
        val payNowButton: Button = root.findViewById(R.id.pay_now_button)
        payNowButton.setOnClickListener {

        }
    }
}
