package com.toycode.pwmemo.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.FrameLayout;
import com.toycode.pwmemo.R;
import com.toycode.pwmemo.PwMemoDbOpenHelper;
import com.toycode.pwmemo.MainListActivity;
import com.toycode.pwmemo.PasswordManager;

public class MainListActivityTest extends
		ActivityInstrumentationTestCase2<MainListActivity> {

	static final String PREF_NAME = "PREF";
	static final String TEST_PASSWORD="test$pass";
	static final class TABLE {
		static final String IDPW = "idpw";
	}
	static final class COLUMN {
		static final String ID = "_id";
		static final String TITLE = "title";
		static final String CRIPTDATA = "cryptdata";
	}
	
	MainListActivity mActivity;
	FrameLayout mFrameLayout;
	
	public MainListActivityTest() {
		super("com.toycode.pwmemo", MainListActivity.class);
	}
	
	/**
	 * 最初のコンディションのテスト
	 */
	public void testInitalCondition(){
		assertEquals( 2, mFrameLayout.getChildCount());
		assertEquals( View.GONE, mFrameLayout.getChildAt(0).getVisibility());
		assertEquals( View.VISIBLE, mFrameLayout.getChildAt(1).getVisibility());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Context context = getInstrumentation().getTargetContext();
        // delete data
        deleteSharedPreferencesData( context);
        (new PwMemoDbOpenHelper(context)).deleteaAll();

        mActivity = getActivity();
		// delete data
		deleteSharedPreferencesData( mActivity);
        deleteDBData( mActivity);
		
		// create new password
		PasswordManager.deleteInstance();
		PasswordManager pm = PasswordManager.getInstance(context);
		pm.createMainPassword(TEST_PASSWORD);
		PasswordManager.deleteInstance();
		
		setActivityInitialTouchMode(false); // テストがキーイベントを送る時はタッチモードをoffにする
		
		mFrameLayout = (FrameLayout) mActivity.findViewById(R.id.frame_layout);
	}

	@Override
	protected void tearDown() throws Exception {
		// シェアードプリファレンスに格納されているデータを削除
		deleteSharedPreferencesData( mActivity);
		// SqlDB に格納されているデータを削除
		deleteDBData( mActivity);
		super.tearDown();
	}
	
	// シェアードプリファレンスに格納されているデータを削除
	private void deleteSharedPreferencesData( Context context) {
		SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		pref.edit().clear().commit();
	}
	
	// SqlDB に格納されているデータを削除
	private void deleteDBData( Context context) {
		SQLiteDatabase db = (new PwMemoDbOpenHelper(context)).getReadableDatabase();
		db.execSQL("drop table if exists " + TABLE.IDPW + ";");
		db.execSQL( "create table " + TABLE.IDPW + 
			    " ( " + COLUMN.ID + " integer primary key autoincrement, " +
			            COLUMN.TITLE + " text not null, " +
			            COLUMN.CRIPTDATA + " text);");
	}
}
