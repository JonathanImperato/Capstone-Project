package com.ji.bookinhand.ui.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ji.bookinhand.BuildConfig;
import com.ji.bookinhand.R;
import com.ji.bookinhand.ui.LicensesActivity;
import com.ji.bookinhand.ui.LoginActivity;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {


    GoogleSignInAccount account;
    GoogleSignInClient mGoogleSignaInClient;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        account = GoogleSignIn.getLastSignedInAccount(getContext());
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignaInClient = GoogleSignIn.getClient(getContext(), gso);

        Preference accountPref = findPreference("account");
        Preference libraryPref = findPreference("library");
        //      Preference feedbackPref = findPreference("feedback");
        Preference versionPref = findPreference("version");
        Preference ratePref = findPreference("rate");
        Preference betaPref = findPreference("join_beta");
        /*new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                //open browser or intent here
                startActivity(new Intent(getContext(), LicensesActivity.class));
                return true;
            }
        }*/
        if (account != null)
            accountPref.setTitle(account.getDisplayName());

        if (versionPref != null) {
            String version = BuildConfig.VERSION_NAME;
            String versionFlav = BuildConfig.FLAVOR;
            versionPref.setSummary(version + " " + versionFlav);
        }
        accountPref.setOnPreferenceClickListener(this);
        libraryPref.setOnPreferenceClickListener(this);
        ratePref.setOnPreferenceClickListener(this);
        betaPref.setOnPreferenceClickListener(this);
        //     feedbackPref.setOnPreferenceClickListener(this);
        versionPref.setOnPreferenceClickListener(this);

        /*if (!isPackageInstalled("com.google.android.feedback")) {
            feedbackPref.setSelectable(false);
            feedbackPref.setEnabled(false);
        }*/
    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey().toString()) {
            case "account":
                if (account == null) {
                    startActivity(new Intent(getContext(), LoginActivity.class));
                } else {
                    createLogoutDialog();
                }
                return true;
            case "rate":
                final String appPackageName = getContext().getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException ex) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                return true;
            case "library":
                startActivity(new Intent(getContext(), LicensesActivity.class));
                return true;
            case "join_beta":
                Toast.makeText(getContext(), "Join beta not yet available.", Toast.LENGTH_SHORT).show();
                return true;
            case "feedback":
                if (isPackageInstalled("com.google.android.feedback")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setClassName("com.google.android.feedback", "com.google.android.feedback.FeedbackActivity");
                    startActivity(intent);
                }
                return true;
            case "version":
                return true;
        }
        return true;
    }

    private void createLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure?")
                .setTitle("Logout")
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (account != null) {
                            revokeAccess(); //todo: check if it logs out successfully
                            account = null;
                        }
                    }
                })
                .setNegativeButton("Cancel", null);

        builder.create().show();
    }

    private void revokeAccess() {
        mGoogleSignaInClient.revokeAccess()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "Successfully signed out.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isPackageInstalled(String packagename) {
        PackageManager packageManager = getContext().getPackageManager();
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
