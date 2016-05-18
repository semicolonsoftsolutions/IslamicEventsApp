package com.example.nonameproject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
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
import android.widget.Spinner;
import android.widget.TimePicker;

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
	private static final String IMAGE_DIRECTORY_NAME = "EventsImages";
	private Button bCamera;
	private Button bGallery;
	private EditText etTitle;
	private Spinner spCat;
	private EditText etAddress;
	private EditText etDate;
	private EditText etDesc;
	private Context context;
	private Uri fileUri;
	private Bitmap bitmap;
	

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

		/*** FILL SPINNER WITH VALUES ***/
		fillSpinner();

		/** SHOW DATE PICKER DIALOG WHEN DATA FIELD IS CLICKED **/
		setUpDateTimePicker();

		/** TODO: OPEN CAMERA TO TAKE PICTURE **/
		setUpCameraAction();

		/** TODO: OPEN GALLERY TO SELECT PICTURE **/
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

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {

			// bimatp factory
			BitmapFactory.Options options = new BitmapFactory.Options();

			// downsizing image as it throws OutOfMemory Exception for larger
			// images
			
			/**
			 * TODO: size of image is big, convert it to small before uploading
			 */
			options.inSampleSize = 4;

			bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
					options);
			/**
			 * TODO: SHOW BITMAP IN A PREVIEW AND CROP IF I HAD ANY REQ LIKE THAT
			 */

		}
	}

	private void captureImage() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		fileUri = System.getInstance(context).getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

		// start the image capture Intent
		startActivityForResult(intent, CAPTURE_IMAGE);
	}
	
	
	private void submitEventInBackground(){
		/**
		 * TODO: FIELD VALIDATION. NONE SHOULD BE EMPTY
		 */
		
		/**
		 * TODO: SHOW WAIT DIALOG WHILE DOING IN BACKGROUND
		 * AND ALSO SHOW ERROR DIALOG INCASE OF ERROR PROCESSING
		 */
	
		final String eventTitle = etTitle.getText().toString();
		final String eventCat = spCat.getSelectedItem().toString();
		final String address = etAddress.getText().toString();
		final String date = etDate.getText().toString();
		final String description = etDesc.getText().toString();
		final String imageString = bitmapToBase64(bitmap);
		
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				ServerUtil serverUtil = new ServerUtil();
				int count = 1;
				String response = null;
				
				for (count = 1; count<=MAX_TRIES; count++) {
					logMessage("Uploading event: try # "+count);
					try{
						response = serverUtil.submitEvent(eventTitle, eventCat,address, date, description, imageString);
						
						if (response != null) {
							break;
						}
					}catch(Exception e){
						logMessage("Error event submission: "+e.getMessage());
					}
				}
				
				/**
				 * IF MAX TRIES ARE ALSO DONE THEN SHOW ERROR
				 * TODO: SHOW THE ERROR MESSAGE INTO A DIALOG INSTEAD
				 * OF A LOG MESSAGE
				 */
				if (count == (MAX_TRIES+1)) {
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
					try{
						Json json = new Json(jsonResult);
						successUpload = json.getAttribute("success");	
					}catch(Exception e){
						e.printStackTrace();
					}
					if (successUpload.trim().equals("1")) {
						displayMessage("Event submitted successfully.");
						finish();
					}else{
						displayMessage("Something went wrong");
					}
				}
			}
		}.execute();
	}
	
	private String bitmapToBase64(Bitmap bitmap){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG,10, bos);
		byte[] byteArray = bos.toByteArray();
		
		return Base64.encodeToString(byteArray,Base64.DEFAULT);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_event, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_save_event) {
			submitEventInBackground();	
			return true;
		} else if (id == R.id.action_cancel_event) {

			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
