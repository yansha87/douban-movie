package com.demon.doubanmovies.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.demon.doubanmovies.R;
import com.demon.doubanmovies.activity.MainActivity;
import com.demon.doubanmovies.utils.Constant;
import com.demon.doubanmovies.utils.PrefsUtil;

import de.psdev.licensesdialog.LicensesDialog;
import rx.Observable;

public class SettingFragment extends PreferenceFragmentCompat {
    private static final String OTHER = "other";
    private static final String IMAGE_SIZE = "image_size";
    private static final String DAY_NIGHT = "day_night";
    private static final String NICKNAME = "nickname";
    private static final String SIGNATURE = "signature";

    /**
     * Preference change listener
     */
    private final Preference.OnPreferenceChangeListener listener = (preference, newValue) -> {

        String key = preference.getKey();
        switch (key) {
            case DAY_NIGHT:
                Intent intent = new Intent(MainActivity.ACTION_LOCAL_SEND);
                PrefsUtil.switchDayNightMode((String) newValue);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

                Observable.just(newValue)
                        .map(Constant.dayNightSummary::get)
                        .subscribe(preference::setSummary);
                break;
            case IMAGE_SIZE:
                Observable.just(newValue)
                        .map(Constant.imageSizeSummary::get)
                        .subscribe(preference::setSummary);
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
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.settings_general);

        // initial day and night mode
        ListPreference dayNightPref = (ListPreference) findPreference(DAY_NIGHT);
        Observable.just(PrefsUtil.getPrefDayNightMode(getActivity()))
                .map(Constant.dayNightSummary::get)
                .subscribe(dayNightPref::setSummary);
        dayNightPref.setOnPreferenceChangeListener(listener);

        // initial image size
        ListPreference imageSizePref = (ListPreference) findPreference(IMAGE_SIZE);
        imageSizePref.setSummary(PrefsUtil.getPrefImageSize(getActivity()));
        Observable.just(PrefsUtil.getPrefImageSize(getActivity()))
                .map(Constant.imageSizeSummary::get)
                .subscribe(imageSizePref::setSummary);
        imageSizePref.setOnPreferenceChangeListener(listener);

        // initial nickname and summary
        EditTextPreference namePref = (EditTextPreference) findPreference(NICKNAME);
        namePref.setSummary(PrefsUtil.getPrefNickname(getActivity()));
        namePref.setOnPreferenceChangeListener(listener);

        // initial signature and summary
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

    /**
     * show licence dialog
     */
    private void showApacheLicenseDialog() {
        new LicensesDialog.
                Builder(getActivity()).
                setNotices(R.raw.notices).
                setIncludeOwnLicense(true).
                build().
                showAppCompat();
    }
}
