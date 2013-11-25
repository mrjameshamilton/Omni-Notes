package it.feio.android.omninotes;

import java.io.File;
import java.util.Locale;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

import it.feio.android.omninotes.utils.ChangelogFragment;
import it.feio.android.omninotes.utils.Constants;
import it.feio.android.omninotes.db.DbHelper;
import it.feio.android.omninotes.utils.ImportExportExcel;
import it.feio.android.omninotes.utils.StorageManager;
import it.feio.android.omninotes.R;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.widget.Toast;

public class SettingsActivity extends SherlockPreferenceActivity {

	public final static String KEEP_USER_DATA = "settings_keep_user_data";
	public final static String ALLOW_GEOLOCATION = "settings_allow_geolocation";
	public final static String ALLOW_MOBILE_DATA = "settings_allow_mobile_data";
	final Activity activity = this;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);


		// Export notes
		Preference export = findPreference("settings_export_data");
		export.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference arg0) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

				// set dialog message
				alertDialogBuilder.setMessage(getString(R.string.export_warning)).setCancelable(false)
						.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int id) {
								try {
									ImportExportExcel importExportExcel = new ImportExportExcel(activity);
									if (importExportExcel.exportDataToCSV(StorageManager.getStorageDir()))
										Toast.makeText(
												activity,
												getString(R.string.export_success) + " " + StorageManager.getStorageDir()
														+ File.separator + Constants.EXPORT_FILE_NAME + ".csv",
												Toast.LENGTH_LONG).show();
									else
										Toast.makeText(activity, getString(R.string.export_fail), Toast.LENGTH_LONG).show();
								} catch (Exception e) {
									Toast.makeText(activity, getString(R.string.export_fail), Toast.LENGTH_LONG).show();
								}
							}
						}).setNegativeButton("No", new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();
				return false;
			}
		});

		// Import notes
		Preference importData = findPreference("settings_import_data");
		importData.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference arg0) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

				// set dialog message
				alertDialogBuilder.setMessage(getString(R.string.import_warning)).setCancelable(false)
						.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int id) {
								try {
									ImportExportExcel importExportExcel = new ImportExportExcel(activity);
									if (importExportExcel.importDataFromCSV(StorageManager.getStorageDir()))
										Toast.makeText(activity, getString(R.string.import_success),
												Toast.LENGTH_LONG).show();
									else
										Toast.makeText(activity, getString(R.string.import_fail),
												Toast.LENGTH_LONG).show();
								} catch (Exception e) {
									Toast.makeText(activity, getString(R.string.import_fail),
											Toast.LENGTH_LONG).show();
								}
							}
						}).setNegativeButton("No", new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();
				return false;
			}
		});


		// Changelog 
		Preference changelog = findPreference("settings_changelog");
		changelog.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference arg0) {
				Intent changelogIntent = new Intent(activity, ChangelogActivity.class);
				startActivity(changelogIntent);
//				ChangelogFragment cf = new ChangelogFragment();
//				cf.show(getFragmentManager(), Constants.TAG);
				return false;
			}
		});


		// Evento di pressione sul pulsante di reset delle impostazioni
		Preference resetData = findPreference("reset_all_data");
		resetData.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference arg0) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

				// set dialog message
				alertDialogBuilder.setMessage(getString(R.string.reset_all_data_confirmation))
						.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int id) {
								PreferenceManager.getDefaultSharedPreferences(activity).edit().clear()
										.commit();
								DbHelper db = new DbHelper(activity);
								db.clear();
								Log.i(Constants.TAG, "Settings back to default");
							}
						}).setNegativeButton("No", new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();
				return false;
			}

		});


		// Popup About
		Preference about = findPreference("settings_about");
		about.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference arg0) {
				Intent aboutIntent = new Intent(activity, AboutActivity.class);
				startActivity(aboutIntent);
				return false;
			}
		});
		
		// Languages 
		Preference lang = findPreference("settings_language");
		lang.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object value) {
				Locale locale = new Locale(value.toString());
				Configuration config = new Configuration();
				config.locale = locale;
				getBaseContext().getResources().updateConfiguration(config,
						getBaseContext().getResources().getDisplayMetrics());
				PreferenceManager.getDefaultSharedPreferences(activity).edit().putString("settings_language", value.toString()).commit();
				finish();
				startActivity(new Intent(getApplicationContext(), ListActivity.class));
				return false;
			}
		});

	}
}
