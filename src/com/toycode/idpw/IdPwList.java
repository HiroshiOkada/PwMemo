package com.toycode.idpw;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;
import android.widget.AbsListView.LayoutParams;

public class IdPwList extends ListActivity implements OnClickListener {
	
	IdPwAdapter mAdapter;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        getListView().setEmptyView(findViewById(R.id.EmptyTextView));
        mAdapter = new IdPwAdapter(this);
        setListAdapter(mAdapter);
        ((Button)findViewById(R.id.AddButton)).setOnClickListener(this);
    }
	
	public class IdPwAdapter extends BaseAdapter {

		private ArrayList<String> mTitles = new ArrayList<String>();
		private Context mContext;
		
		public IdPwAdapter(Context context) {
			mContext = context;
		}

		@Override
		public int getCount() {
			return mTitles.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Button button = new Button(mContext);
			button.setText(mTitles.get(position));
			button.setLayoutParams(new AbsListView.LayoutParams(
					LayoutParams.FILL_PARENT,                                                      
					LayoutParams.WRAP_CONTENT));  
			button.setBackgroundColor(Color.TRANSPARENT);
			button.setTextColor(Color.WHITE);
			button.setOnClickListener(IdPwList.this);
			return button;
		}
		
		public void addItem(){
			String title = "Untitled " + mTitles.size();
			mTitles.add(title);
			notifyDataSetChanged();
		}
	}

	@Override
	public void onClick(View v) {
		if( v.getId() == R.id.AddButton){
			mAdapter.addItem();
		}else if( v.getClass() == Button.class){
            Toast.makeText(this, ((Button)v).getText().toString(),  Toast.LENGTH_SHORT)
            	 .show();
            Intent i = new Intent(this,IdPwEdit.class);
            i.putExtra("Title", ((Button)v).getText());
            startActivityForResult(i, 0);
        }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

}