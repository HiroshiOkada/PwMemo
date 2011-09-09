
package com.toycode.pwmemo;

import android.content.Context;
import android.preference.DialogPreference;
import android.preference.PreferenceActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;

public class DeleteAllDialogPreference extends DialogPreference {
    CheckBox mCheckBox;
    PreferenceActivity mPreferenceActivity = null;

    public DeleteAllDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (context instanceof PreferenceActivity) {
            mPreferenceActivity = (PreferenceActivity)context;
        }
    }

    public DeleteAllDialogPreference(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 確認を確実にするためにチェックボックスを追加
     */
    @Override
    protected View onCreateDialogView() {
        mCheckBox = new CheckBox(getContext());
        mCheckBox.setText(R.string.delete_all_summary);
        return mCheckBox;
    }

    /**
     * OK が押されていて、チェックボックスがチェックされているときのみ データ削除
     */
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult & mCheckBox.isChecked()) {
            Context context = getContext();
            PwMemoDbOpenHelper dbHelper = new PwMemoDbOpenHelper(context);
            dbHelper.deleteaAll();
            PasswordManager.getInstance(context).deleteMasterPassword();
            App.GetApp(context).toastMessage(R.string.all_data_deleted);
            if (mPreferenceActivity != null) {
                mPreferenceActivity.finish();   
            }
        }
    }
}
