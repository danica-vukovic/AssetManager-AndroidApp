package com.example.assetmanager.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.assetmanager.R;

import java.util.Locale;


public class SettingsFragment extends Fragment {

    private static final String PREF_LANGUAGE = "language_pref";
    private static final String LANG_ENGLISH = "en";
    private static final String LANG_SERBIAN = "sr";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RadioGroup rgLanguages = view.findViewById(R.id.rgLanguages);
        RadioButton rbEnglish = view.findViewById(R.id.rbEnglish);
        RadioButton rbSerbian = view.findViewById(R.id.rbSerbian);

        // Set initial state
        String currentLanguage = getCurrentLanguage();
        if (LANG_ENGLISH.equals(currentLanguage)) {
            rbEnglish.setChecked(true);
        } else if (LANG_SERBIAN.equals(currentLanguage)) {
            rbSerbian.setChecked(true);
        }

        rgLanguages.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbEnglish) {
                setLanguage(LANG_ENGLISH);
            } else if (checkedId == R.id.rbSerbian) {
                setLanguage(LANG_SERBIAN);
            }
        });
    }

    private String getCurrentLanguage() {
        SharedPreferences preferences = requireContext().getSharedPreferences(PREF_LANGUAGE, Context.MODE_PRIVATE);
        return preferences.getString(PREF_LANGUAGE, Locale.getDefault().getLanguage());
    }

    private void setLanguage(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);
        requireContext().getResources().updateConfiguration(config, requireContext().getResources().getDisplayMetrics());

        SharedPreferences preferences = requireContext().getSharedPreferences(PREF_LANGUAGE, Context.MODE_PRIVATE);
        preferences.edit().putString(PREF_LANGUAGE, languageCode).apply();

        requireActivity().recreate();
    }
}

