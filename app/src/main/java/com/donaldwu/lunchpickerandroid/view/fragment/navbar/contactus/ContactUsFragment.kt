package com.donaldwu.lunchpickerandroid.view.fragment.navbar.contactus

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import com.donaldwu.lunchpickerandroid.R
import com.google.android.material.snackbar.Snackbar
import com.stripe.android.ApiResultCallback
import com.stripe.android.PaymentConfiguration
import com.stripe.android.Stripe
import com.stripe.android.model.Token
import com.stripe.android.view.CardInputWidget
import org.json.JSONObject
import com.donaldwu.lunchpickerandroid.model.Model
import com.skydoves.elasticviews.ElasticButton
import java.util.*
import kotlin.collections.ArrayList

class ContactUsFragment : Fragment() {

    private val currencyList: ArrayList<String> = arrayListOf(
        "Hong Kong Dollar (HKD)",
        "Singapore Dollar (SGD)",
        "British Dollar Pound (GBP)",
        "Chinese Renminbi Yuan (CNY)",
        "US Dollar (USD)"
    )
    private val STRIPE_TEST_API_KEY = "pk_test_NZ0RpkNsrP6yvxIEr95z6ATO00evlvKpSP"
    private val STRIPE_API_KEY = "pk_live_YbsoEmSzoPtTAuI0dB7bSR6k002TH9P1kD"

    private var amountNum = 0.0
    private var currency = ""
    private var token = ""
    private var cardObj = JSONObject()

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

        hideCardInputWidgetPostalCode(root)

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
        val donateButton: ElasticButton = root.findViewById(R.id.donate_button)
        donateButton.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("https://donorbox.org/donate-for-lunch-picker-better-features-and-development")
            startActivity(i)
        }
    }

    private fun handleAmountChange(root: View) {
        val amount: EditText = root.findViewById(R.id.amount)
        amount.addTextChangedListener {
            amountNum = it.toString().toDouble()
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

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()

                val amount: EditText = root.findViewById(R.id.amount)
                when (selectedItem) {
                    "Hong Kong Dollar (HKD)" -> {
                        amount.setText("3", TextView.BufferType.EDITABLE)
                        currency = "hkd"
                    }
                    "Singapore Dollar (SGD)" -> {
                        amount.setText("1", TextView.BufferType.EDITABLE)
                        currency = "sgd"
                    }
                    "British Dollar Pound (GBP)" -> {
                        amount.setText("1", TextView.BufferType.EDITABLE)
                        currency = "gbp"
                    }
                    "Chinese Renminbi Yuan (CNY)" -> {
                        amount.setText("3", TextView.BufferType.EDITABLE)
                        currency = "cny"
                    }
                    "US Dollar (USD)" -> {
                        amount.setText("1", TextView.BufferType.EDITABLE)
                        currency = "usd"
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun hideCardInputWidgetPostalCode(root: View) {
        val cardInputWidget: CardInputWidget = root.findViewById(R.id.card_input_widget)
        cardInputWidget.postalCodeEnabled = false
    }

    private fun handlePayNowButton(root: View) {
        val payNowButton: ElasticButton = root.findViewById(R.id.pay_now_button)
        payNowButton.setOnClickListener {
            val cardInputWidget: CardInputWidget = root.findViewById(R.id.card_input_widget)
            val card = cardInputWidget.card
            if (card != null) {
                if (card.validateCard()) {
                    PaymentConfiguration.init(root.context, STRIPE_API_KEY)
                    val stripe = Stripe(root.context, PaymentConfiguration.getInstance(root.context).publishableKey)

                    val idempotencyKey = UUID.randomUUID().toString()
                    stripe.createCardToken(card, idempotencyKey, object: ApiResultCallback<Token> {
                        override fun onSuccess(result: Token) {
                            token = result.id
                            val cardMap = result.card!!.toParamMap()
                            val cardResult = cardMap["card"].toString()
                            cardObj = JSONObject(cardResult)
                        }

                        override fun onError(e: Exception) {
                            Log.i("logger", "error = ${e.message}")
                            Snackbar.make(root, e.message.toString(), Snackbar.LENGTH_SHORT).show()
                        }
                    })

                    if (amountNum != 0.0 && currency.isNotEmpty() && token.isNotEmpty()) {
                        val response = Model.creditCardPayment(amountNum, currency, token, cardObj)
                        Log.i("logger", "response = ${response}")
                    }
                } else {
                    Snackbar.make(root, "Card is not a valid card", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }
}
