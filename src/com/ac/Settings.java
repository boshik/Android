package com.ac;

import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;

public class Settings {
public static int TextSize = 33;
private static TextPaint textPaint;
private static Rect textBounds;

public static int getTextLineHeight(){
	if (textPaint==null){
	textPaint=new TextPaint();
    textPaint.setTextSize(TextSize);
    textPaint.setTypeface(Typeface.MONOSPACE);
    textBounds = new Rect();
    textPaint.getTextBounds("A", 0, 1, textBounds);
	}
	return textBounds.height()+getTextLineGap();
}

public static int getTextLineGap(){
	return 10;
}


}
