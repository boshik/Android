package com.ac;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class ACListView extends ScrollView{
	
	public Paint paint;
	public Paint bgpaint;
	public String[] files;
	public String[] size;
	public boolean[] selected;
	public TextPaint textPaint;
	public TextPaint sizePaint;
	public  Paint selectedPaint;	
	public LinearLayout layout;
	public View back;
	public Rect textBounds;
	public int textSize=33;
	
	public void update(String[] files, String[] size){
		this.files = files;
		this.size= size;
		this.selected = new boolean[files.length];
		back.setMinimumHeight((int) ((files.length)*getTextHeight()));
		this.updateViewLayout(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		invalidate();
	}
	
	public void toggleSelect(int i){
		selected[i]=!selected[i];
	}
	
	
	public ACListView(Context context, AttributeSet set) {
		super(context,set);

		paint = new Paint();

		this.setVerticalScrollBarEnabled(false);
		this.setFadingEdgeLength(0);
		
		textSize = Settings.TextSize;
		
        textPaint=new TextPaint();
        textPaint.setTextSize(textSize);
        textPaint.setTypeface(Typeface.MONOSPACE);
        textBounds = new Rect();
        textPaint.getTextBounds("A", 0, 1, textBounds);
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);

        sizePaint=new TextPaint();
        sizePaint.setTextSize(textSize);
        sizePaint.setTypeface(Typeface.MONOSPACE);
        sizePaint.setTextAlign(Align.RIGHT);
        sizePaint.setColor(Color.WHITE);
        sizePaint.setAntiAlias(true);
        
        selectedPaint=new Paint();
        selectedPaint.setColor(Color.rgb(0, 0, 128));

        bgpaint=new Paint();
        bgpaint.setColor(Color.rgb(0, 0, 255));

        layout=new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setFadingEdgeLength(0);
		back=new View(context);
		layout.addView(back);
		addView(layout);
	}


	private int getTextHeight(){
		return (int) (textBounds.height()+Settings.getTextLineGap());
	}
	
	@Override
	protected void onDraw(Canvas c) {
		c.drawColor(Color.rgb(0, 0, 255));
		
		if (files==null)
			return;
		
		float x=0,y=this.getScrollY();
		int start=(int) (getScrollY()/getTextHeight());
		for(int i=start>1?start-1:0;i<files.length;i++){
			y=i*getTextHeight()+getTextHeight();
			if(selected[i]){
				c.drawRect(x,y-getTextHeight()+Settings.getTextLineGap()/2,getWidth(),y+Settings.getTextLineGap()/2,selectedPaint);
			}
			c.drawText(files[i], x, y, textPaint);

			float w=sizePaint.measureText("  "+size[i]);
			if(selected[i]){
				c.drawRect(getWidth()-w,y-getTextHeight()+Settings.getTextLineGap()/2,getWidth(),y+Settings.getTextLineGap()/2,selectedPaint);
			} else {
				c.drawRect(getWidth()-w,y-getTextHeight()+Settings.getTextLineGap()/2,getWidth(),y+Settings.getTextLineGap()/2,bgpaint);
				
			}
			c.drawText(" "+size[i], getWidth(), y, sizePaint);

			if (y>this.getScrollY()+this.getHeight()+getTextHeight())break;
		}
	}
	
	
	float tX,tY;
	
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		if (e.getAction()==MotionEvent.ACTION_DOWN){
			tX = e.getX(); tY=e.getY();
			return true;
		}
		if (e.getAction()==MotionEvent.ACTION_UP && tX!=-1){
			double dist=Math.sqrt( (tX-e.getX())*(tX-e.getX())+(tY-e.getY())*(tY-e.getY())); 
			int idx=(int) ((e.getY()+getScrollY())/getTextHeight());
			idx=idx>=selected.length?selected.length-1:idx;

			if (dist>20 && Math.abs(tX-e.getX())>Math.abs(tY-e.getY())){
				
				selected[idx]=!selected[idx];
				invalidate();
				return false;
			} else {
				final ACListView listView = this;
				final int index = idx;
				//click
				if (dist<10){
					selected[idx]=!selected[idx];
					invalidate();
					this.postDelayed(new Runnable(){
						@Override
						public void run() {
							((AC)getContext()).onItemClick(listView, index);
						}}, 200);
				}
			}
			tX=-1;
		}
		return super.onTouchEvent(e);
	}
}
