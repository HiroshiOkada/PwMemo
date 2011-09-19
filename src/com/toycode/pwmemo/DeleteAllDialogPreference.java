
package com.toycode.pwmemo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.PreferenceActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class DeleteAllDialogPreference extends DialogPreference {
    CheckBox mCheckBox;
    Button mPositiveButton = null;
    
    PreferenceActivity mPreferenceActivity = null;

    public DeleteAllDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (context instanceof PreferenceActivity) {
            mPreferenceActivity = (PreferenceActivity)context;
        }
    }

    /**
     * At first, disable the OK button.
     */
    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);
        Dialog dialog = getDialog();
        if (dialog instanceof AlertDialog){
            mPositiveButton = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE);
            mPositiveButton.setEnabled(false);
        }
    }
        
    public DeleteAllDialogPreference(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Add CheckBox for avoid delete by mistake.
     */
    @Override
    protected View onCreateDialogView() {
        mCheckBox = new CheckBox(getContext());
        mCheckBox.setText(R.string.delete_all_summary);
        mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mPositiveButton != null) {
                    mPositiveButton.setEnabled(isChecked);
                }                
            }
            
        });
        return mCheckBox;
    }

    /**
     * ChecBox ON and OK button then delete.
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
