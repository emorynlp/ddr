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
package edu.emory.mathcs.nlp.bin;

import java.io.PrintStream;
import java.util.List;

import org.kohsuke.args4j.Option;

import edu.emory.mathcs.nlp.bin.util.BinUtils;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.common.util.Language;
import edu.emory.mathcs.nlp.common.util.NLPUtils;
import edu.emory.mathcs.nlp.structure.constituency.CTReader;
import edu.emory.mathcs.nlp.structure.constituency.CTTree;
import edu.emory.mathcs.nlp.structure.conversion.C2DConverter;
import edu.emory.mathcs.nlp.structure.dependency.NLPGraph;


public class DDGConvert
{
	@Option(name="-i", usage="input path (required)", required=true, metaVar="<filepath>")
	private String input_path;
	@Option(name="-pe", usage="parse file extension (default: parse)", required=false, metaVar="<string>")
	private String parse_ext  = "parse";
	@Option(name="-oe", usage="output file extension (default: ddg)", required=false, metaVar="<string>")
	private String output_ext = "ddg";
	@Option(name="-n", usage="if set, normalize empty category indices", required=false, metaVar="<boolean>")
	private boolean normalize = false;
	@Option(name="-r", usage="if set, traverse parse files recursively", required=false, metaVar="<boolean>")
	private boolean recursive = false;

	public DDGConvert() {}
	
	public DDGConvert(String[] args) throws Exception
	{
		BinUtils.initArgs(args, this);
		Language language = Language.ENGLISH;
		
		List<String> parseFiles = FileUtils.getFileList(input_path, parse_ext, recursive);
		C2DConverter converter = NLPUtils.getC2DConverter(language);
		
		for (String parseFile : parseFiles)
		{
			int n = convert(converter, language, parseFile, parse_ext, output_ext, normalize);
			System.out.printf("%s: %d trees\n", parseFile, n);
		}
	}
	
	protected int convert(C2DConverter converter, Language language, String parseFile, String parseExt, String outputExt, boolean normalize) throws Exception
	{
		CTReader reader = new CTReader(IOUtils.createFileInputStream(parseFile), language);
		PrintStream fout = IOUtils.createBufferedPrintStream(parseFile+"."+outputExt);
		CTTree   cTree;
		NLPGraph dTree;
		int n;
		
		for (n=0; (cTree = reader.next()) != null; n++)
		{
			if (normalize) cTree.normalizeIndices();
			dTree = converter.toDependencyGraph(cTree);
			
			if (dTree != null)
				fout.println(dTree.toString()+"\n");
			else
				System.err.println("No token in the tree "+(n+1)+"\n"+cTree.toStringLine());
		}
		
		reader.close();
		fout.close();
		return n;
	}
	
	public static void main(String[] args)
	{
		try
		{
			new DDGConvert(args);
		}
		catch (Exception e) {e.printStackTrace();}
	}
}