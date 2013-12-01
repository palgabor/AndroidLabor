package hu.bute.daai.amorg.xg5ort.androidlabor10.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class DrawerImageView extends ImageView {

	private ArrayList<PointF> touchPoints = new ArrayList<PointF>();;
	private Paint mPaint;
	
	public DrawerImageView(Context context, AttributeSet attrs) {
		super(context,attrs);
		setDrawingCacheEnabled(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(0x44FF0000);
		
		for (int i=0; i<touchPoints.size(); i++)
		{
			canvas.drawCircle(touchPoints.get(i).x, touchPoints.get(i).y, 5, mPaint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			touchPoints.add(new PointF(event.getX(), event.getY()));
		}
		this.invalidate();
		return super.onTouchEvent(event);
	}
	
	public void setColor(int color)
	{
		if(mPaint != null)
		{
			mPaint.setColor(color);
		}
	}
	
	public void clearDrawing()
	{
		touchPoints.clear();
		this.invalidate();
	}
} 