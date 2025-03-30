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
import com.example.ireapplication.IREApplication
import com.example.ireapplication.R
import com.example.ireapplication.databinding.FragmentSettingsBinding
import com.example.ireapplication.utils.TextScaleUtils
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
        setupFontSizeSlider()
        setupSettingsObserver()
        setupClickListeners()
    }

    private fun setupVersionInfo() {
        binding.versionText.text = "Version ${BuildConfig.VERSION_NAME}"
    }

    private fun setupFontSizeSlider() {
        binding.fontSizeSlider.apply {
            valueFrom = TextScaleUtils.MIN_SCALE
            valueTo = TextScaleUtils.MAX_SCALE
            stepSize = TextScaleUtils.STEP_SIZE
            value = TextScaleUtils.quantizeScale(viewModel.settings.value.fontSizeScale)
        }
    }

    private fun setupSettingsObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.settings.collect { settings ->
                    binding.exhibitNotificationsSwitch.isChecked = settings.exhibitNotificationsEnabled
                    binding.eventNotificationsSwitch.isChecked = settings.eventNotificationsEnabled
                    binding.darkModeSwitch.isChecked = settings.highContrastEnabled
                    
                    val currentValue = binding.fontSizeSlider.value
                    val newValue = TextScaleUtils.quantizeScale(settings.fontSizeScale)
                    if (currentValue != newValue) {
                        binding.fontSizeSlider.value = newValue
                    }
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
            viewModel.toggleHighContrast(isChecked)
            updateContrastMode(isChecked)
        }

        // Only store the font size value when slider changes, don't apply it
        binding.fontSizeSlider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                val quantizedValue = TextScaleUtils.quantizeScale(value.coerceIn(TextScaleUtils.MIN_SCALE, TextScaleUtils.MAX_SCALE))
                if (quantizedValue != value) {
                    binding.fontSizeSlider.value = quantizedValue
                }
                // Only store the selected value in ViewModel, don't apply it yet
                viewModel.updateSelectedFontSize(quantizedValue)
            }
        }
        
        // Apply the font size only when the Apply button is clicked
        binding.applyFontSizeButton.setOnClickListener {
            val fontScale = binding.fontSizeSlider.value
            val quantizedValue = TextScaleUtils.quantizeScale(fontScale)
            
            // Update settings and apply font scale to the app
            viewModel.updateFontSize(quantizedValue)
            
            // Apply font scaling to current activity
            activity?.let { activity ->
                // Apply the font scale
                TextScaleUtils.applyFontScale(activity, quantizedValue)
                
                // Log the current font scale for debugging
                android.util.Log.d("SettingsFragment", "Applied font scale: $quantizedValue, " +
                    "fontScale in config: ${resources.configuration.fontScale}")
                
                // Force update the UI by recreating the activity
                android.util.Log.d("SettingsFragment", "Recreating activity to apply font scale")
                activity.recreate()
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

        // Social Media Links
        binding.homeWebsiteButton.setOnClickListener {
            openWebPage(WEBSITE_URL)
        }

        binding.instagramButton.setOnClickListener {
            openAppOrWebPage(INSTAGRAM_APP_URL, INSTAGRAM_WEB_URL)
        }

        binding.facebookButton.setOnClickListener {
            openAppOrWebPage(FACEBOOK_APP_URL, FACEBOOK_WEB_URL)
        }

        // Review Links
        binding.googleMapsButton.setOnClickListener {
            openAppOrWebPage(MAPS_APP_URL, MAPS_WEB_URL)
        }

        binding.tripadvisorButton.setOnClickListener {
            openWebPage(TRIPADVISOR_URL)
        }
    }

    private fun showLanguageSelectionDialog() {
        val languages = arrayOf(
            getString(R.string.language_english),
            getString(R.string.language_irish),
            getString(R.string.language_french),
            getString(R.string.language_german)
        )

        val languageCodes = arrayOf("en", "ga", "fr", "de")
        val currentSettings = viewModel.settings.value
        val currentSelection = languageCodes.indexOf(currentSettings.language)
        
        android.util.Log.d("LanguageDialog", "Current language: ${currentSettings.language}")
        android.util.Log.d("LanguageDialog", "Current selection index: $currentSelection")
        android.util.Log.d("LanguageDialog", "Available languages: ${languageCodes.joinToString()}")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.settings_select_language))
            .setSingleChoiceItems(languages, currentSelection) { dialog, which ->
                val language = languageCodes[which]
                android.util.Log.d("LanguageDialog", "Selected language: $language at position: $which")
                viewModel.updateLanguage(language)
                dialog.dismiss()
            }
            .show()
    }

    private fun updateContrastMode(enabled: Boolean) {
        // Check if the high contrast mode has actually changed
        if (IREApplication.isHighContrastEnabled() == enabled) {
            return // No change, don't recreate the activity
        }
        
        // Apply system dark mode
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
        
        // Update the application-wide high contrast setting
        IREApplication.updateHighContrast(enabled)
        
        // Apply high contrast theme if enabled
        if (enabled) {
            activity?.setTheme(R.style.Theme_IREApplication_HighContrast)
        } else {
            activity?.setTheme(R.style.Theme_IREApplication)
        }
        
        // Re-create the activity to apply the theme change
        activity?.recreate()
    }

    private fun openAppOrWebPage(appUrl: String, webUrl: String) {
        try {
            // For Facebook, try the app first
            if (appUrl.startsWith("fb://")) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_APP_URL))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    return
                } catch (e: Exception) {
                    // If app fails, fall back to web
                    openWebPage(FACEBOOK_WEB_URL)
                    return
                }
            }
            
            // For other apps
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(appUrl))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } catch (e: Exception) {
            // If app is not installed, open in web browser
            openWebPage(webUrl)
        }
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
        
        // Social Media URLs
        private const val WEBSITE_URL = "https://www.internationalrugbyexperience.com"
        private const val INSTAGRAM_APP_URL = "instagram://user?username=internationalrugbyexperience"
        private const val INSTAGRAM_WEB_URL = "https://www.instagram.com/internationalrugbyexperience"
        private const val FACEBOOK_APP_URL = "fb://page/509668662840016"
        private const val FACEBOOK_WEB_URL = "https://www.facebook.com/internationalrugbyexperience"
        
        // Review URLs
        private const val TRIPADVISOR_URL = "https://www.tripadvisor.ie/Attraction_Review-g186621-d26245268-Reviews-International_Rugby_Experience_Limerick-Limerick_County_Limerick.html"
        
        // Directions URLs
        private const val MAPS_APP_URL = "google.navigation:q=International+Rugby+Experience"
        private const val MAPS_WEB_URL = "https://maps.google.com/?q=International+Rugby+Experience"
    }
} 