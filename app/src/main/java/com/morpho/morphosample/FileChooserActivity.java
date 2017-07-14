// The present software is not subject to the US Export Administration Regulations (no exportation license required), May 2012
package com.morpho.morphosample;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.manvish.sampletest.R;
import com.morpho.morphosample.file.FileArrayAdapter;
import com.morpho.morphosample.file.Option;
import com.morpho.morphosample.file.Option.ItemType;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileChooserActivity extends ListActivity
{

	private File				currentDir;
	private FileArrayAdapter	adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		try
		{
			//Environment.getExternalStorageDirectory().getParent()
			currentDir = new File("/sdcard/");
			fill(currentDir);
		}
		catch (Exception e)
		{
		}

		setContentView(R.layout.activity_file_choice);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_file_choice, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				// This ID represents the Home or Up button. In the case of this
				// activity, the Up button is shown. Use NavUtils to allow users
				// to navigate up one level in the application structure. For
				// more details, see the Navigation pattern on Android Design:
				//
				// http://developer.android.com/design/patterns/navigation.html#up-vs-back
				//
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void fill(File f)
	{
		File[] dirs = f.listFiles();
		this.setTitle("Current Dir: " + f.getName());
		List<Option> dir = new ArrayList<Option>();
		List<Option> fls = new ArrayList<Option>();
		try
		{
			for (File ff : dirs)
			{
				if (ff.isDirectory())
				{
					dir.add(new Option(ff.getName(), "Folder", ff.getAbsolutePath(), ItemType.Folder));
				}
				else
				{
					fls.add(new Option(ff.getName(), "File Size: " + ff.length(), ff.getAbsolutePath(), ItemType.File));
				}
			}
		}
		catch (Exception e)
		{
		}
		Collections.sort(dir);
		Collections.sort(fls);
		dir.addAll(fls);
		if (!f.getName().equalsIgnoreCase("sdcard"))
		{
			dir.add(0, new Option("..", "Parent Directory", f.getParent(), ItemType.Folder));
		}

		adapter = new FileArrayAdapter(FileChooserActivity.this, R.layout.file_view, dir);
		this.setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		Option o = adapter.getItem(position);
		if ((o.getData().equalsIgnoreCase("folder")) || (o.getData().equalsIgnoreCase("parent directory")))
		{
			currentDir = new File(o.getPath());
			fill(currentDir);
		}
		else
		{
			onFileClick(o);
		}
	}

	private void onFileClick(Option o)
	{
		//Toast.makeText(this, "File Clicked: "+ currentDir.getPath() +"/"+  o.getName(), Toast.LENGTH_SHORT).show();
		Intent resultIntent = new Intent();
		resultIntent.putExtra("SelectedFile", currentDir.getPath() + "/" + o.getName());
		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}
}
