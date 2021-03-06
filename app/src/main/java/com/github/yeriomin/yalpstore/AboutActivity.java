package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.yeriomin.yalpstore.bugreport.BugReportSenderEmail;

public class AboutActivity extends YalpStoreActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity_layout);
        ((TextView) findViewById(R.id.version)).setText(BuildConfig.VERSION_NAME);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ((TextView) findViewById(R.id.user_email)).setText(sharedPreferences.getString(PlayStoreApiAuthenticator.PREFERENCE_EMAIL, ""));
        TextView gsfIdView = (TextView) findViewById(R.id.gsf_id);
        gsfIdView.setText(sharedPreferences.getString(PlayStoreApiAuthenticator.PREFERENCE_GSF_ID, ""));
        gsfIdView.setOnClickListener(new CopyToClipboardListener());
        findViewById(R.id.developer_email).setOnClickListener(new CopyToClipboardListener() {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                new BugReportSenderEmail(getApplicationContext()).send();
            }
        });
        findViewById(R.id.website).setOnClickListener(new UriOpeningListener());
        findViewById(R.id.paypal).setOnClickListener(new UriOpeningListener());
        findViewById(R.id.bitcoin).setOnClickListener(new UriOpeningListener() {
            @Override
            protected String getUri(View v) {
                return "bitcoin:" + super.getUri(v) + "?label=YalpStore";
            }
        });
    }

    private class CopyToClipboardListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).setText(((TextView) v).getText());
            Toast.makeText(v.getContext().getApplicationContext(), R.string.about_copied_to_clipboard, Toast.LENGTH_SHORT).show();
        }
    }

    public class UriOpeningListener extends CopyToClipboardListener {
        @Override
        public void onClick(View v) {
            super.onClick(v);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getUri(v)));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }

        protected String getUri(View v) {
            return (String) ((TextView) v).getText();
        }
    }
}
