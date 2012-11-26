package com.ac;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import android.widget.LinearLayout.LayoutParams;

import com.myopicmobile.textwarrior.android.TextWarriorIntents;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AC extends Activity implements OnKeyListener {

	static ACListView leftPanel;
	static ACListView rightPanel;
	static RootStorage leftES, rightES;

	Activity context;

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.ac);

		context = this;

		int h=Settings.getTextLineHeight()+Settings.getTextLineGap();
		
		TextView dotLeft = (TextView) findViewById(R.id.dotLeft);
		dotLeft.setTextSize(Settings.TextSize);
		dotLeft.setHeight(h);
		TextView dotRight = (TextView) findViewById(R.id.dotRight);
		dotRight.setTextSize(Settings.TextSize);
		dotRight.setHeight(h);
		TextView fsLeft = (TextView) findViewById(R.id.fsLeft);
		fsLeft.setTextSize(Settings.TextSize);
		fsLeft.setHeight(h);
		TextView fsRight = (TextView) findViewById(R.id.fsRight);
		fsRight.setTextSize(Settings.TextSize);
		fsRight.setHeight(h);
		
		EditText terminal = (EditText) findViewById(R.id.terminal);
		terminal.setTextSize(Settings.TextSize);
		terminal.setHeight(h-Settings.getTextLineGap());
		terminal.setText("");
		terminal.append(Util.getTerminalPrompt());
		terminal.setSelectAllOnFocus(false);
		terminal.setSelection(Util.getTerminalPrompt().length());
		terminal.requestFocus();
		terminal.setOnKeyListener(this);
	
		TextView termOut = (TextView) findViewById(R.id.terminalOutputScrollable);
		termOut.setTextSize(Settings.TextSize);
		termOut.setMovementMethod(new ScrollingMovementMethod());
		
		Util.initTerminal(termOut);
		
		
