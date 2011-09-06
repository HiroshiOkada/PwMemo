package com.toycode.pwmemo.test;

import com.toycode.pwmemo.App;

import android.content.Context;
import android.test.AndroidTestCase;
import android.widget.TextView;

public class AppTest extends AndroidTestCase {
    Context mContext;

    public void testIsEmptyTextView() {
        assertTrue(App.isEmptyTextView(null));
        TextView tv = new TextView(mContext);
        tv.setText("");
        assertTrue(App.isEmptyTextView(tv));
        tv.setText("a");
        assertFalse(App.isEmptyTextView(tv));
    }

    public void testIsEmptyCharSequence() {
        assertTrue(App.isEmptyCharSequence(null));
        assertTrue(App.isEmptyCharSequence(""));
        assertFalse(App.isEmptyCharSequence("\0"));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mContext = getContext();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
