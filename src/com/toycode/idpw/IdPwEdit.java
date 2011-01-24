package com.toycode.idpw;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

public class IdPwEdit extends Activity implements OnClickListener {
	EditText mTitleEditText;
	ImageView mLockImageView;
	boolean mLockState = true;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Edit");
        setContentView(R.layout.edit);
    	mTitleEditText = (EditText)findViewById(R.id.TitleEditText);
    	mLockImageView = (ImageView)findViewById(R.id.LockImageView);
    	mLockImageView.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String title = extras.getString("Title");
            if( title != null){
            	mTitleEditText.setText(title);
            }           
        }    
    }

	@Override
	public void onClick(View v) {
		if( v.getId()== R.id.LockImageView ){
			mLockState = !mLockState;
			if( mLockState){
				mLockImageView.setImageResource(R.drawable.keylock);
			}else{
				mLockImageView.setImageResource(R.drawable.keyunlock);
			}
		}		
	}
}
