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
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.abhi.olaplay.R;
import com.abhi.olaplay.model.repositories.PlaylistRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a dialog to
 * remove song from a playlist
 */

public class RemoveFromPlaylistDialogFragment extends DialogFragment {

    private static final String EXTRA_STRING_SONG_ID = "song_id";
    private String mSongId;
    private AlertDialog mAlertDialog;
    private ArrayList<String> mPlaylists;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSongId = getArguments().getString(EXTRA_STRING_SONG_ID);
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
                .setTitle(getResources().getString(R.string.remove_from_playlist))
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        // Add to chosen playlist
                        PlaylistRepository.getInstance(getActivity().getApplicationContext())
                                .removeSongFromPlaylist(mSongId,
                                        mPlaylists.get(i));
                    }
                })
                .create();
        return mAlertDialog;
    }

    public static class Builder {
        private static final String FRAGMENT_TAG = "RFPDFrag_tag";
        RemoveFromPlaylistDialogFragment mFragment;

        /**
         * @param songId the id created by app, not the device id
         */
        public Builder(String songId) {
            mFragment = new RemoveFromPlaylistDialogFragment();
            Bundle args = new Bundle();
            args.putString(EXTRA_STRING_SONG_ID, songId);
            mFragment.setArguments(args);
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
