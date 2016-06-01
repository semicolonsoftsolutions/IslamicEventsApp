package com.example.nonameproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

public class ActivitySplash extends Activity{
	private static final int WAIT_TIME = 5000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_splash);
		
		
		if (showSplash()){
			showRandomHadeeth();
			waitAndFinish(WAIT_TIME);
		}else{
			startActivity(new Intent(ActivitySplash.this,ActivityNotifications.class));
			finish();
		}
		
	}
	
	/**
	 * TODO: RETURN BOOLEAN SAVED IN PREFS
	 * @return
	 */
	private boolean showSplash(){
		return true;
	}
	private void showRandomHadeeth(){
		String fontFileName = "arabic_font.ttf";
		Typeface tf = Typeface.createFromAsset(getAssets(), fontFileName);
		TextView tvHadeeth = (TextView) findViewById(R.id.tvHadeethSplash);
		// Apply the font to your TextView object
		tvHadeeth.setTypeface(tf);
		tvHadeeth.setText("الْإِسْلَامُ أَنْ تَشْهَدَ أَنْ لَا إِلَهَ إِلَّا اللَّهُ وَأَنَّ مُحَمَّدًا رَسُولُ اللَّهِ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ وَتُقِيمَ الصَّلَاةَ وَتُؤْتِيَ الزَّكَاةَ وَتَصُومَ رَمَضَانَ وَتَحُجَّ الْبَيْتَ إِنْ اسْتَطَعْتَ إِلَيْهِ سَبِيلًا");
	}
	private void waitAndFinish(final int time){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try{
					Thread.sleep(time);
				}catch(Exception ex){
					
				}finally{
					startActivity(new Intent(ActivitySplash.this,ActivityNotifications.class));
					finish();
				}
				
			}
		}).start();
	}

}
