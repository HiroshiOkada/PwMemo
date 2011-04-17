package com.toycode.idpw.test;

import com.toycode.idpw.DeclarMasterPasswordActivity;
import com.toycode.idpw.R;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;

public class DeclarMasterPasswordActivityTest extends
		ActivityInstrumentationTestCase2<DeclarMasterPasswordActivity> {

	DeclarMasterPasswordActivity mActiviy;
	EditText mMasterPasswordEditText;
    EditText mConfirmationEditText;
	Button mOkButton;
	
	public DeclarMasterPasswordActivityTest() {
		super( "com.toycode.idpw", DeclarMasterPasswordActivity.class);
	}
	
	/**
	 * 最初のコンディションのテスト
	 */
	public void testInitalCondition(){
		assertEquals( "", mMasterPasswordEditText.getText().toString());
		assertEquals( "", mConfirmationEditText.getText().toString());
		assertFalse( mOkButton.isEnabled());
		
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mActiviy = getActivity();
		mMasterPasswordEditText = (EditText)mActiviy.findViewById( R.id.MasterPasswordEditText);
		mConfirmationEditText = (EditText)mActiviy.findViewById( R.id.ConfirmationEditText);
		mOkButton = (Button)mActiviy.findViewById( R.id.OkButton);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
