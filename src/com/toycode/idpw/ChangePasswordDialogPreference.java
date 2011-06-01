package com.toycode.idpw;
import android.content.Context;
import android.preference.EditTextPreference;
import android.text.InputType;
import android.util.AttributeSet;

public class ChangePasswordDialogPreference extends EditTextPreference {

	public ChangePasswordDialogPreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		getEditText().setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
		Toy.log(this, context.getClass().getName());
	}

	public ChangePasswordDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		getEditText().setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
		Toy.log(this, context.getClass().getName());
	}

	public ChangePasswordDialogPreference(Context context) {
		super(context);
		getEditText().setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
		Toy.log(this, context.getClass().getName());
	}

}
