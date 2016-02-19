package com.demon.doubanmovies.fragment;

import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.demon.doubanmovies.R;

import de.psdev.licensesdialog.LicensesDialog;

/**
 * Created by user on 2016/2/18.
 */
public class SettingFragment extends PreferenceFragmentCompat {
    private static final String TAG = "SettingFragment";
    private static final String OTHER = "other";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        //Set night-mode or other UI changes
        //view.setBackground(
        //        new ColorDrawable(getContext().getResources().getColor(R.color.ios_internal_bg)));
        return view;
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.settings_general);
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
