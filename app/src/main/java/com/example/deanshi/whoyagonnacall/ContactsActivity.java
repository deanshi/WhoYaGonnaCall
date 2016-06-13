package com.example.deanshi.whoyagonnacall;

import android.Manifest;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ContactsActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /* Constant and Global Values */
    private static final String[] GETTING_FROM = new String[] { ContactsContract.Contacts.DISPLAY_NAME };
    private static final int[] POSTING_TO = new int[] { android.R.id.text1, android.R.id.text2 };
    private static final String[] PROJECTION = new String[] {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME
    };
    private static final String SELECTION = "((" +
            ContactsContract.Contacts.DISPLAY_NAME + " NOTNULL) AND (" +
            ContactsContract.Contacts.DISPLAY_NAME + " != '') AND (" +
            ContactsContract.Contacts.HAS_PHONE_NUMBER + "))";
    private static String contactName = "";
    SimpleCursorAdapter contactsCursorAdapter;
    Uri contactUri;

    /* Binding Views */
    @BindView(R.id.search_contacts_view)
    SearchView contactsSearchView;

    @BindView(android.R.id.list)
    ListView contactsListView;

    /* Methods and Functions */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        ButterKnife.bind(this);

        createContactsAdapter();
        getLoaderManager().initLoader(0, null, this);
        setupSearchView();
    }

    public void createContactsAdapter() {
        contactsCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, GETTING_FROM, POSTING_TO, 0);
        setListAdapter(contactsCursorAdapter);
    }

    public void setupSearchView() {
        contactsSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String contactText) {
                Timber.d("SearchView text has been modified: %s", contactText);
                contactName = TextUtils.isEmpty(contactText) ? "" : contactText;
                getLoaderManager().restartLoader(0, null, ContactsActivity.this);
                return true;
            }
        });
        contactsSearchView.setSubmitButtonEnabled(true); //Not used in my case.
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (TextUtils.isEmpty(contactName)) {
            contactUri = ContactsContract.Contacts.CONTENT_URI;
        } else {
            contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, Uri.encode(contactName));
        }
        return new CursorLoader(this, contactUri, PROJECTION, SELECTION, null,
                                ContactsContract.Contacts.DISPLAY_NAME + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        contactsCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        contactsCursorAdapter.swapCursor(null);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        // Need to figure out how to pull Phone Number and Name to display/use within the application
        // Must look into the Projection/Selection/Data fields.
        // This cannot be the best implementation. There are too many things wrong with it:
        // 1. Queries twice, which is not ideal. Should only query once. May change that in the future.
        // 2. Moves the userNumberCursor to first, when you should only need to query the Column Index?

        Cursor contactClicked = (Cursor) getListAdapter().getItem(position);
        String userName = contactClicked.getString(contactClicked.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        Timber.d("Username of Clicked Contact: %s", userName);

        Uri numberUri = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, Uri.encode(userName));
        String[] numberProjection = new String[] { ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER };
        Cursor userNumberCursor = getContentResolver().query(numberUri, numberProjection, null, null, null);

        assert userNumberCursor != null;
        if (!userNumberCursor.moveToFirst()) return;
        String userNumber = userNumberCursor.getString(userNumberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        userNumberCursor.close();

        Intent callPhoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + userNumber));
        startActivity(callPhoneIntent);
    }
}



