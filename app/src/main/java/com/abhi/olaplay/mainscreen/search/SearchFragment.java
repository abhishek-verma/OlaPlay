package com.abhi.olaplay.mainscreen.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.abhi.olaplay.R;
import com.abhi.olaplay.model.repositories.SearchRepo;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragment to implement the search view,
 * fragment is used instead of a custom view,
 * because it will make easier adding features in future
 *
 * Created by Abhishek on 12/17/2017.
 */
public class SearchFragment extends Fragment implements SearchContract.SearchView {

    @BindView(R.id.searchBarEdtV)
    EditText mSearchBarEdtV;
    @BindView(R.id.searchBarClearBtn)
    ImageButton mSearchBarClearBtn;
    private SearchContract.SearchListener mListener;

    public static SearchFragment newInstance() {

        Bundle args = new Bundle();

        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search_fragment, container, false);
        ButterKnife.bind(this, rootView);

        mListener = new SearchPresenter(this, getActivity());
        setViewListeners();

        return rootView;
    }

    /**
     * set view listener for edit text and clear button
     */
    private void setViewListeners() {
        mSearchBarEdtV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mListener.onTextChanged(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mSearchBarClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClearClicked();
            }
        });
    }

    @Override
    public void setText(String text) {
        mSearchBarEdtV.setText(text);
    }

    /**
     * highlight text
     * if the text pattern matches a playlist search
     * eg: PLAYLIST: playlist_name
     *
     * @param highlight enable or disable
     */
    @Override
    public void highlightText(boolean highlight) {
        try {
            if (highlight)
                mSearchBarEdtV.setTextColor(getResources().getColor(R.color.highlighted_text_color));
            else
                mSearchBarEdtV.setTextColor(getResources().getColor(R.color.white));
        } catch (IllegalStateException ignore) {}
    }

    /**
     * clear text
     */
    @Override
    public void clear() {
        mSearchBarEdtV.setText("");
    }
}
