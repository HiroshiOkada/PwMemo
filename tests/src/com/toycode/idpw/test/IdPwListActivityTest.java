package com.toycode.idpw.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.FrameLayout;
import com.toycode.idpw.R;
//import com.toycode.idpw.IdPwDbOpenHelper;
import com.toycode.idpw.Const;
import com.toycode.idpw.IdPwDbOpenHelper;
import com.toycode.idpw.IdPwListActivity;
import com.toycode.idpw.PasswordManager;

public class IdPwListActivityTest extends
		ActivityInstrumentationTestCase2<IdPwListActivity> {

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
	
	IdPwListActivity mActivity;
	FrameLayout mFrameLayout;
	
	public IdPwListActivityTest() {
		super("com.toycode.idpw", IdPwListActivity.class);
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
		
		// シェアードプリファレンスに格納されているデータを削除
		deleteSharedPreferencesData( context);
		
		// 新規のパスワードを作成
		PasswordManager.deleteInstance();
		PasswordManager pm = PasswordManager.getInstance(context);
		pm.createMainPassword(TEST_PASSWORD);
		PasswordManager.deleteInstance();

		// SqlDB に格納されているデータを削除
		deleteDBData( context);
		
		setActivityInitialTouchMode(false); // テストがキーイベントを送る時はタッチモードをoffにする
		
		mActivity = getActivity();
		mFrameLayout = (FrameLayout) mActivity.findViewById(R.id.FrameLayout);
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
		SQLiteDatabase db = (new IdPwDbOpenHelper(context)).getReadableDatabase();
		db.execSQL("drop table if exists " + TABLE.IDPW + ";");
		db.execSQL( "create table " + TABLE.IDPW + 
			    " ( " + COLUMN.ID + " integer primary key autoincrement, " +
			            COLUMN.TITLE + " text not null, " +
			            COLUMN.CRIPTDATA + " text);");
	}
}
