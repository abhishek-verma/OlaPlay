package com.abhi.olaplay.playlist;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.abhi.olaplay.R;
import com.abhi.olaplay.model.repositories.PlaylistRepository;
import com.abhi.olaplay.model.repositories.SearchRepo;
import com.abhi.olaplay.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Dialog to select a playlist, and set that
 * playlist name as search term in search dialog
 */

public class SelectPlaylistDialogFragment extends DialogFragment {

    private AlertDialog mAlertDialog;
    private ArrayList<String> mPlaylists;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mPlaylists = new ArrayList<>();

        final ArrayAdapter adapter
                = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1,
                mPlaylists);

        new AsyncTask<Void, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(Void... voids) {

                return PlaylistRepository
                        .getInstance(getActivity())
                        .getPlaylists();

            }

            @Override
            protected void onPostExecute(List<String> valueSet) {
                super.onPostExecute(valueSet);
                mPlaylists.addAll(valueSet);
                adapter.notifyDataSetChanged();
            }
        }.execute();

        mAlertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.show_songs_by_playlist))
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SearchRepo.getInstance().setSearchTerm(TextUtils.PLAYLIST_IDENTIFIER + mPlaylists.get(i));
                    }
                })
                .create();
        return mAlertDialog;
    }

    public static class Builder {
        private static final String FRAGMENT_TAG = "SPDFrag_tag";
        SelectPlaylistDialogFragment mFragment;

        public Builder() {
            mFragment = new SelectPlaylistDialogFragment();
        }

        public void show(FragmentManager fm) {
            // DialogFragment.show() will take care of adding the fragment
            // in a transaction.  We also want to remove any currently showing
            // dialog, so make our own transaction and take care of that here.
            FragmentTransaction ft = fm.beginTransaction();
            Fragment prev = fm.findFragmentByTag(FRAGMENT_TAG);
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            // Create and show the dialog.
            mFragment.show(ft, FRAGMENT_TAG);
        }
    }
}
