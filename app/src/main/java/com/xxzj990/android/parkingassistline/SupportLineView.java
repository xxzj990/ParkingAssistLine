package com.xxzj990.android.parkingassistline;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

/**
 * 倒车辅助线
 * 
 * 
 * @todo 
 * 	1、触摸超出范围
 *  2、保存设置记录
 * @author leilei.liu
 *
 */
public class SupportLineView extends View {

	private static final String TAG = "SupportLineView";
	
	private static final int LINE_WIDTH = 5;
	private static int POINT_HINT_RADIUS = 0;

	public enum Mode {
		VIEW, EDIT
	}

	private static final String SUPPORT_LINE = "support_line";
	private SharedPreferences sp;
	private static final String KEY_P1X = "p1x", KEY_P1Y = "p1y",
			KEY_P2X = "p2x", KEY_P2Y = "p2y", KEY_P3X = "p3x", KEY_P3Y = "p3y",
			KEY_P4X = "p4x", KEY_P4Y = "p4y", KEY_P5X = "p5x", KEY_P5Y = "p5y",
			KEY_P6X = "p6x", KEY_P6Y = "p6y", KEY_P7X = "p7x", KEY_P7Y = "p7y",
			KEY_P8X = "p8x", KEY_P8Y = "p8y";
	
	
	private Paint paint;

	private int[] point1 = { 170, 350 };
	private int[] point2 = { 225, 250 };
	private int[] point3 = { 275, 150 };
	private int[] point4 = { 330, 50 };

	private int[] point5 = { 650, 50 };
	private int[] point6 = { 705, 150 };
	private int[] point7 = { 755, 250 };
	private int[] point8 = { 810, 350 };

	private Mode mode = Mode.VIEW;
	private Bitmap moveIcon = null;
	private int actionPoint = -1;
	
	// Outer
	private int startX = 0;
	private int startY = 0;
	private int endX = 0;
	private int endY = 0;

	public SupportLineView(Context context) {
		super(context);
		init(context);
	}

	public SupportLineView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SupportLineView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public void setMode(Mode mode) {
		this.mode = mode;
		invalidate();
	}
	
	public Mode getMode() {
		return this.mode;
	}

	private void init(Context context) {
		sp = context.getSharedPreferences(SUPPORT_LINE, Context.MODE_PRIVATE);
		
		paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);// 空心
		paint.setAntiAlias(true); // 抗锯齿
		paint.setStrokeWidth(LINE_WIDTH); // 粗细

		moveIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_move);
		POINT_HINT_RADIUS = moveIcon.getWidth()/2;
		
		getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				Log.d(TAG, "Out point ready.");
				getViewTreeObserver().removeGlobalOnLayoutListener(this);

				int height = getMeasuredHeight();
				int width = getMeasuredWidth();
				startX = (int) getX() + POINT_HINT_RADIUS;
				startY = (int) getY() + POINT_HINT_RADIUS;
				endX = startX + width - 2*POINT_HINT_RADIUS;
				endY = startY + height- 2*POINT_HINT_RADIUS;
			}
		});
		
		// Read data
		readData();
	}
	
	public void readData() {
		point1[0] = sp.getInt(KEY_P1X, point1[0]);
		point1[1] = sp.getInt(KEY_P1Y, point1[1]);
		
		point2[0] = sp.getInt(KEY_P2X, point2[0]);
		point2[1] = sp.getInt(KEY_P2Y, point2[1]);
		
		point3[0] = sp.getInt(KEY_P3X, point3[0]);
		point3[1] = sp.getInt(KEY_P3Y, point3[1]);
		
		point4[0] = sp.getInt(KEY_P4X, point4[0]);
		point4[1] = sp.getInt(KEY_P4Y, point4[1]);
		
		point5[0] = sp.getInt(KEY_P5X, point5[0]);
		point5[1] = sp.getInt(KEY_P5Y, point5[1]);
		
		point6[0] = sp.getInt(KEY_P6X, point6[0]);
		point6[1] = sp.getInt(KEY_P6Y, point6[1]);
		
		point7[0] = sp.getInt(KEY_P7X, point7[0]);
		point7[1] = sp.getInt(KEY_P7Y, point7[1]);
		
		point8[0] = sp.getInt(KEY_P8X, point8[0]);
		point8[1] = sp.getInt(KEY_P8Y, point8[1]);
	}
	
	public void saveData() {
		Editor editor = sp.edit();
		editor.putInt(KEY_P1X, point1[0]);
		editor.putInt(KEY_P1Y, point1[1]);
		
		editor.putInt(KEY_P2X, point2[0]);
		editor.putInt(KEY_P2Y, point2[1]);
		
		editor.putInt(KEY_P3X, point3[0]);
		editor.putInt(KEY_P3Y, point3[1]);
		
		editor.putInt(KEY_P4X, point4[0]);
		editor.putInt(KEY_P4Y, point4[1]);
		
		editor.putInt(KEY_P5X, point5[0]);
		editor.putInt(KEY_P5Y, point5[1]);
		
		editor.putInt(KEY_P6X, point6[0]);
		editor.putInt(KEY_P6Y, point6[1]);
		
		editor.putInt(KEY_P7X, point7[0]);
		editor.putInt(KEY_P7Y, point7[1]);
		
		editor.putInt(KEY_P8X, point8[0]);
		editor.putInt(KEY_P8Y, point8[1]);
		editor.commit();
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		/**
		 * 4 —— 5 
		 * |    | 
		 * 3 -- 6 
		 * |    | 
		 * 2 -- 7 
		 * |    | 
		 * 1    8
		 */

		paint.setPathEffect(null);

		// Line 3-4 5-6
		paint.setColor(Color.GREEN);
		Path path = new Path();
		path.moveTo(point3[0], point3[1]); // 3
		path.lineTo(point4[0], point4[1]); // 4
		path.moveTo(point5[0], point5[1]); // 5
		path.lineTo(point6[0], point6[1]); // 6
		canvas.drawPath(path, paint);

		// Line 2-3 6-7
		paint.setColor(Color.YELLOW);
		path = new Path();
		path.moveTo(point2[0], point2[1]); // 2
		path.lineTo(point3[0], point3[1]); // 3
		path.moveTo(point6[0], point6[1]); // 6
		path.lineTo(point7[0], point7[1]); // 7
		canvas.drawPath(path, paint);

		// Line 1-2 7-8
		paint.setColor(Color.RED);
		path = new Path();
		path.moveTo(point1[0], point1[1]); // 1
		path.lineTo(point2[0], point2[1]); // 2
		path.moveTo(point7[0], point7[1]); // 7
		path.lineTo(point8[0], point8[1]); // 8
		canvas.drawPath(path, paint);

		// 4——5
		path = new Path();
		paint.setColor(Color.GREEN);
		path.moveTo(point4[0], point4[1]); // 4
		path.lineTo(point5[0], point5[1]); // 5
		canvas.drawPath(path, paint);

		PathEffect effect = new DashPathEffect(new float[] { 14, 14, 14, 14 }, 1); // {实线,空白,实线,空白:偶数}
		paint.setPathEffect(effect);
		
		// 3--6
		path = new Path();
		paint.setColor(Color.YELLOW);
		path.moveTo(point3[0], point3[1]); // 3
		path.lineTo(point6[0], point6[1]); // 6
		canvas.drawPath(path, paint);

		// 2--7
		path = new Path();
		paint.setColor(Color.RED);
		path.moveTo(point2[0], point2[1]); // 2
		path.lineTo(point7[0], point7[1]); // 7
		canvas.drawPath(path, paint);

		path = null;
		paint.setPathEffect(null);

		if (mode == Mode.EDIT) {
			canvas.drawBitmap(moveIcon, point1[0] - POINT_HINT_RADIUS, point1[1] - POINT_HINT_RADIUS, paint);
			canvas.drawBitmap(moveIcon, point2[0] - POINT_HINT_RADIUS, point2[1] - POINT_HINT_RADIUS, paint);
			canvas.drawBitmap(moveIcon, point3[0] - POINT_HINT_RADIUS, point3[1] - POINT_HINT_RADIUS, paint);
			canvas.drawBitmap(moveIcon, point4[0] - POINT_HINT_RADIUS, point4[1] - POINT_HINT_RADIUS, paint);
			canvas.drawBitmap(moveIcon, point5[0] - POINT_HINT_RADIUS, point5[1] - POINT_HINT_RADIUS, paint);
			canvas.drawBitmap(moveIcon, point6[0] - POINT_HINT_RADIUS, point6[1] - POINT_HINT_RADIUS, paint);
			canvas.drawBitmap(moveIcon, point7[0] - POINT_HINT_RADIUS, point7[1] - POINT_HINT_RADIUS, paint);
			canvas.drawBitmap(moveIcon, point8[0] - POINT_HINT_RADIUS, point8[1] - POINT_HINT_RADIUS, paint);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mode == Mode.VIEW) {
			Log.d(TAG, "View mode.");
			return false;
		}
		
		if(event.getAction()==MotionEvent.ACTION_MOVE) {
			float x = event.getX();
			float y = event.getY();
			if(x<=startX || x>=endX || y<=startY || y>=endY) {
				Log.d(TAG, "Out touch.startX=" + startX + ";endX=" + endX + ";startY=" + startY + ";endY=" + endY);
				return true;
			}
			
			if(actionPoint>-1) {// 已经选中
				// 1、4、5、8移动实线
				// 2、3、6、7移动虚线
				switch (actionPoint) {
				case 1:
					point1[0] = (int) x;
					if (y > endY) {  
				        point1[1] = endY;  
				    } else if (y > point2[1] + POINT_HINT_RADIUS * 2 && y < endY) {  
				        point1[1] = (int) y;  
				    } else {  
				        point1[1] = point2[1] + POINT_HINT_RADIUS * 2;  
				    }
					
					point2[0] = (point2[1] - point1[1]) * (point1[0] - point4[0])  
					        / (point1[1] - point4[1]) + point1[0];  
					point3[0] = (point3[1] - point1[1]) * (point1[0] - point4[0])  
					        / (point1[1] - point4[1]) + point1[0];  
					break;
				case 4:
					point4[0] = (int) x;
					if (y > point3[1] - POINT_HINT_RADIUS * 2) {  
						point4[1] = point3[1] - POINT_HINT_RADIUS * 2;  
				    } else if (y > startY && y < point3[1]) {  
				    	point4[1] = (int) y;  
				    } else {  
				    	point4[1] = startY;  
				    }
					
					point2[0] = (point2[1] - point1[1]) * (point1[0] - point4[0])  
					        / (point1[1] - point4[1]) + point1[0];  
					point3[0] = (point3[1] - point1[1]) * (point1[0] - point4[0])  
					        / (point1[1] - point4[1]) + point1[0]; 
					break;
				case 5:
					point5[0] = (int) x;
					if (y > point6[1] - POINT_HINT_RADIUS * 2) {  
						point5[1] = point6[1] - POINT_HINT_RADIUS * 2;  
				    } else if (y > startY && y < point6[1]) {  
				    	point5[1] = (int) y;  
				    } else {  
				    	point5[1] = startY;  
				    }
					
					point6[0] = (point6[1] - point5[1]) * (point5[0] - point8[0])  
					        / (point5[1] - point8[1]) + point5[0];  
					point7[0] = (point7[1] - point5[1]) * (point5[0] - point8[0])  
					        / (point5[1] - point8[1]) + point5[0];
					break;
				case 8:
					point8[0] = (int) x;
					if (y > endY) {  
						point8[1] = endY;  
				    } else if (y > point7[1] + POINT_HINT_RADIUS * 2 && y < endY) {  
				    	point8[1] = (int) y;  
				    } else {  
				    	point8[1] = point7[1] + POINT_HINT_RADIUS * 2;  
				    }
					
					point6[0] = (point6[1] - point5[1]) * (point5[0] - point8[0])  
					        / (point5[1] - point8[1]) + point5[0];  
					point7[0] = (point7[1] - point5[1]) * (point5[0] - point8[0])  
					        / (point5[1] - point8[1]) + point5[0];
					break;
				case 2:
					if (y > point1[1] - POINT_HINT_RADIUS * 2) {  
				        point2[1] = point1[1] - POINT_HINT_RADIUS * 2;  
				    } else if (y > point3[1] + POINT_HINT_RADIUS * 2 && y < point1[1]) {  
				        point2[1] = (int) y;  
				    } else {  
				        point2[1] = point3[1] + POINT_HINT_RADIUS * 2;  
				    }
					point2[0] = (point2[1] - point1[1]) * (point1[0] - point4[0])  
					        / (point1[1] - point4[1]) + point1[0]; 
					break;
				case 3:
					if (y > point2[1] - POINT_HINT_RADIUS * 2) {  
				        point3[1] = point2[1] - POINT_HINT_RADIUS * 2;  
				    } else if (y > point4[1] + POINT_HINT_RADIUS * 2 && y < point2[1]) {  
				        point3[1] = (int) y;  
				    } else {  
				        point3[1] = point4[1] + POINT_HINT_RADIUS * 2;  
				    }
					point3[0] = (point3[1] - point1[1]) * (point1[0] - point4[0])  
					        / (point1[1] - point4[1]) + point1[0]; 
					break;
				case 6:
					if (y > point7[1] - POINT_HINT_RADIUS * 2) {  
				        point6[1] = point7[1] - POINT_HINT_RADIUS * 2;  
				    } else if (y > point5[1] + POINT_HINT_RADIUS * 2 && y < point7[1]) {  
				        point6[1] = (int) y;  
				    } else {  
				        point6[1] = point5[1] + POINT_HINT_RADIUS * 2;  
				    }
					
					point6[0] = (point6[1] - point5[1]) * (point5[0] - point8[0])  
					        / (point5[1] - point8[1]) + point5[0];
					break;
				case 7:
					if (y > point8[1] - POINT_HINT_RADIUS * 2) {  
				        point7[1] = point8[1] - POINT_HINT_RADIUS * 2;  
				    } else if (y > point6[1] + POINT_HINT_RADIUS * 2 && y < point8[1]) {  
				        point7[1] = (int) y;  
				    } else {  
				        point7[1] = point6[1] + POINT_HINT_RADIUS * 2;  
				    }
					
					point7[0] = (point7[1] - point5[1]) * (point5[0] - point8[0])  
					        / (point5[1] - point8[1]) + point5[0];
					break;
				default:
					break;
				}
				invalidate();
			} else {
				// 1、4、5、8
				if(x>point1[0]-POINT_HINT_RADIUS && x<point1[0]+POINT_HINT_RADIUS
						&& y>point1[1]-POINT_HINT_RADIUS && y<point1[1]+POINT_HINT_RADIUS) {
					actionPoint = 1;
				} else if(x>point4[0]-POINT_HINT_RADIUS && x<point4[0]+POINT_HINT_RADIUS
						&& y>point4[1]-POINT_HINT_RADIUS && y<point4[1]+POINT_HINT_RADIUS) {
					actionPoint = 4;
				} else if(x>point5[0]-POINT_HINT_RADIUS && x<point5[0]+POINT_HINT_RADIUS
						&& y>point5[1]-POINT_HINT_RADIUS && y<point5[1]+POINT_HINT_RADIUS) {
					actionPoint = 5;
				} else if(x>point8[0]-POINT_HINT_RADIUS && x<point8[0]+POINT_HINT_RADIUS
						&& y>point8[1]-POINT_HINT_RADIUS && y<point8[1]+POINT_HINT_RADIUS) {
					actionPoint = 8;
				}
				
				// 2、3、6、7
				else if(x>point2[0]-POINT_HINT_RADIUS && x<point2[0]+POINT_HINT_RADIUS
						&& y>point2[1]-POINT_HINT_RADIUS && y<point2[1]+POINT_HINT_RADIUS) {
					actionPoint = 2;
				} else if(x>point3[0]-POINT_HINT_RADIUS && x<point3[0]+POINT_HINT_RADIUS
						&& y>point3[1]-POINT_HINT_RADIUS && y<point3[1]+POINT_HINT_RADIUS) {
					actionPoint = 3;
				} else if(x>point6[0]-POINT_HINT_RADIUS && x<point6[0]+POINT_HINT_RADIUS
						&& y>point6[1]-POINT_HINT_RADIUS && y<point6[1]+POINT_HINT_RADIUS) {
					actionPoint = 6;
				} else if(x>point7[0]-POINT_HINT_RADIUS && x<point7[0]+POINT_HINT_RADIUS
						&& y>point7[1]-POINT_HINT_RADIUS && y<point7[1]+POINT_HINT_RADIUS) {
					actionPoint = 7;
				}
			}
		} else if(event.getAction() == MotionEvent.ACTION_UP) {
			actionPoint = -1;
		} else if(event.getAction() == MotionEvent.ACTION_DOWN) {
			performClick();
		}
		return true;
	}
	
	@Override
	public boolean performClick() {
		return super.performClick();
	}

}
