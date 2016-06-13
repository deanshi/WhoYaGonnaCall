package com.example.deanshi.whoyagonnacall;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactsActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String[] GETTING_FROM = new String[] { ContactsContract.Contacts.DISPLAY_NAME };
    private static final int[] POSTING_TO = new int[] { android.R.id.text1 };
    private static final String[] PROJECTION = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME };
    private static String SELECTION = "((" +
            ContactsContract.Contacts.DISPLAY_NAME + " NOTNULL) AND (" +
            ContactsContract.Contacts.DISPLAY_NAME + " != '') AND (" +
            ContactsContract.Contacts.HAS_PHONE_NUMBER + "))";
    private static String contactName = "";
    SimpleCursorAdapter contactsCursorAdapter;
    Uri contactUri;

    @BindView(R.id.search_contacts_view)
    SearchView contactsSearchView;

    @BindView(android.R.id.list)
    ListView contactsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        ButterKnife.bind(this);

        createProgressBar();
        createContactsAdapter();
        getLoaderManager().initLoader(0, null, this);
        setupSearchView();
    }

    public void createProgressBar() {
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.WRAP_CONTENT,
                                                              ListView.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);

        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);
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
                Log.i("QueryTextChange", "Text has been changed: " + contactText);
                if (TextUtils.isEmpty(contactText)) {
                    contactName = "";
                } else {
                    contactName = contactText;
                }
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
        return new CursorLoader(this, contactUri, PROJECTION, SELECTION, null, null);
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
        // Do something
    }


}



