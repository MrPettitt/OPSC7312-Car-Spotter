package com.example.carspotteropsc7312poe.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.carspotteropsc7312poe.R
import com.example.carspotteropsc7312poe.data.auth.AccountManager
import com.example.carspotteropsc7312poe.ui.dashboard.DashboardActivity
import com.example.carspotteropsc7312poe.util.InputValidator
import com.example.carspotteropsc7312poe.util.toast
import kotlinx.coroutines.launch

class RegistrationActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPass: EditText
    private lateinit var etConfPass: EditText
    private lateinit var btnSignUp: Button
    private lateinit var tvRedirectLogin: TextView
    private lateinit var accounts: AccountManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        etEmail = findViewById(R.id.etSEmailAddress)
        etPass = findViewById(R.id.etSPassword)
        etConfPass = findViewById(R.id.etSConfPassword)
        btnSignUp = findViewById(R.id.btnSSigned)
        tvRedirectLogin = findViewById(R.id.tvRedirectLogin)

        accounts = AccountManager(this)

        btnSignUp.setOnClickListener { signUpUser() }

        tvRedirectLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun signUpUser() {
        val email = etEmail.text.toString().trim()
        val pass = etPass.text.toString()
        val confirmPassword = etConfPass.text.toString()

        if (email.isBlank() || pass.isBlank() || confirmPassword.isBlank()) {
            toast(R.string.err_blank_credentials)
            return
        }
        if (!InputValidator.isValidEmail(email)) {
            toast(R.string.err_invalid_email)
            return
        }
        if (!InputValidator.isValidPassword(pass)) {
            toast(R.string.err_password_short, InputValidator.MIN_PASSWORD_LENGTH)
            return
        }
        if (pass != confirmPassword) {
            toast(R.string.err_password_mismatch)
            return
        }

        btnSignUp.isEnabled = false
        lifecycleScope.launch {
            val result = accounts.register(email, pass)
            btnSignUp.isEnabled = true
            when (result) {
                is AccountManager.Result.Success -> {
                    toast(R.string.sign_up_success)
                    startActivity(Intent(this@RegistrationActivity, DashboardActivity::class.java))
                    finish()
                }
                AccountManager.Result.EmailTaken -> toast(R.string.err_email_taken)
                AccountManager.Result.InvalidCredentials -> toast(R.string.err_wrong_credentials)
            }
        }
    }
}
