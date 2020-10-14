package com.su.lab;

import android.Manifest;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class DetailsActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    static String CONTACT_ID_EXTRA = "su.lab.CONTACT_ID_EXTRA";

    // Defines a constant that identifies the loader
    static int DETAILS_QUERY_ID = 0;

    private void openSystemPhone(View view) {
        String phone = ((TextView)view.findViewById(R.id.contacts_phone_number)).getText().toString();
        Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
        phoneIntent.setData(Uri.parse("tel:" + phone));
        startActivity(phoneIntent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getLoaderManager().initLoader(DETAILS_QUERY_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Intent intent = getIntent();
        long contactId = intent.getLongExtra(CONTACT_ID_EXTRA, -1);
        return new CursorLoader(
                this,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= " + contactId,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursor.moveToPosition(0);
        final String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        TextView phoneNumber = findViewById(R.id.contacts_phone_number);
        phoneNumber.setText(number);
        phoneNumber.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    openSystemPhone(v);
                } else  {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE},  0);
                }
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
