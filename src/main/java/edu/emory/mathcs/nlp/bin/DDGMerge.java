/**
 * Copyright 2016, Emory University
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
package edu.emory.mathcs.nlp.bin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.component.morph.MorphAnalyzer;
import edu.emory.mathcs.nlp.component.morph.english.EnglishMorphAnalyzer;
import edu.emory.mathcs.nlp.structure.constituency.CTNode;
import edu.emory.mathcs.nlp.structure.constituency.CTReader;
import edu.emory.mathcs.nlp.structure.constituency.CTTree;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DDGMerge
{
	public List<List<String>> getSources(InputStream in, MorphAnalyzer analyzer)
	{
		List<List<String>> list = new ArrayList<>();
		CTReader reader = new CTReader(in);
		CTTree tree;
		
		while ((tree = reader.next()) != null)
		{
			if (tree.getTokens().isEmpty()) continue;
			List<String> ls = new ArrayList<>();
			list.add(ls);
			
			for (CTNode node : tree.getTokens())
			{
				StringJoiner join = new StringJoiner("\t");
				join.add(Integer.toString(node.getTokenID()+1));
				join.add(node.getForm());
				join.add(analyzer.setLemma(node));
				ls.add(join.toString());
			}
		}
		
		reader.close();
		return list;
	}
	
	public List<List<String>> getTargets(InputStream in)
	{
		BufferedReader reader = IOUtils.createBufferedReader(in);
		List<List<String>> list = new ArrayList<>();
		List<String> ls = new ArrayList<>();
		String line;
		
		try
		{
			while ((line = reader.readLine()) != null)
			{
				line = line.trim();
				
				if (line.isEmpty())
				{
					if (!ls.isEmpty())
					{
						list.add(ls);
						ls = new ArrayList<>();
					}
				}
				else
					ls.add(line);
			}
			
			reader.close();
		}
		catch (IOException e) {e.printStackTrace();}
		
		return list;
	}
	
	public void merge(String output_file, List<List<String>> sources, List<List<String>> targets)
	{
		PrintStream fout = IOUtils.createBufferedPrintStream(output_file);
		
		for (int i=0; i<sources.size(); i++)
		{
			List<String> source = sources.get(i);
			List<String> target = targets.get(i);
			
			for (int j=0; j<source.size(); j++)
				fout.println(source.get(j)+"\t"+target.get(j));
			
			fout.println();
		}
		
		fout.close();
	}
	
	public boolean validate(List<List<String>> sources, List<List<String>> targets)
	{
		if (sources.size() != targets.size()) return false;
		int s = sources.stream().mapToInt(n -> n.size()).sum();
		int t = targets.stream().mapToInt(n -> n.size()).sum();
		return s == t;
	}
	
	static public void main(String[] args)
 	{
		final String source_path = args[0];
		final String target_path = args[1];
		final String parse_ext   = args[2];
		final String skel_ext    = "skel";
		
		MorphAnalyzer analyzer = new EnglishMorphAnalyzer();
		DDGMerge ddg = new DDGMerge();
		
		List<String> source_files = FileUtils.getFileList(source_path, parse_ext, true);
		Collections.sort(source_files);
		
		for (String source_file : source_files)
		{
			String base = source_file.substring(source_path.length());
			String target_file = (source_files.size() == 1 && new File(target_path).isFile()) ? target_path : target_path+"/"+base+"."+skel_ext;;

			if (!new File(target_file).exists())
			{
				System.err.println("Target does not exist: "+base);
				continue;
			}
			
			List<List<String>> sources = ddg.getSources(IOUtils.createFileInputStream(source_file), analyzer);
			List<List<String>> targets = ddg.getTargets(IOUtils.createFileInputStream(target_file));
			
			if (ddg.validate(sources, targets))
			{
				System.out.println(base);
				String output_file = target_file.substring(0, target_file.length()-parse_ext.length()-skel_ext.length()-1)+"ddg";
				ddg.merge(output_file, sources, targets);
			}
			else
				System.err.println("Trees do not match: "+base);
		}
	}
}
