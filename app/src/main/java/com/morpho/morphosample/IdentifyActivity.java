// The present software is not subject to the US Export Administration Regulations (no exportation license required), May 2012
package com.morpho.morphosample;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.manvish.sampletest.R;
import com.morpho.morphosample.info.IdentifyInfo;
import com.morpho.morphosample.info.MorphoInfo;
import com.morpho.morphosample.info.VerifyInfo;

public class IdentifyActivity extends MorphoTabActivity
{

	// private MorphoInfo morphoInfo = ProcessInfo.getInstance().getMorphoInfo();
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_identify);
		VerifyInfo.getInstance();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_identify, menu);
		return true;
	}

	@Override
	public MorphoInfo retrieveSettings()
	{
		Log.i("retrieveSettings","Start");
		return IdentifyInfo.getInstance();
	}
}
