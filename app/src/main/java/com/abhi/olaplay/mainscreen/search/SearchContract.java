package com.abhi.olaplay.mainscreen.search;

/**
 * Created by Abhishek on 12/17/2017.
 */

interface SearchContract {

    interface SearchView {

        void highlightText(boolean highlight);

        void clear();

        void setText(String text);
    }

    interface SearchListener {

        void onTextChanged(String s);

        void onClearClicked();
    }
}
