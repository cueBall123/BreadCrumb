
package breadcrumb.cue.myapplication;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import com.gimbal.android.CommunicationManager;
import com.gimbal.android.Gimbal;
import com.gimbal.android.PlaceManager;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        Preference placeMonitoringPreference = findPreference(GimbalDAO.PLACE_MONITORING_PREFERENCE);
        placeMonitoringPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue == Boolean.TRUE) {
                    CommunicationManager.getInstance().startReceivingCommunications();
                    PlaceManager.getInstance().startMonitoring();
                }
                else {
                    PlaceManager.getInstance().stopMonitoring();
                    CommunicationManager.getInstance().stopReceivingCommunications();
                }
                return true;
            }
        });

        Preference resetAppInstanceIdPreference = findPreference("pref_reset_app_instance_id");
        resetAppInstanceIdPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Gimbal.resetApplicationInstanceIdentifier();
                Toast.makeText(SettingsActivity.this, "App Instance ID reset", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        CheckBoxPreference pref = (CheckBoxPreference) findPreference(GimbalDAO.PLACE_MONITORING_PREFERENCE);
        pref.setChecked(PlaceManager.getInstance().isMonitoring());
    }
}
