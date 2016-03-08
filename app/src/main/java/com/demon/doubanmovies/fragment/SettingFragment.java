package com.demon.doubanmovies.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.demon.doubanmovies.R;
import com.demon.doubanmovies.activity.MainActivity;
import com.demon.doubanmovies.utils.PrefsUtil;

import de.psdev.licensesdialog.LicensesDialog;

public class SettingFragment extends PreferenceFragmentCompat {
    private static final String OTHER = "other";
    private static final String DAY_NIGHT = "day_night";
    private static final String NICKNAME = "nickname";
    private static final String SIGNATURE = "signature";

    Preference.OnPreferenceChangeListener listener = (preference, newValue) -> {

        String key = preference.getKey();
        switch (key) {
            case DAY_NIGHT:
                Intent intent = new Intent(MainActivity.ACTION_LOCAL_SEND);
                PrefsUtil.switchDayNightMode((String) newValue);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                break;
            case NICKNAME:
            case SIGNATURE:
                preference.setSummary((String) newValue);
                break;
            default:
                break;
        }

        return true;
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.settings_general);

        ListPreference listPreference = (ListPreference) findPreference(DAY_NIGHT);
        listPreference.setOnPreferenceChangeListener(listener);

        EditTextPreference namePref = (EditTextPreference) findPreference(NICKNAME);
        namePref.setSummary(PrefsUtil.getPrefNickname(getActivity()));
        namePref.setOnPreferenceChangeListener(listener);

        EditTextPreference signPref = (EditTextPreference) findPreference(SIGNATURE);
        signPref.setSummary(PrefsUtil.getPrefSignature(getActivity()));
        signPref.setOnPreferenceChangeListener(listener);
    }


    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        String key = preference.getKey();

        if (key != null && key.equals(OTHER)) {
            showApacheLicenseDialog();
            return false;
        }

        return super.onPreferenceTreeClick(preference);
    }

    private void showApacheLicenseDialog() {

        new LicensesDialog.
                Builder(getActivity()).
                setNotices(R.raw.notices).
                setIncludeOwnLicense(true).
                build().
                showAppCompat();
    }
}
