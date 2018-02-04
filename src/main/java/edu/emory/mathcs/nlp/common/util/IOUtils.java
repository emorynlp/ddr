/**
 * Copyright 2015, Emory University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.emory.mathcs.nlp.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import edu.emory.mathcs.nlp.common.constant.StringConst;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class IOUtils
{
	private IOUtils() {}
	
	public static Set<String> readSet(InputStream in)
	{
		BufferedReader reader = createBufferedReader(in);
		Set<String> set = new HashSet<>();
		String line;
		
		try
		{
			while ((line = reader.readLine()) != null)
				set.add(line.trim());
		}
		catch (IOException e) {e.printStackTrace();}
		
		return set;
	}
	
	public static Object fromByteArray(byte[] array)
	{
		ByteArrayInputStream bin = new ByteArrayInputStream(array);
		Object obj = null;
		
		try
		{
			ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(bin));
			obj = in.readObject();
			in.close();
		}
		catch (Exception e) {e.printStackTrace();}
		
		return obj;
	}
	
	
	public static byte[] toByteArray(Object obj)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		try
		{
			ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(bos));
			out.writeObject(obj);
			out.close();
		}
		catch (IOException e) {e.printStackTrace();}
		
		return bos.toByteArray();
	}
	
	public static Map<String,byte[]> toByteMap(ZipInputStream stream) throws IOException
	{
		Map<String,byte[]> map = new HashMap<>();
		ZipEntry zEntry;
		
		while ((zEntry = stream.getNextEntry()) != null)
			map.put(zEntry.getName(), toByteArray(stream));

		stream.close();
		return map;
	}
	
	public static byte[] toByteArray(ZipInputStream in) throws IOException
	{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int count;
		
		while ((count = in.read(buffer)) != -1)
			bout.write(buffer, 0, count);
         
		return bout.toByteArray();
	}
	
	/** @param in internally wrapped by {@code new BufferedReader(new InputStreamReader(in))}. */
	static public BufferedReader createBufferedReader(InputStream in)
	{
		return new BufferedReader(new InputStreamReader(in));
	}
	
	static public BufferedReader createBufferedReader(File file)
	{
		try
		{
			return new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		}
		catch (FileNotFoundException e) {e.printStackTrace();}
		
		return null;
	}
	
	static public BufferedReader createBufferedReader(String filename)
	{
		return createBufferedReader(createFileInputStream(filename));
	}
	
	/** @param in internally wrapped by {@code new PrintStream(new BufferedOutputStream(out))}. */
	static public PrintStream createBufferedPrintStream(OutputStream out)
	{
		return new PrintStream(new BufferedOutputStream(out));
	}
	
	static public PrintStream createBufferedPrintStream(String filename)
	{
		return createBufferedPrintStream(createFileOutputStream(filename));
	}
	
	static public FileInputStream createFileInputStream(String filename)
	{
		FileInputStream in = null;
		
		try
		{
			in = new FileInputStream(filename);
		}
		catch (FileNotFoundException e) {e.printStackTrace();}
		
		return in;
	}
	
	static public FileInputStream[] createFileInputStreams(String[] filelist)
	{
		int i, len = filelist.length;
		FileInputStream[] in = new FileInputStream[len];
		
		for (i=0; i<len; i++)
			in[i] = IOUtils.createFileInputStream(filelist[i]);
		
		return in;
	}
	
	static public FileOutputStream createFileOutputStream(String filename)
	{
		FileOutputStream out = null;
		
		try
		{
			out = new FileOutputStream(filename);
		}
		catch (FileNotFoundException e) {e.printStackTrace();}
		
		return out;
	}
	
	/** @param in internally wrapped by {@code new ByteArrayInputStream(str.getBytes())}. */
	static public ByteArrayInputStream createByteArrayInputStream(String s)
	{
		return new ByteArrayInputStream(s.getBytes());
	}
	
	public static InputStream getInputStreamsFromResource(String path)
	{
		return IOUtils.class.getResourceAsStream(StringConst.FW_SLASH+path);
	}
	
	public static InputStream getInputStream(String path)
	{
		InputStream in = IOUtils.getInputStreamsFromResource(path);
		return (in != null) ? in : IOUtils.createFileInputStream(path);
	}
}
