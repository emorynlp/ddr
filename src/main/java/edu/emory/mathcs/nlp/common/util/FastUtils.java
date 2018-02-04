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

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class FastUtils
{
	static public <K>int increment(Object2IntMap<K> map, K key)
	{
		return map.merge(key, 1, (oldCount, newCount) -> oldCount + newCount);
	}
	
	static public <K>int increment(Object2IntMap<K> map, K key, int count)
	{
		return map.merge(key, count, (oldCount, newCount) -> oldCount + newCount);
	}
	
	static public void add(FloatArrayList list, int index, float inc)
	{
		list.set(index, list.getFloat(index)+inc);
	}
}
