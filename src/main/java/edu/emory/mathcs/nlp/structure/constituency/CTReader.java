/**
 * Copyright 2014, Emory University
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
package edu.emory.mathcs.nlp.structure.constituency;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.StringTokenizer;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.Language;
import edu.emory.mathcs.nlp.structure.util.PTBLib;

/**
 * Constituent tree reader.
 * @see CTTree 
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CTReader
{
	private LineNumberReader reader;
	private Deque<String>    tokens;
	private Language         language;
	
	public CTReader()
	{
		this(Language.ENGLISH);
	}
	
	public CTReader(Language language)
	{
		setLanguage(language);
	}
	
	/** @param in internally wrapped by {@code new LineNumberReader(new InputStreamReader(new BufferedInputStream(in)))}}. */
	public CTReader(InputStream in)
	{
		this(in, Language.ENGLISH);
	}
	
	public CTReader(InputStream in, Language language)
	{
		open(in);
		setLanguage(language);
	}
	
	/** @param in internally wrapped by {@code new LineNumberReader(new InputStreamReader(new BufferedInputStream(in)))}}. */
	public void open(InputStream in)
	{
		open(in, "UTF-8");
	}
	
	public void open(InputStream in, String charsetName)
	{
		try
		{
			reader = new LineNumberReader(new InputStreamReader(new BufferedInputStream(in), charsetName));
			tokens = new ArrayDeque<String>();
		}
		catch (UnsupportedEncodingException e) {e.printStackTrace();}
	}
	
	/** Closes the current reader. */
	public void close()
	{
		if (reader != null)
		{
			try
			{
				reader.close();
			}
			catch (IOException e) {e.printStackTrace();}			
		}
	}
	
	public Language getLanguage()
	{
		return language;
	}
	
	public void setLanguage(Language language)
	{
		this.language = language;
	}
	
	/** @return a list of all constituent trees in the input stream. */
	public List<CTTree> readTrees()
	{
		List<CTTree> trees = new ArrayList<>();
		CTTree tree;
		
		while ((tree = next()) != null)
			trees.add(tree);

		return trees;
	}
	
	/**
	 * @return the next tree if exists; otherwise, {@code null}.
	 * Returns {@code null} if the next tree is incomplete or erroneous.
	 * Automatically links antecedents of all co-indexed empty categories.
	 */
	public CTTree next()
	{
		String token = nextToken(), tags;
		
		if (token == null)
			return null;
		
		if (!token.equals(StringConst.LRB))
		{
			System.err.println("Error: \""+token+"\" found, \"(\" expected - line "+reader.getLineNumber());
			return null;
		}
		
		int nBrackets = 1, startLine = reader.getLineNumber();
		CTNode root = new CTNode(CTTag.TOP, null);
		CTNode curr = root, node;
		
		while ((token = nextToken()) != null)
		{
			if (nBrackets == 1 && token.equals(CTTag.TOP))
				continue;
			
			if (token.equals(StringConst.LRB))
			{
				tags = nextToken();
				node = new CTNode(tags);
				curr.addChild(node);
				curr = node;
				nBrackets++;
			}
			else if (token.equals(StringConst.RRB))
			{
				curr = curr.getParent();
				nBrackets--;
			}
			else
			{
				curr.setForm(token);
			}
			
			if (nBrackets == 0)
			{
				CTTree tree = new CTTree(root);
				if (language == Language.ENGLISH) PTBLib.preprocess(tree);
				return tree;
			}
		}
		
		System.err.println("Error: brackets mismatch - starting line "+startLine);
		return null;
	}
	
	/**
	 * @return the next tree after skipping the specific number of trees if exists; otherwise, {@code null}.
	 * @param skip the number of trees to skip.
	 */
	public CTTree next(int skip)
	{
		CTTree tree = null;
		int i;
		
		for (i=0; i<=skip; i++)
		{
			tree = next();
			if (tree == null) return null;
		}
		
		return tree;
	}

	/** Called by {@link #next()}. */
	private String nextToken()
	{
		if (tokens.isEmpty())
		{
			String line = null;
			
			try
			{
				line = reader.readLine();
			}
			catch (IOException e) {e.printStackTrace();}

			if (line == null)
				return null;
			
			line = line.trim();
			if (line.isEmpty())
				return nextToken();
			
			StringTokenizer tok = new StringTokenizer(line, "() \t\n\r\f", true);
			String str;
			
			while (tok.hasMoreTokens())
			{
				str = tok.nextToken().trim();
				if (!str.isEmpty()) tokens.add(str);
			}
		}
		
		return tokens.pop();
	}
}	