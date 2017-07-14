// The present software is not subject to the US Export Administration Regulations (no exportation license required), May 2012
package com.morpho.morphosample.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MorphoTools
{

	public static ByteArrayOutputStream ReadFile(File file) throws IOException
	{

		ByteArrayOutputStream ous = null;
		InputStream ios = null;
		try
		{
			byte[] buffer = new byte[4096];
			ous = new ByteArrayOutputStream();
			ios = new FileInputStream(file);
			int read = 0;
			while ((read = ios.read(buffer)) != -1)
			{
				ous.write(buffer, 0, read);
			}
		}
		finally
		{
			try
			{
				if (ous != null)
					ous.close();
			}
			catch (IOException e)
			{
			}

			try
			{
				if (ios != null)
					ios.close();
			}
			catch (IOException e)
			{
			}
		}
		return ous;
	}	
	
	public static String checkfield(String field, boolean isUpdateTemplate)
	{
		if(isUpdateTemplate)
		{
			return field;
		}
		else
		{		
			if (field.equalsIgnoreCase(""))
			{
				return "<None>";
			}
			else
			{	
				return field;
			}
		}
	}
}
