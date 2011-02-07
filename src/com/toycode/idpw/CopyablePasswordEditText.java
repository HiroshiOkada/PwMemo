package com.toycode.idpw;

import android.content.Context;
import android.graphics.Typeface;
import android.text.ClipboardManager;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.widget.EditText;

public class CopyablePasswordEditText extends EditText {

	public CopyablePasswordEditText(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		setUp();
	}

	public CopyablePasswordEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		setUp();
	}

	public CopyablePasswordEditText(Context context) {
		this(context, null);
	}

	public void setUp() {
		setTypeface(Typeface.MONOSPACE);
		setTransformationMethod(PasswordTransformationMethod.getInstance());
	}

	@Override
	protected void onCreateContextMenu(ContextMenu menu) {
		super.onCreateContextMenu(menu);
		if( menu.size() == 0){
			menu.setHeaderTitle("Copy Password");
		}
		menu.add(0, android.R.id.copy, 0, "Copy Password")
				.setOnMenuItemClickListener(
						new MenuItem.OnMenuItemClickListener() {
							public boolean onMenuItemClick(MenuItem item) {
								return onTextContextMenuItem(item.getItemId());
							}
						})
				.setAlphabeticShortcut('c');
	}

	@Override
	public boolean onTextContextMenuItem(int id) {
		if (id == android.R.id.copy) {
			Context context = getContext();
			ClipboardManager clipboardManager = (ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboardManager.setText(getText());
			return true;
		} else {
			return super.onTextContextMenuItem(id);
		}
	}
}
