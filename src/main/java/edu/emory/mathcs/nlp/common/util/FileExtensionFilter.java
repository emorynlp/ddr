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

import java.io.File;
import java.io.FilenameFilter;

import edu.emory.mathcs.nlp.common.constant.StringConst;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class FileExtensionFilter implements FilenameFilter
{
	private String s_extension; 
	
	/** @param extension the extension of files to keep (e.g., {@code "txt"}). */
	public FileExtensionFilter(String extension)
	{
		s_extension = StringUtils.toLowerCase(extension);
	}

	@Override
	public boolean accept(File dir, String name)
	{
		return match(name); 
	}
	
	public boolean match(String name)
	{
		return s_extension.equals(StringConst.ASTERISK) || StringUtils.toLowerCase(name).endsWith(s_extension);
	}
}