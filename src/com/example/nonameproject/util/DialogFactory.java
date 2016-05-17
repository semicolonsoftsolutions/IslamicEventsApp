package com.example.nonameproject.util;

import com.example.nonameproject.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogFactory {

	public static Dialog createDialog(DialogType type, Context context) {
		Dialog dialog = null;

		switch (type) {
		case DIALOG_WAIT:
			dialog = new ProgressDialog(context);
			break;

		case DIALOG_ERROR:
			dialog = new AlertDialog.Builder(context).setIcon(
					android.R.drawable.alert_dark_frame).setPositiveButton(
					"Okay", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					}).create();
			break;
		case DIALOG_ALERT:
			break;
		case DIALOG_DATETIME:
			dialog = new Dialog(context);
			dialog.setContentView(R.layout.dialog_date_time);
			dialog.setTitle("Date Time");
			break;
		case DIALOG_IMAGE_PREVIEW:
			dialog = new Dialog(context);
			dialog.setContentView(R.layout.dialog_image_preview);
			dialog.setTitle("Preview");
			break;
		}

		return dialog;
	}

}
