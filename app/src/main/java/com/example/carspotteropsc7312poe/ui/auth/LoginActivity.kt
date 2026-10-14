package com.example.carspotteropsc7312poe.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.carspotteropsc7312poe.R
import com.example.carspotteropsc7312poe.data.auth.AccountManager
import com.example.carspotteropsc7312poe.ui.dashboard.DashboardActivity
import com.example.carspotteropsc7312poe.util.InputValidator
import com.example.carspotteropsc7312poe.util.toast
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var tvRedirectSignUp: TextView
    private lateinit var etEmail: EditText
    private lateinit var etPass: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnBiometric: Button
    private lateinit var accounts: AccountManager

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        tvRedirectSignUp = findViewById(R.id.tvRedirectSignUp)
        btnLogin = findViewById(R.id.btnLogin)
        btnBiometric = findViewById(R.id.btnBiometric)
        etEmail = findViewById(R.id.etEmailAddress)
        etPass = findViewById(R.id.etPassword)

        accounts = AccountManager(this)

        setupBiometric()

        btnLogin.setOnClickListener { login() }
        btnBiometric.setOnClickListener { showBiometricPrompt() }

        tvRedirectSignUp.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
            finish()
        }
    }

    private fun setupBiometric() {
        val canAuthenticate = BiometricManager.from(this)
            .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        btnBiometric.isEnabled = canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS

        when (canAuthenticate) {
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> toast(R.string.err_biometric_no_hardware)
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> toast(R.string.err_biometric_none_enrolled)
        }

        biometricPrompt = BiometricPrompt(
            this,
            ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    signInLastUser()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    toast(R.string.biometric_error, errString)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    toast(R.string.biometric_failed)
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_title))
            .setSubtitle(getString(R.string.biometric_subtitle))
            .setNegativeButtonText(getString(R.string.biometric_use_password))
            .build()
    }

    private fun showBiometricPrompt() {
        // Biometrics sign the device owner back in to the account last used on this phone.
        // If nobody has signed in here yet there is nothing to unlock.
        if (!accounts.hasLastUser()) {
            toast(R.string.sign_in_first)
            return
        }
        biometricPrompt.authenticate(promptInfo)
    }

    private fun signInLastUser() {
        lifecycleScope.launch {
            if (accounts.signInLastUser()) proceedToDashboard() else toast(R.string.sign_in_first)
        }
    }

    private fun proceedToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    private fun login() {
        val email = etEmail.text.toString().trim()
        val pass = etPass.text.toString()

        if (email.isBlank() || pass.isBlank()) {
            toast(R.string.err_blank_credentials)
            return
        }
        if (!InputValidator.isValidEmail(email)) {
            toast(R.string.err_invalid_email)
            return
        }

        btnLogin.isEnabled = false
        lifecycleScope.launch {
            val result = accounts.signIn(email, pass)
            btnLogin.isEnabled = true
            when (result) {
                is AccountManager.Result.Success -> {
                    toast(R.string.sign_in_success)
                    proceedToDashboard()
                }
                else -> toast(R.string.err_wrong_credentials)
            }
        }
    }
}