//		LinearLayout header = (LinearLayout) findViewById(R.id.header);
//		header.getLayoutParams().height =  100;//Settings.getTextLineHeight()+10;
//		header.requestLayout();
		
		LinearLayout panels = (LinearLayout) findViewById(R.id.panels);
		LinearLayout header = (LinearLayout) findViewById(R.id.header);
		LinearLayout footer = (LinearLayout) findViewById(R.id.footer);
		
		header.getLayoutParams().height = Settings.getTextLineHeight()+Settings.getTextLineGap();

		footer.getLayoutParams().height = 2*(Settings.getTextLineHeight());

		Object o=panels.getLayoutParams();;
		RelativeLayout.LayoutParams lpp=(RelativeLayout.LayoutParams) o;
		lpp.setMargins(0, Settings.getTextLineHeight()+Settings.getTextLineGap(), 0, 0);
		panels.requestLayout();
		//
		int hh=Settings.getTextLineHeight()*2;
		
		ACListView left = (ACListView) findViewById(R.id.left);
		o=left.getLayoutParams();;
		LinearLayout.LayoutParams llpp=(LinearLayout.LayoutParams) o;
		llpp.setMargins(0, 0, 0, hh);
		left.requestLayout();

		ACListView right = (ACListView) findViewById(R.id.right);
		o=right.getLayoutParams();;
		llpp=(LinearLayout.LayoutParams) o;
		llpp.setMargins(0, 0, 0, hh);
		right.requestLayout();

		View center = (View) findViewById(R.id.center);
		o=center.getLayoutParams();;
		llpp=(LinearLayout.LayoutParams) o;
		llpp.setMargins(0, 0, 0, hh);
		center.requestLayout();

		View termOut2 = (View) findViewById(R.id.terminalOutput);
		o=termOut2.getLayoutParams();;
		RelativeLayout.LayoutParams llpp2=(RelativeLayout.LayoutParams) o;
		llpp2.setMargins(0, 0, 0, hh);
		termOut2.requestLayout();

		
		leftES = new RootStorage(this, this);
		rightES = new RootStorage(this, this);

		String[] list = leftES.list();
		leftPanel = (ACListView) findViewById(R.id.left);
		leftPanel.update(list, leftES.size(list));

		list = rightES.list();
		rightPanel = (ACListView) findViewById(R.id.right);
		rightPanel.update(list, rightES.size(list));
		
		TextView leftPanelTxt = (TextView) findViewById(R.id.dotLeft);
		leftPanelTxt.setText(leftES.path.getParent() == null ? leftES.path
				.getAbsolutePath() : (leftES.path.getAbsolutePath()
				+ File.separator + ".."));

		TextView rightPanelTxt = (TextView) findViewById(R.id.dotRight);

		rightPanelTxt.setText(rightES.path.getParent() == null ? rightES.path
				.getAbsolutePath() : (rightES.path.getAbsolutePath()
				+ File.separator + ".."));
	}

	
	
	public void onItemClick(ACListView arg0, int arg2) {
		String name = (String) arg0.files[arg2];
		String[] list;

		if (arg0.getId() == R.id.left) {
			
			if (!leftES.chdir(name)){
//				((FileAdapter)leftPanel.getAdapter()).toggleSelect(arg2);
//				leftPanel.invalidateViews();
				return;
			}

			list = leftES.list();

			if (list != null) {
				leftPanel.update(list, leftES.size(list));

				TextView leftPanelTxt = (TextView) findViewById(R.id.dotLeft);

				leftPanelTxt
						.setText(leftES.path.getParent() == null ? leftES.path
								.getAbsolutePath() : (leftES.path
								.getAbsolutePath() + File.separator + ".."));

			}
		} else if (arg0.getId() == R.id.right) {

			if (!rightES.chdir(name)){
//				((FileAdapter)rightPanel.getAdapter()).toggleSelect(arg2);
//				rightPanel.invalidateViews();
				return;
			}

			list = rightES.list();

			if (list != null) {
				rightPanel.update(list, rightES.size(list));

				TextView rightPanelTxt = (TextView) findViewById(R.id.dotRight);

				rightPanelTxt
						.setText(rightES.path.getParent() == null ? rightES.path
								.getAbsolutePath() : (rightES.path
								.getAbsolutePath() + File.separator + ".."));

			}
		}
		Log.i("", "" + name);
	}

	public void upClick(View view) {
		String[] list;
		if (view.getId() == R.id.dotLeft) {

			if (leftES.path.getParent() == null)
				return;

			if (!leftES.chdir(".."))
				return;
			
			list = leftES.list();
			if (list != null) {
				
				leftPanel.update(list, leftES.size(list));

				TextView leftPanelTxt = (TextView) findViewById(R.id.dotLeft);

				leftPanelTxt
						.setText(leftES.path.getParent() == null ? leftES.path
								.getAbsolutePath() : (leftES.path
								.getAbsolutePath() + File.separator + ".."));
			}
		} else {
			if (rightES.path.getParent() == null)
				return;

			if(!rightES.chdir(".."))
				return;
			
			list = rightES.list();

			if (list != null) {
				rightPanel.update(list, rightES.size(list));
				TextView rightPanelTxt = (TextView) findViewById(R.id.dotRight);

				rightPanelTxt
						.setText(rightES.path.getParent() == null ? rightES.path
								.getAbsolutePath() : (rightES.path
								.getAbsolutePath() + File.separator + ".."));
			}
		}

	}

	public void menuClick(View view) {
		Log.i("", "CLICK");

		//this runs text editor but now commented
//		try {
//
//			String str=Environment.getExternalStorageDirectory()+"/"+System.currentTimeMillis()+".tmp";
//			File f=new File(str);
//			f.createNewFile();
//			
//			Intent intent = new Intent();
//	           intent.setClass(AC.this, TextWarriorApplication.class);
//	           intent.setAction(Intent.ACTION_EDIT);
//	           intent.setData(Uri.parse(f.getAbsolutePath()));
//	           startActivity(intent);	
//
//		
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		
		 Dialog dialog = new Dialog(this, R.style.ActivityDialog);
		 
         dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
         dialog.setContentView(R.layout.dialog_copy);
         dialog.setCancelable(true);
         dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.RED));
         dialog.show();
		
     }

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}



	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		EditText term=((EditText)v);
		String str=term.getText().toString();

		if (event.getAction()== KeyEvent.ACTION_DOWN)
		{
		if (keyCode==KeyEvent.KEYCODE_ENTER){
			TextView termOut = (TextView) findViewById(R.id.terminalOutputScrollable);

			if (str.length()<=1){//just $ or # sign
				showTerminal();
				return true;
			}

			termOut.append(str+"\n");//append entered line
			
			try{
				Util.exec(str.substring(1));//exec command
			} catch (Exception e){
				termOut.append(e.getMessage()+"\n");
			}
			
			term.setText("");//this trick sets the cursor to the end
			term.append(Util.getTerminalPrompt());
			showTerminal();
			return true;
		}
		
		if (keyCode==KeyEvent.KEYCODE_DEL && str.length()==1){
			return true;
		}
		
		if (!str.startsWith("$")){
			term.setText("");//this trick sets the cursor to the end
			term.append("$"+str);
		}
		}
		return false;
	}

	
	public void iconClick(View view) {
		hideTerminal();
	}

	public void terminateClick(View view) {
		Util.terminate();
	}

	
	public void showTerminal(){
		
		LinearLayout panels = (LinearLayout) findViewById(R.id.panels);
		LinearLayout header = (LinearLayout) findViewById(R.id.header);
		LinearLayout term = (LinearLayout) findViewById(R.id.terminalOutput);
		View  div=findViewById(R.id.footerDivider);
		div.setVisibility(LinearLayout.INVISIBLE);
		
		panels.setVisibility(LinearLayout.INVISIBLE);
		header.setVisibility(LinearLayout.INVISIBLE);
		term.setVisibility(LinearLayout.VISIBLE);
	}

	public void hideTerminal(){
		
		LinearLayout panels = (LinearLayout) findViewById(R.id.panels);
		LinearLayout header = (LinearLayout) findViewById(R.id.header);
		LinearLayout term = (LinearLayout) findViewById(R.id.terminalOutput);
		View  div=findViewById(R.id.footerDivider);
		div.setVisibility(LinearLayout.VISIBLE);
		panels.setVisibility(LinearLayout.VISIBLE);
		header.setVisibility(LinearLayout.VISIBLE);
		term.setVisibility(LinearLayout.INVISIBLE);
	}

	
	
}