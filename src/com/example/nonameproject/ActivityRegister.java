package com.example.nonameproject;

import java.util.ArrayList;
import java.util.List;

import com.example.nonameproject.util.AppPrefs;
import com.example.nonameproject.util.DialogFactory;
import com.example.nonameproject.util.DialogType;
import com.example.nonameproject.util.GCMConfig;
import com.example.nonameproject.util.Json;
import com.example.nonameproject.util.ServerUtil;
import com.example.nonameproject.util.System;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityRegister extends ActivityMaster {

	private static final int MAX_TRIES = 5;
	private Context context;
	private Button bRegisterUser;
	private EditText etPhoneNumber, etName;
	private String phoneNumber;
	private String name;
	private System system;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		context = this;
		system = System.getInstance(this);

		bRegisterUser = (Button) findViewById(R.id.bRegisterUser);
		etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
		etName = (EditText) findViewById(R.id.etName);

		bRegisterUser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				phoneNumber = etPhoneNumber.getText().toString();
				name = etName.getText().toString();

				/**
				 * FIELD VALIDATION
				 */
				if (TextUtils.isEmpty(phoneNumber)) {
					displayMessage("Invalid phone number");
					return;
				}
				if (TextUtils.isEmpty(name)) {
					displayMessage("Kindly tell us your name");
					return;
				}
				/***************************************************/

				/**
				 * REGISTER IN BACKGROUND IF EVERYTING IS FINE, GCM WON'T ALLOW
				 * REGISTERING IN THE UI THREAD SO THAT'S WHY WE DO IT IN ASYNCH
				 * TASK INSTEAD. . . . .
				 **/
				registerInBackground(phoneNumber, name);

			}
		});

	}

	/**
	 * Handles registration tasks. Implement sleep in catch block in
	 * doInBackground below and delete this line of comment.
	 */
	private void registerInBackground(String phoneNumber, String name) {

		// FINAL BECAUSE IT AIN'T GONNA CHANGE IN CODE
		final String pNumber = phoneNumber;
		final String pName = name;

		/**
		 * DIALOGS THAT'LL BE USED IN THIS TASK
		 */
		final ProgressDialog waitDialog = (ProgressDialog) DialogFactory
				.createDialog(DialogType.DIALOG_WAIT, context);
		waitDialog.setTitle("Please wait!");
		waitDialog.setMessage("Registering device on cloud");
		waitDialog.setIndeterminate(true);

		final AlertDialog errorDialog = (AlertDialog) DialogFactory
				.createDialog(DialogType.DIALOG_ERROR, context);
		errorDialog.setTitle("Error");
		errorDialog.setMessage("Check your internet connection and try again!");

		/******************************************************************************************************/

		new AsyncTask<Void, Void, List<Object>>() {

			protected void onPreExecute() {
				waitDialog.show();
			};

			@Override
			protected List<Object> doInBackground(Void... params) {

				/** INITIATE ID VARIABLE FROM NULL */
				String id = null;
				String serverResponse = null;

				// one for GCM reg id and one for server response
				List<Object> result = new ArrayList<Object>();

				GoogleCloudMessaging gcm = GoogleCloudMessaging
						.getInstance(context);

				/** PROBLEM MIGHT OCCURE SO TRY MAX NUMBER OF TIMES MAX = 5 **/
				int count = 1;
				for (count = 1; count <= MAX_TRIES; count++) {

					logMessage("Trying to register on cloud, try: " + count);

					try {

						id = gcm.register(GCMConfig
								.senderID(ActivityRegister.this));

						// WE HAVE A REGISTRATION ID, BREAK THE LOOP AND RETURN
						if (id != null) {
							break;
						}

					} catch (Exception e) {
						logMessage("Unable to register on cloud, error: "
								+ e.getMessage());
					}
				}

				//loop exausts, Change the error message (I don't care about spellings when it comes to writing code)
				if (count == MAX_TRIES + 1) {
					errorDialog
							.setMessage("Server is not responding, try again later!");
					count = 1;
				}

				// IF ID IS NOT NULL THEN TRY TO SAVE IT ON SERVER SIDE
				// ALONG WITH OTHER PARAMETERS
				if (id != null) {

					// PUT ID IN RESULT ONLY WHEN IT IS NOT NULL
					result.add(id);

					ServerUtil server = new ServerUtil();

					// DO THE SAME THING AS GCM CLOUD REGISTRATION, USE LOOPS IN
					// CASE;
					for (count = 1; count <= MAX_TRIES; count++) {

						try {

							serverResponse = server.registerUser(
									system.getDeviceId(), pNumber, id, pName);

							if (serverResponse != null)
								break;

						} catch (Exception e) {
							logMessage("Unable to register regID on server, error: "
									+ e.getMessage());
						}

					}

					if (serverResponse != null) {
						// PUT SERVER RESPONSE IN RESULT ONLY WHEN IT IS NOT
						// NULL
						result.add(serverResponse);
					}

					// Change the error message
					// count in a for loop is +1 then max limit.last
					// increment on last iteration, false iteration.
					if (count == MAX_TRIES + 1) {
						errorDialog
								.setMessage("Server is not responding, try again later!");
						count = 1;
					}

				}

				return result;
			}

			protected void onPostExecute(List<Object> response) {

				// IF EVERYTHING WENT OKAY THEN LENGTH SHOULD BE 2. i.e. GCM +
				// Server works okay.
				if (response.size() == 2) {

					// GET THE REG ID FROM RESPONSE SENT FROM doInBackground
					String regID = (String) response.get(0);

					// GET SERVER RESPONSE AND PARSE JSON OUT OF IT
					// I AM USING FREE HOSTING SO THAT'S WHY I AM GETTING
					// GARBAGE WITH MY JSON TOO. I'LL CLEAN IT UP BY MYSELF.
					String serverResponse = (String) response.get(1);

					/**
					 * CAN PUT THIS CODE INTO StringUtil Class if i made one in
					 * future also serverResponse might be somewhat like this "<br>
					 * <table
					 * border='1' cellpadding='2' bgcolor='" handle it too.
					 * currently it is causing application to crash.
					 * 
					 * PS: okay no need to handle above response, it was because
					 * of syntax error in PHP. LOL
					 * 
					 * I think below string handling code is not protected against
					 * different strings. like string length could be 1 or something
					 * like that. keep in mind to refactor it.
					 */

					
					int openingBraceIndex = serverResponse.indexOf('{');
					int closingBraceIndex = serverResponse.lastIndexOf('}');

					String jsonResponse = serverResponse.substring(
							openingBraceIndex, (closingBraceIndex + 1));

					logMessage(jsonResponse);
					/*******************************************************************/

					// GET THE JSON STRING AND PARSE ATTRIBUTES IN IT.
					// JSON MIGHT NOT CONTAIN THE KEY AND CAN THROUGH AN ERROR
					// PROTECT IT TOO. 
					int success = -1;
					String operationType = null;
					try {
						Json json = new Json(jsonResponse);
						success = Integer.parseInt(json.getAttribute("success")
								.trim());
						//CAN USE AT SOME LATER STAGE
						operationType = json.getAttribute("operation");
					} catch (Exception e) {
						e.printStackTrace();
					}

					logMessage("success: " + success);
					// EVERYTHING IS OKAY, GCM REG ID IS NOT NULL OR EMPTY AND
					// RESPONSE FROM SERVER IS ALSO NOT NULL OR EMPTY OR 0
					// SAVE ID IN PREFS AND FINISH THIS ACTIVITY;
					if (success == 1) {
						AppPrefs prefs = new AppPrefs(ActivityRegister.this);
						prefs.save(AppPrefs.REG_ID, regID);
						waitDialog.cancel();
						setResult(RESULT_OK);
						finish();
					} else {
						waitDialog.cancel();
						errorDialog.show();
					}

				} else {
					waitDialog.cancel();
					errorDialog.show();
				}
			};
		}.execute();
	}
}