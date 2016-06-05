package com.example.nonameproject;

import java.io.InputStream;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.nonameproject.model.Event;
import com.example.nonameproject.util.Database;
import com.example.nonameproject.util.DialogFactory;
import com.example.nonameproject.util.DialogType;
import com.example.nonameproject.util.Json;
import com.example.nonameproject.util.ServerUtil;
import com.example.nonameproject.util.StringUtil;
import com.example.nonameproject.util.System;

public class ActivityEvent extends ActivityMaster {
	protected static final int CAPTURE_IMAGE = 0;
	public static final int MEDIA_TYPE_IMAGE = 0;
	private static final int MAX_TRIES = 5;
	/* EDIT/NEW MODE CONTROL */
	private Button bCamera;
	private EditText etCat;
	private Button bGallery;
	private EditText etTitle;
	private Spinner spCat;
	private EditText etAddress;
	private EditText etDate;
	private EditText etDesc;

	/* VIEW MODE CONTROLS */
	private TextView tvCat;
	private TextView tvTitle;
	private TextView tvAddress;
	private TextView tvDate;
	private TextView tvDesc;

	private Context context;
	private Uri fileUri;
	private Bitmap bitmap;
	private Menu eventMenu;
	private boolean showEventMode;
	private boolean bannerDownloading;
	private DownloadImageTask downloadTask;
	private Event selectedEvent;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event);
		context = this;
		getActionBar().setDisplayHomeAsUpEnabled(true);

		/**
		 * MAKE UI REFERENCES
		 */
		bCamera = (Button) findViewById(R.id.captureImage);
		bGallery = (Button) findViewById(R.id.openGallery);
		etTitle = (EditText) findViewById(R.id.etTitle);
		spCat = (Spinner) findViewById(R.id.spCat);
		etAddress = (EditText) findViewById(R.id.etAddress);
		etDate = (EditText) findViewById(R.id.etDate);
		etDesc = (EditText) findViewById(R.id.etDesc);

		tvCat = (TextView) findViewById(R.id.tvCat);
		tvAddress = (TextView) findViewById(R.id.tvAddress);
		tvDate = (TextView) findViewById(R.id.tvDate);
		tvDesc = (TextView) findViewById(R.id.tvDescription);
		tvTitle = (TextView) findViewById(R.id.tvTitle);

		/**
		 * CHECK IF IT'S A NEW EVENT OR VIEW EVENT. IF IT'S VIEW EVENT MODE THEN
		 * DISABLE ALL THE FIELDS.
		 */
		Intent intent = getIntent();
		showEventMode = intent.hasExtra("EXTRA_EVENT");
		if (showEventMode) {
			selectedEvent = intent.getExtras().getParcelable("EXTRA_EVENT");
			ImageView ivImage = (ImageView) findViewById(R.id.ivEventImage);

			// SHOW EVENT ON CONTROLS
			downloadTask = new DownloadImageTask(ivImage);
			downloadTask.execute(selectedEvent.getAttribute5());
			getActionBar().setTitle(selectedEvent.getAttribute1());
			tvTitle.setText(selectedEvent.getAttribute1());
			tvAddress.setText(selectedEvent.getAttribute3());
			tvDate.setText(selectedEvent.getAttribute2());
			tvDesc.setText(selectedEvent.getEvent_desc());
			tvCat.setText(selectedEvent.getAttribute4());

			// SWITCH ALL CONTROLS
			switchControls();

		}

		/*** HIDE SOFT KEYBOARD ***/
		System.getInstance(this).hideSoftKeyboard(this);

		/*** FILL SPINNER WITH VALUES ***/
		fillSpinner();

		/** SHOW DATE PICKER DIALOG WHEN DATA FIELD IS CLICKED **/
		setUpDateTimePicker();

		/** TODO: OPEN CAMERA TO TAKE PICTURE **/
		setUpCameraAction();

		/** TODO: OPEN GALLERY TO SELECT PICTURE **/
	}


	private void switchMenuItems(Menu menu) {
		MenuItem saveMItem = menu.findItem(R.id.action_save_event);
		MenuItem cancelMItem = menu.findItem(R.id.action_cancel_event);
		MenuItem moreIMItem = menu.findItem(R.id.action_w);

		saveMItem.setVisible(false);
		cancelMItem.setVisible(false);
		moreIMItem.setVisible(true);

		// invalidateOptionsMenu();
	}

	private void switchControls() {
		etTitle.setVisibility(View.GONE);
		etAddress.setVisibility(View.GONE);
		etDate.setVisibility(View.GONE);
		etDesc.setVisibility(View.GONE);
		spCat.setVisibility(View.GONE);
		// HIDE BUTTONS
		bCamera.setVisibility(View.GONE);
		bGallery.setVisibility(View.GONE);

		tvTitle.setVisibility(View.VISIBLE);
		tvAddress.setVisibility(View.VISIBLE);
		tvDate.setVisibility(View.VISIBLE);
		tvDesc.setVisibility(View.VISIBLE);
		tvCat.setVisibility(View.VISIBLE);

	}

	private void setUpCameraAction() {
		bCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				captureImage();
			}
		});
	}

	private void setUpDateTimePicker() {
		etDate.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					final Dialog dialog = DialogFactory.createDialog(
							DialogType.DIALOG_DATETIME, context);
					Button bOk = (Button) dialog
							.findViewById(R.id.bOkayDateTime);
					final DatePicker dPicker = (DatePicker) dialog
							.findViewById(R.id.datePicker);
					final TimePicker tPicker = (TimePicker) dialog
							.findViewById(R.id.timePicker);
					bOk.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							int year = dPicker.getYear();
							// dPicker month starts from 0 and so does
							// Calendar's
							int month = dPicker.getMonth() + 1;
							int day = dPicker.getDayOfMonth();

							int hour = tPicker.getCurrentHour();
							int min = tPicker.getCurrentMinute();
							String ampm = hour > 12 ? "PM" : "AM";

							String date = month + "/" + day + "/" + year + " "
									+ (hour % 12) + ":" + min + " " + ampm;
							etDate.setText(date);
							dialog.cancel();
						}
					});
					dialog.show();
				}

				return false;
			}
		});
	}

	private void fillSpinner() {
		String[] cats = getCategories();
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_dropdown_item, cats);
		spCat.setAdapter(spinnerAdapter);
	}

	/**
	 * TODO: BELOW VALUES ARE DUMMY, REPLACE THEM WITH ORIGINAL VALUES
	 * 
	 * @return String[]
	 */
	private String[] getCategories() {
		String cats[] = new String[5];
		cats[0] = "CAT-A";
		cats[1] = "CAT-B";
		cats[2] = "CAT-C";
		cats[3] = "CAT-D";
		cats[4] = "CAT-E";
		return cats;

	}

	/**
	 * I GOT THIS CODE FROM THE INTERNET, SOME THREAD ON STACK OVERFLOW. I DON'T
	 * KNOW HOW BELOW CODE WORKS BUT IT DOES WORK.
	 * 
	 * @param bm
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	public Bitmap getResizedBitmap(Bitmap bm) {

		int scaleToUse = 30; // this will be our percentage

		/**
		 * GET SCREEN RESOLUTION
		 */
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		int sizeY = size.y * scaleToUse / 100;
		int sizeX = bm.getWidth() * sizeY / bm.getHeight();
		Bitmap scaled = Bitmap.createScaledBitmap(bm, sizeX, sizeY, false);
		return scaled;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {

			bitmap = BitmapFactory.decodeFile(fileUri.getPath());
			bitmap = getResizedBitmap(bitmap);
			ImageView iv = (ImageView) findViewById(R.id.ivEventImage);
			iv.setImageBitmap(bitmap);

			/**
			 * TODO: SHOW BITMAP IN A PREVIEW AND CROP IF I HAD ANY REQ LIKE
			 * THAT
			 */

		}
	}

	private void captureImage() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		fileUri = System.getInstance(context).getOutputMediaFileUri(
				MEDIA_TYPE_IMAGE);

		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

		// start the image capture Intent
		startActivityForResult(intent, CAPTURE_IMAGE);
	}

	private void submitEventInBackground() {
		/**
		 * TODO: FIELD VALIDATION. NONE SHOULD BE EMPTY
		 */

		/**
		 * DIALOGS THAT'LL BE USED IN THIS TASK
		 */
		final ProgressDialog waitDialog = (ProgressDialog) DialogFactory
				.createDialog(DialogType.DIALOG_WAIT, context);
		waitDialog.setTitle("Please wait!");
		waitDialog.setMessage("Uploading. . . .");
		waitDialog.setIndeterminate(true);

		final AlertDialog errorDialog = (AlertDialog) DialogFactory
				.createDialog(DialogType.DIALOG_ERROR, context);
		errorDialog.setTitle("Error");
		errorDialog.setMessage("Check your internet connection and try again!");

		final String eventTitle = etTitle.getText().toString();
		final String eventCat = spCat.getSelectedItem().toString();
		final String address = etAddress.getText().toString();
		final String date = etDate.getText().toString();
		final String description = etDesc.getText().toString();
		final String imageString = StringUtil.bitmapToBase64(bitmap);

		new AsyncTask<Void, Void, String>() {

			protected void onPreExecute() {
				waitDialog.show();
			};

			@Override
			protected String doInBackground(Void... params) {
				ServerUtil serverUtil = new ServerUtil();
				int count = 1;
				String response = null;

				for (count = 1; count <= MAX_TRIES; count++) {

					logMessage("Uploading event: try # " + count);
					try {
						response = serverUtil.submitEvent(eventTitle, eventCat,
								address, date, description, imageString);

						if (response != null) {
							break;
						}
					} catch (Exception e) {
						logMessage("Error event submission: " + e.getMessage());
						try{
							Thread.sleep(1000*1);
						}catch (Exception ie) {}
						
					}
				}

				waitDialog.cancel();

				/**
				 * IF MAX TRIES ARE ALSO DONE THEN SHOW ERROR
				 */
				if (count == (MAX_TRIES + 1)) {

					ActivityEvent.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							errorDialog
									.setMessage("SERVER ISN'T RESPONDING, TRY AGAIN.");
							errorDialog.show();
						}
					});

					logMessage("Server isn't responding or your internet is not on");
				}

				// this is just for testing.
				final String message = response;
				ActivityEvent.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						displayMessage("" + message);
					}
				});
				return response;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				waitDialog.cancel();
				if (result != null) {
					StringUtil strUtil = new StringUtil();
					String jsonResult = strUtil.parseJsonString(result);
					String successUpload = "";
					try {
						Json json = new Json(jsonResult);
						successUpload = json.getAttribute("success");
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (successUpload.trim().equals("1")) {

						displayMessage("EVENT SUBMITTED SUCCESSFULLY");
						finish();
					} else {
						errorDialog
								.setMessage("SOMETHING WENT WRONG, TRY AGAIN.");
						errorDialog.show();
					}
				} else {
					displayMessage("NO RESPONSE . . TRY AGAIN");
					finish();
				}
			}
		}.execute();
	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

		ImageView imageView;
		/**
		 * DIALOGS THAT'LL BE USED IN THIS TASK
		 */
		final ProgressDialog waitDialog = (ProgressDialog) DialogFactory
				.createDialog(DialogType.DIALOG_WAIT, context);

		public DownloadImageTask(ImageView imageView) {
			this.imageView = imageView;
			waitDialog.setTitle("Please wait!");
			waitDialog.setMessage("Downloading Banner.");
			waitDialog.setIndeterminate(true);
			waitDialog.setCancelable(false);
			bannerDownloading = true;
		}

		@Override
		protected Bitmap doInBackground(String... params) {

			String imageName = params[0].split("/")[1];
			System system = System.getInstance(context);

			// IF IMAGE EXISTS IN LOCAL STORAGE THEN GET IT FROM THERE
			if (system.fileExists(imageName)) {
				logMessage("image exists in local storage");
				try {
					return system.getLocalBitmap(imageName);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}

			}

			// IF THE IMAGE IS NOT IN LOCAL STORAGE THEN DOWNLOAD IT FROM REMOTE
			// AND SAVE IN LOCAL STORAGE
			// TODO: CAN'T DOWNLOAD IMAGES WITH NAME WITH SPACES. TAKE CARE OF
			// IT.
			logMessage("downloading image . . . "
					+ imageName.trim().replace(" ", "%20"));
			String urldisplay = ApplicationClass.BASE_URL + "/"
					+ params[0].trim().replace(" ", "%20");
			Bitmap mIcon11 = null;
			int count = 1;
			for (count = 1; count <= MAX_TRIES;count++) {
				try {
					InputStream in = new java.net.URL(urldisplay).openStream();
					mIcon11 = BitmapFactory.decodeStream(in);
					if (mIcon11 != null) {
						system.saveBitmap(mIcon11, imageName);
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
					try{
						Thread.sleep(1000*1);
					}catch (Exception ie) {}
				}
			}

			if (count == (MAX_TRIES + 1)) {
				logMessage("Can't download image . . . . ");
				// SHOW A BUTTON BY PRESSING WHICH THE USER WILL START IMAGE
				// DOWNLOADING AGAIN.
			}

			return mIcon11;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			waitDialog.cancel();
			if (result != null) {
				imageView.setImageBitmap(result);
			}

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			waitDialog.show();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.activity_event, menu);

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (showEventMode) {
			switchMenuItems(menu);
			
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_save_event) {
			submitEventInBackground();
			return true;
		} else if (id == R.id.action_cancel_event) {

			return true;
		} else if (id == android.R.id.home) {
			NavUtils.navigateUpFromSameTask(this);

		}else if (id == R.id.action_w){
			showMoreOptions();
		}
		return super.onOptionsItemSelected(item);
	}
	private void showMoreOptions(){
		CharSequence[] items = {"Add To Calendar", "Share","Remove"};
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setItems(items, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int item) {

	            if(item == 0) {
	            	addEventToCalendar(selectedEvent);
	            } else if(item == 1) {

	            } else if(item == 2) {
	            	deleteEvent(selectedEvent);
	            }
	        }
	    });

	     AlertDialog dialog = builder.create();
	     dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	     WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();

	 wmlp.gravity = Gravity.TOP | Gravity.RIGHT;
	 //wmlp.x = 100;   //x position
	 wmlp.y = 100;//y position

	 dialog.show();
	}
	private void deleteEvent(Event e){
		Database database = new Database(context);
		long numRows = database.deleteEvent(e);
		if (numRows > 0) {
			displayMessage("Event Removed.");
			finish();
		}else{
			displayMessage("Error Removing Event.");
		}
	}
	
	//TODO: ADD EVENT DATE TO CALENDAR EVENT
	private void addEventToCalendar(Event e){
		Intent intent = new Intent(Intent.ACTION_EDIT);
		intent.setType("vnd.android.cursor.item/event");
		intent.putExtra(Events.TITLE, e.getAttribute1());
		intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
		                    java.lang.System.currentTimeMillis());
		intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
		                    java.lang.System.currentTimeMillis());
		intent.putExtra(Events.ALL_DAY, false);// periodicity
		            intent.putExtra(Events.DESCRIPTION,e.getEvent_desc());
		            startActivity(intent);
	}
}
