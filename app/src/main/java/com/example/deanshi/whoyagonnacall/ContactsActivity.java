package com.example.deanshi.whoyagonnacall;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactsActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String[] GETTING_FROM = new String[] { ContactsContract.Contacts.DISPLAY_NAME };
    private static final int[] POSTING_TO = new int[] { android.R.id.text1 };
    private static final String[] PROJECTION = new String[] {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME};
    private static final String SELECTION = "((" +
                    ContactsContract.Contacts.DISPLAY_NAME + " NOTNULL) AND (" +
                    ContactsContract.Contacts.DISPLAY_NAME + " != '') AND (" +
                    ContactsContract.Contacts.HAS_PHONE_NUMBER + "))";
    SimpleCursorAdapter contactsCursorAdapter;


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

        // Must add the progress bar to the root of the layout
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
                contactsCursorAdapter.getFilter().filter("B");
                contactsCursorAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String contactText) {
                Log.i("QueryTextChange", "Text has been changed");
                if (TextUtils.isEmpty(contactText)) {
                    contactsListView.clearTextFilter();
                } else {
                    contactsCursorAdapter.getFilter().filter("B");
                    contactsCursorAdapter.notifyDataSetChanged();
                }
                contactsCursorAdapter.notifyDataSetChanged();
                return true;
            }
        });
        contactsSearchView.setSubmitButtonEnabled(true);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ContactsContract.Contacts.CONTENT_URI, PROJECTION, SELECTION, null, null);
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



