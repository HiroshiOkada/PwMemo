package com.toycode.idpw;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;

public class DeleteAllDialogPreference extends DialogPreference {
	CheckBox mCheckBox;
	
	public DeleteAllDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
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
	 * OK が押されていて、チェックボックスがチェックされているときのみ
	 * データ削除
	 */
	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (positiveResult) {
			Toy.toastMessage(getContext(), "r=" + Boolean.toString(mCheckBox.isChecked()));
		}
	}
}
