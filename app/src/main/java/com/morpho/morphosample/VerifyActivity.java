// The present software is not subject to the US Export Administration Regulations (no exportation license required), May 2012
package com.morpho.morphosample;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.manvish.sampletest.R;
import com.morpho.morphosample.info.MorphoInfo;
import com.morpho.morphosample.info.ProcessInfo;
import com.morpho.morphosample.info.VerifyInfo;
import com.morpho.morphosample.info.subtype.AuthenticationMode;
import com.morpho.morphosmart.sdk.ErrorCodes;




public class VerifyActivity extends MorphoTabActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verify);
		VerifyInfo.getInstance();
		initAuthentMode();
		initSelectedFile();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_verify, menu);
		return true;
	}

	private void initSelectedFile()
	{
		TextView tv = (TextView) findViewById(R.id.selectedfile);
		tv.setText(VerifyInfo.getInstance().getFileName());
	}

	public void onAuthentModeClicked(View view)
	{
		RadioButton rb = (RadioButton) view;
		AuthenticationMode am = AuthenticationMode.File;
		if (rb.getId() == R.id.database)
		{
			am = AuthenticationMode.Database;
		}
		VerifyInfo.getInstance().setAuthenticationMode(am);
	}

	private void initAuthentMode()
	{
		AuthenticationMode am = VerifyInfo.getInstance().getAuthenticationMode();
		int id = R.id.file;
		if (am == AuthenticationMode.Database)
		{
			id = R.id.database;
		}
		RadioButton rb = (RadioButton) findViewById(id);
		rb.setChecked(true);
	}

	public void onSelectFileClick(View view)
	{
		try
		{
			Intent activityIntent = new Intent(this, FileChooserActivity.class);
			// Store the Workflow file in the intent
			startActivityForResult(activityIntent, 0);
		}
		catch (Exception e)
		{
		}
	}

	/**
	 * Function called when the FileChooserActivity exits!
	 *
	 * @param requestCode : Activity result request code
	 * @param resultCode : how did the activity end (Next, Cancel, Back, ...) ?
	 * @param data : Activity result data
	 * @since 1.0
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		try
		{
			super.onActivityResult(requestCode, resultCode, data);
			if (RESULT_OK == resultCode)
			{
				Bundle b = data.getExtras();
				if (b.containsKey("SelectedFile"))
				{
					VerifyInfo.getInstance().setFileName(b.getString("SelectedFile"));
					initSelectedFile();
					((TextView) findViewById(R.id.idselectfilelabel)).setTextColor(Color.BLACK);
				}
			}
		}
		catch (Exception e)
		{
		}
	}

	@Override
	public MorphoInfo retrieveSettings()
	{
		MorphoInfo ret = null;
		if (("".equals(VerifyInfo.getInstance().getFileName())) && (VerifyInfo.getInstance().getAuthenticationMode() == AuthenticationMode.File))
		{
			alert(getResources().getString(R.string.mustselectfile));			
			((TextView) findViewById(R.id.idselectfilelabel)).setTextColor(Color.RED);
		}
		else
		{
			if (VerifyInfo.getInstance().getAuthenticationMode() == AuthenticationMode.Database)
			{
				if (ProcessInfo.getInstance().getDatabaseItems().size() == 0)
				{
					alert(ErrorCodes.getError(ErrorCodes.MORPHOERR_DB_EMPTY, 0));
				}
				else
				{
					if (ProcessInfo.getInstance().getDatabaseSelectedIndex() != -1)
					{
						((TextView) findViewById(R.id.idselectfilelabel)).setTextColor(Color.BLACK);
						ret = VerifyInfo.getInstance();
					}
					else
					{
						alert(getResources().getString(R.string.selectuserfirst));						
					}
				}
			}
			else
			{
				((TextView) findViewById(R.id.idselectfilelabel)).setTextColor(Color.BLACK);
				ret = VerifyInfo.getInstance();
			}
		}
		return ret;
	}
}
