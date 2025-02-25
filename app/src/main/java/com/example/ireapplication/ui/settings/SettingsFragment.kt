package com.example.ireapplication.ui.settings

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.ireapplication.BuildConfig
import com.example.ireapplication.R
import com.example.ireapplication.databinding.FragmentSettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupVersionInfo()
        setupSettingsObserver()
        setupClickListeners()
    }

    private fun setupVersionInfo() {
        binding.versionText.text = "Version ${BuildConfig.VERSION_NAME}"
    }

    private fun setupSettingsObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.settings.collect { settings ->
                    binding.exhibitNotificationsSwitch.isChecked = settings.exhibitNotificationsEnabled
                    binding.eventNotificationsSwitch.isChecked = settings.eventNotificationsEnabled
                    binding.darkModeSwitch.isChecked = settings.darkModeEnabled
                    binding.fontSizeSlider.value = settings.fontSizeScale
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.exhibitNotificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleExhibitNotifications(isChecked)
        }

        binding.eventNotificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleEventNotifications(isChecked)
        }

        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleDarkMode(isChecked)
        }

        binding.fontSizeSlider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                viewModel.updateFontSize(value)
            }
        }

        binding.languageButton.setOnClickListener {
            showLanguageSelectionDialog()
        }

        binding.privacyPolicyButton.setOnClickListener {
            openWebPage(PRIVACY_POLICY_URL)
        }

        binding.termsButton.setOnClickListener {
            openWebPage(TERMS_URL)
        }
    }

    private fun showLanguageSelectionDialog() {
        val languages = arrayOf(
            getString(R.string.language_english),
            getString(R.string.language_irish),
            getString(R.string.language_french),
            getString(R.string.language_german)
        )

        val currentLocale = resources.configuration.locales[0]
        val currentSelection = when (currentLocale.language) {
            "ga" -> 1  // Irish
            "fr" -> 2  // French
            "de" -> 3  // German
            else -> 0  // English (default)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.settings_select_language))
            .setSingleChoiceItems(languages, currentSelection) { dialog, which ->
                val locale = when (which) {
                    1 -> Locale("ga")  // Irish
                    2 -> Locale("fr")  // French
                    3 -> Locale("de")  // German
                    else -> Locale("en")  // English
                }
                updateLocale(locale)
                dialog.dismiss()
            }
            .show()
    }

    private fun updateLocale(locale: Locale) {
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        requireActivity().recreate()
    }

    private fun updateDarkMode(enabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun openWebPage(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val PRIVACY_POLICY_URL = "https://www.internationalrugbyexperience.com/sites/default/files/2023-03/IRE%20Website%20Privacy%20Policy%20-%20Holmes%20PDF.pdf"
        private const val TERMS_URL = "https://www.internationalrugbyexperience.com/sites/default/files/2023-04/IRE%20Terms%20and%20Conditions%20-%20Holmes%2010_03_2023.pdf"
    }
} 