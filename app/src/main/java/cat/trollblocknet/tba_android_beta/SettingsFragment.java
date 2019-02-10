package cat.trollblocknet.tba_android_beta;


import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        p.getBoolean("developerMode", false);

//this effectively resets the myPrefKey to its default value
        //p.edit().remove("developerMode").apply();
    }
}
