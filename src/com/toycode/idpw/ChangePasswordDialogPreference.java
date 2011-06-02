package com.toycode.idpw;
import android.content.Context;
import android.content.Intent;
import android.preference.EditTextPreference;
import android.text.InputType;
import android.util.AttributeSet;

public class ChangePasswordDialogPreference extends EditTextPreference {
	private Context mContext = null;

	public ChangePasswordDialogPreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		getEditText().setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
		mContext = context;
	}

	public ChangePasswordDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		getEditText().setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
		mContext = context;
	}

	public ChangePasswordDialogPreference(Context context) {
		super(context);
		getEditText().setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
		mContext = context;
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (positiveResult && (mContext != null)) {
			Intent i = new Intent(mContext, UpdateMasterPasswordActivity.class);
			mContext.startActivity(i);
		}
	}
}
