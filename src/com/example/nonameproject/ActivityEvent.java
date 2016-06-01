package com.example.nonameproject;

import java.io.InputStream;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.example.nonameproject.model.Event;
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
	private Button bCamera;
	private EditText etCat;
	private Button bGallery;
	private EditText etTitle;
	private Spinner spCat;
	private EditText etAddress;
	private EditText etDate;
	private EditText etDesc;
	private Context context;
	private Uri fileUri;
	private Bitmap bitmap;
	private Menu eventMenu;
	private boolean showEventMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event);
		context = this;

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
		etCat = (EditText) findViewById(R.id.etCat);

		/**
		 * CHECK IF IT'S A NEW EVENT OR VIEW EVENT. IF IT'S VIEW EVENT MODE THEN
		 * DISABLE ALL THE FIELDS.
		 */
		Intent intent = getIntent();
		showEventMode = intent.hasExtra("EXTRA_EVENT");
		if (showEventMode) {
			Event event = intent.getExtras().getParcelable("EXTRA_EVENT");
			ImageView ivImage = (ImageView) findViewById(R.id.ivEventImage);

			// SHOW EVENT ON CONTROLS
			new DownloadImageTask(ivImage).execute(event.getAttribute5());
			etTitle.setText(event.getAttribute1());
			getActionBar().setTitle(event.getAttribute1());
			etAddress.setText(event.getAttribute3());
			etDate.setText(event.getAttribute2());
			etDesc.setText(event.getEvent_desc());

			// DISABLE ALL CONTROLS
			disableControls();

			// BECAUSE I AM TOO LAZY TO DISPLAY CATEGORY ON A SPINNER, SUCKS.
			etCat.setVisibility(View.VISIBLE);
			etCat.setEnabled(false);
			etCat.setText(event.getAttribute4());

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

	private void hideMenuItems(Menu menu) {
		MenuItem saveMItem = menu.findItem(R.id.action_save_event);
		MenuItem cancelMItem = menu.findItem(R.id.action_cancel_event);

		saveMItem.setVisible(false);
		cancelMItem.setVisible(false);

		//invalidateOptionsMenu();
	}

	private void disableControls() {
		etTitle.setEnabled(false);
		etAddress.setEnabled(false);
		etDate.setEnabled(false);
		etDesc.setEnabled(false);
		spCat.setVisibility(View.GONE);

		// HIDE BUTTONS
		bCamera.setVisibility(View.GONE);
		bGallery.setVisibility(View.GONE);
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

		int scaleToUse = 40; // this will be our percentage

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
		waitDialog.setMessage("Submitting Event. Try # 1.");
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
					waitDialog.setMessage("Submitting Event, Try # " + count);
					logMessage("Uploading event: try # " + count);
					try {
						response = serverUtil.submitEvent(eventTitle, eventCat,
								address, date, description, imageString);

						if (response != null) {
							break;
						}
					} catch (Exception e) {
						logMessage("Error event submission: " + e.getMessage());
					}
				}

				/**
				 * IF MAX TRIES ARE ALSO DONE THEN SHOW ERROR TODO: SHOW THE
				 * ERROR MESSAGE INTO A DIALOG INSTEAD OF A LOG MESSAGE
				 */
				if (count == (MAX_TRIES + 1)) {
					errorDialog
							.setMessage("SERVER ISN'T RESPONDING, TRY AGAIN.");
					logMessage("Server isn't responding or your internet is not on");
				}
				return response;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
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
						waitDialog.cancel();
						displayMessage("EVENT SUBMITTED SUCCESSFULLY");
						finish();
					} else {
						waitDialog.cancel();
						errorDialog
								.setMessage("SOMETHING WENT WRONG, TRY AGAIN.");
						errorDialog.show();
						displayMessage("Something went wrong");
					}
				}
			}
		}.execute();
	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

		ImageView imageView;

		public DownloadImageTask(ImageView imageView) {
			this.imageView = imageView;
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
			logMessage("downloading image . . . " + imageName);
			String urldisplay = ApplicationClass.BASE_URL + "/" + params[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
				system.saveBitmap(mIcon11, imageName);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			imageView.setImageBitmap(result);
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
			hideMenuItems(menu);
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
		}else if(id == android.R.id.home){
			NavUtils.navigateUpFromSameTask(this);
			
		}
		return super.onOptionsItemSelected(item);
	}
}
