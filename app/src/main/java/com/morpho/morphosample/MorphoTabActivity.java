// The present software is not subject to the US Export Administration Regulations (no exportation license required), May 2012
package com.morpho.morphosample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.manvish.sampletest.R;
import com.morpho.morphosample.info.MorphoInfo;
import com.morpho.morphosmart.sdk.ErrorCodes;

public class MorphoTabActivity extends Activity
{

	public MorphoInfo retrieveSettings()
	{
		return null;
	}

	protected void alert(String msg)
	{
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(R.string.app_name);
		alertDialog.setMessage(msg);
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
			}
		});
		alertDialog.show();
	}

	protected void alert(int codeError, int internalError, String title, String message)
	{
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(title);
		String msg;
		if (codeError == 0)
		{
			msg = getString(R.string.OP_SUCCESS);
		}
		else
		{
			String errorInternationalization = convertToInternationalMessage(codeError, internalError);
			msg = getString(R.string.OP_FAILED) + "\n" + errorInternationalization;
		}
		msg += ((message.equalsIgnoreCase("")) ? "" : "\n" + message);
		alertDialog.setMessage(msg);
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
			}
		});
		alertDialog.show();
	}

	@SuppressLint("DefaultLocale")
	public String convertToInternationalMessage(int iCodeError, int iInternalError)
	{
		switch (iCodeError)
		{
			case ErrorCodes.MORPHO_OK:
				return getString(R.string.MORPHO_OK);
			case ErrorCodes.MORPHOERR_INTERNAL:
				return getString(R.string.MORPHOERR_INTERNAL);
			case ErrorCodes.MORPHOERR_PROTOCOLE:
				return getString(R.string.MORPHOERR_PROTOCOLE);
			case ErrorCodes.MORPHOERR_CONNECT:
				return getString(R.string.MORPHOERR_CONNECT);
			case ErrorCodes.MORPHOERR_CLOSE_COM:
				return getString(R.string.MORPHOERR_CLOSE_COM);
			case ErrorCodes.MORPHOERR_BADPARAMETER:
				return getString(R.string.MORPHOERR_BADPARAMETER);
			case ErrorCodes.MORPHOERR_MEMORY_PC:
				return getString(R.string.MORPHOERR_MEMORY_PC);
			case ErrorCodes.MORPHOERR_MEMORY_DEVICE:
				return getString(R.string.MORPHOERR_MEMORY_DEVICE);
			case ErrorCodes.MORPHOERR_NO_HIT:
				return getString(R.string.MORPHOERR_NO_HIT);
			case ErrorCodes.MORPHOERR_STATUS:
				return getString(R.string.MORPHOERR_STATUS);
			case ErrorCodes.MORPHOERR_DB_FULL:
				return getString(R.string.MORPHOERR_DB_FULL);
			case ErrorCodes.MORPHOERR_DB_EMPTY:
				return getString(R.string.MORPHOERR_DB_EMPTY);
			case ErrorCodes.MORPHOERR_ALREADY_ENROLLED:
				return getString(R.string.MORPHOERR_ALREADY_ENROLLED);
			case ErrorCodes.MORPHOERR_BASE_NOT_FOUND:
				return getString(R.string.MORPHOERR_BASE_NOT_FOUND);
			case ErrorCodes.MORPHOERR_BASE_ALREADY_EXISTS:
				return getString(R.string.MORPHOERR_BASE_ALREADY_EXISTS);
			case ErrorCodes.MORPHOERR_NO_ASSOCIATED_DB:
				return getString(R.string.MORPHOERR_NO_ASSOCIATED_DB);
			case ErrorCodes.MORPHOERR_NO_ASSOCIATED_DEVICE:
				return getString(R.string.MORPHOERR_NO_ASSOCIATED_DEVICE);
			case ErrorCodes.MORPHOERR_INVALID_TEMPLATE:
				return getString(R.string.MORPHOERR_INVALID_TEMPLATE);
			case ErrorCodes.MORPHOERR_NOT_IMPLEMENTED:
				return getString(R.string.MORPHOERR_NOT_IMPLEMENTED);
			case ErrorCodes.MORPHOERR_TIMEOUT:
				return getString(R.string.MORPHOERR_TIMEOUT);
			case ErrorCodes.MORPHOERR_NO_REGISTERED_TEMPLATE:
				return getString(R.string.MORPHOERR_NO_REGISTERED_TEMPLATE);
			case ErrorCodes.MORPHOERR_FIELD_NOT_FOUND:
				return getString(R.string.MORPHOERR_FIELD_NOT_FOUND);
			case ErrorCodes.MORPHOERR_CORRUPTED_CLASS:
				return getString(R.string.MORPHOERR_CORRUPTED_CLASS);
			case ErrorCodes.MORPHOERR_TO_MANY_TEMPLATE:
				return getString(R.string.MORPHOERR_TO_MANY_TEMPLATE);
			case ErrorCodes.MORPHOERR_TO_MANY_FIELD:
				return getString(R.string.MORPHOERR_TO_MANY_FIELD);
			case ErrorCodes.MORPHOERR_MIXED_TEMPLATE:
				return getString(R.string.MORPHOERR_MIXED_TEMPLATE);
			case ErrorCodes.MORPHOERR_CMDE_ABORTED:
				return getString(R.string.MORPHOERR_CMDE_ABORTED);
			case ErrorCodes.MORPHOERR_INVALID_PK_FORMAT:
				return getString(R.string.MORPHOERR_INVALID_PK_FORMAT);
			case ErrorCodes.MORPHOERR_SAME_FINGER:
				return getString(R.string.MORPHOERR_SAME_FINGER);
			case ErrorCodes.MORPHOERR_OUT_OF_FIELD:
				return getString(R.string.MORPHOERR_OUT_OF_FIELD);
			case ErrorCodes.MORPHOERR_INVALID_USER_ID:
				return getString(R.string.MORPHOERR_INVALID_USER_ID);
			case ErrorCodes.MORPHOERR_INVALID_USER_DATA:
				return getString(R.string.MORPHOERR_INVALID_USER_DATA);
			case ErrorCodes.MORPHOERR_FIELD_INVALID:
				return getString(R.string.MORPHOERR_FIELD_INVALID);
			case ErrorCodes.MORPHOERR_USER_NOT_FOUND:
				return getString(R.string.MORPHOERR_USER_NOT_FOUND);
			case ErrorCodes.MORPHOERR_COM_NOT_OPEN:
				return getString(R.string.MORPHOERR_COM_NOT_OPEN);
			case ErrorCodes.MORPHOERR_ELT_ALREADY_PRESENT:
				return getString(R.string.MORPHOERR_ELT_ALREADY_PRESENT);
			case ErrorCodes.MORPHOERR_NOCALLTO_DBQUERRYFIRST:
				return getString(R.string.MORPHOERR_NOCALLTO_DBQUERRYFIRST);
			case ErrorCodes.MORPHOERR_USER:
				return getString(R.string.MORPHOERR_USER);
			case ErrorCodes.MORPHOERR_BAD_COMPRESSION:
				return getString(R.string.MORPHOERR_BAD_COMPRESSION);
			case ErrorCodes.MORPHOERR_SECU:
				return getString(R.string.MORPHOERR_SECU);
			case ErrorCodes.MORPHOERR_CERTIF_UNKNOW:
				return getString(R.string.MORPHOERR_CERTIF_UNKNOW);
			case ErrorCodes.MORPHOERR_INVALID_CLASS:
				return getString(R.string.MORPHOERR_INVALID_CLASS);
			case ErrorCodes.MORPHOERR_USB_DEVICE_NAME_UNKNOWN:
				return getString(R.string.MORPHOERR_USB_DEVICE_NAME_UNKNOWN);
			case ErrorCodes.MORPHOERR_CERTIF_INVALID:
				return getString(R.string.MORPHOERR_CERTIF_INVALID);
			case ErrorCodes.MORPHOERR_SIGNER_ID:
				return getString(R.string.MORPHOERR_SIGNER_ID);
			case ErrorCodes.MORPHOERR_SIGNER_ID_INVALID:
				return getString(R.string.MORPHOERR_SIGNER_ID_INVALID);
			case ErrorCodes.MORPHOERR_FFD:
				return getString(R.string.MORPHOERR_FFD);
			case ErrorCodes.MORPHOERR_MOIST_FINGER:
				return getString(R.string.MORPHOERR_MOIST_FINGER);
			case ErrorCodes.MORPHOERR_NO_SERVER:
				return getString(R.string.MORPHOERR_NO_SERVER);
			case ErrorCodes.MORPHOERR_OTP_NOT_INITIALIZED:
				return getString(R.string.MORPHOERR_OTP_NOT_INITIALIZED);
			case ErrorCodes.MORPHOERR_OTP_PIN_NEEDED:
				return getString(R.string.MORPHOERR_OTP_PIN_NEEDED);
			case ErrorCodes.MORPHOERR_OTP_REENROLL_NOT_ALLOWED:
				return getString(R.string.MORPHOERR_OTP_REENROLL_NOT_ALLOWED);
			case ErrorCodes.MORPHOERR_OTP_ENROLL_FAILED:
				return getString(R.string.MORPHOERR_OTP_ENROLL_FAILED);
			case ErrorCodes.MORPHOERR_OTP_IDENT_FAILED:
				return getString(R.string.MORPHOERR_OTP_IDENT_FAILED);
			case ErrorCodes.MORPHOERR_NO_MORE_OTP:
				return getString(R.string.MORPHOERR_NO_MORE_OTP);
			case ErrorCodes.MORPHOERR_OTP_NO_HIT:
				return getString(R.string.MORPHOERR_OTP_NO_HIT);
			case ErrorCodes.MORPHOERR_OTP_ENROLL_NEEDED:
				return getString(R.string.MORPHOERR_OTP_ENROLL_NEEDED);
			case ErrorCodes.MORPHOERR_DEVICE_LOCKED:
				return getString(R.string.MORPHOERR_DEVICE_LOCKED);
			case ErrorCodes.MORPHOERR_DEVICE_NOT_LOCK:
				return getString(R.string.MORPHOERR_DEVICE_NOT_LOCK);
			case ErrorCodes.MORPHOERR_OTP_LOCK_GEN_OTP:
				return getString(R.string.MORPHOERR_OTP_LOCK_GEN_OTP);
			case ErrorCodes.MORPHOERR_OTP_LOCK_SET_PARAM:
				return getString(R.string.MORPHOERR_OTP_LOCK_SET_PARAM);
			case ErrorCodes.MORPHOERR_OTP_LOCK_ENROLL:
				return getString(R.string.MORPHOERR_OTP_LOCK_ENROLL);
			case ErrorCodes.MORPHOERR_FVP_MINUTIAE_SECURITY_MISMATCH:
				return getString(R.string.MORPHOERR_FVP_MINUTIAE_SECURITY_MISMATCH);
			case ErrorCodes.MORPHOERR_FVP_FINGER_MISPLACED_OR_WITHDRAWN:
				return getString(R.string.MORPHOERR_FVP_FINGER_MISPLACED_OR_WITHDRAWN);
			case ErrorCodes.MORPHOERR_LICENSE_MISSING:
				return getString(R.string.MORPHOERR_LICENSE_MISSING);
			case ErrorCodes.MORPHOERR_CANT_GRAN_PERMISSION_USB:
				return getString(R.string.MORPHOERR_CANT_GRAN_PERMISSION_USB);
			default:
				return String.format("Unknown error %d, Internal Error = %d", iCodeError, iInternalError);
		}
	}
}
