package com.toycode.idpw.test;

import com.toycode.idpw.DeclarMasterPasswordActivity;
import com.toycode.idpw.PasswordManager;
import com.toycode.idpw.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DeclarMasterPasswordActivityTest extends
		ActivityInstrumentationTestCase2<DeclarMasterPasswordActivity> {

	static final String PREF_NAME = "PREF";

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
	
	/**
	 * 普通にマスターパスワードを作成できるかのテスト
	 */
	public void testNormalcreate(){
		setForcus(mMasterPasswordEditText);
		sendKeys("T E S T");
		setForcus(mConfirmationEditText);
		sendKeys("T E S T");
		assertTrue( mOkButton.isEnabled());
		clickButton( mOkButton);
		PasswordManager pm = PasswordManager.getInstance(mActiviy);
		assertTrue( pm.isMainPasswordExist());		
	}
	
	/**
	 * 両方のフィールドに同じ文字が設定されていなければボタンは押せない
	 */
	public void testDifferentText(){
		setForcus(mMasterPasswordEditText);
		sendKeys("A B C D E");
		setForcus(mConfirmationEditText);
		sendKeys("A B B D E");
		assertFalse( mOkButton.isEnabled());
	}

	/**
	 * 4文字未満のパスワードではボタンは押せない
	 */
	public void testShortText(){
		setForcus(mMasterPasswordEditText);
		sendKeys("A B C");
		setForcus(mConfirmationEditText);
		sendKeys("A B C");
		assertFalse( mOkButton.isEnabled());
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		setActivityInitialTouchMode(false); // テストがキーイベントを送る時はタッチモードをoffにする 
		mActiviy = getActivity();
		mMasterPasswordEditText = (EditText)mActiviy.findViewById( R.id.MasterPasswordEditText);
		mConfirmationEditText = (EditText)mActiviy.findViewById( R.id.ConfirmationEditText);
		mOkButton = (Button)mActiviy.findViewById( R.id.OkButton);
		// ShardPerefe を削除
		SharedPreferences pref = mActiviy.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		pref.edit().clear().commit();
	}
	
	/**
	 * 指定の view にフォーカスを写す
	 */
	private void setForcus(View v){
		final View view = v;
		mActiviy.runOnUiThread( new Runnable() {
            @Override
            public void run() {
            	view.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
	}

	/**
	 * ボタンをクリックする
	 */
	private void clickButton( Button b){
		final Button button = b;
		mActiviy.runOnUiThread( new Runnable() {
            @Override
            public void run() {
            	button.performClick();
            }
        });
        getInstrumentation().waitForIdleSync();		
	}
	
	@Override
	protected void tearDown() throws Exception {
		// ShardPerefe を削除
		SharedPreferences pref = mActiviy.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		pref.edit().clear().commit();
		super.tearDown();
	}
}
