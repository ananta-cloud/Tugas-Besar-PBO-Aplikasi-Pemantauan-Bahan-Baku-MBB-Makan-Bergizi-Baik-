package com.codeway.daoTemplate.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Abhishek Pandey
 *         <br>
 *         Copyright (c) Abhishek Pandey
 *         <br>
 *         <br>
 *         Licensed under the Apache License, Version 2.0 (the "License");
 *         you may not use this file except in compliance with the License.
 *         You may obtain a copy of the License at
 *         <br>
 *         <br>
 *         http://www.apache.org/licenses/LICENSE-2.0
 *         <br>
 *         <br>
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *         implied.
 *         See the License for the specific language governing permissions and
 *         limitations under the License.
 */
public class TemplateConfiguration {

	private static Map<String, String> map;

	public static String getString(String key) throws Exception {
		if (map == null)
			loadProperties();
		return map.get(key);
	}

	public static boolean getBoolean(String key) {
		try {
			String val = getString(key);
			return val != null && val.equalsIgnoreCase("true");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void loadProperties() throws Exception {

		map = new HashMap<>();
		InputStream is = TemplateConfiguration.class.getClassLoader().getResourceAsStream("template-dao.properties");

		if (is == null) {
			throw new Exception("template-dao.properties file not found in class path");
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;
		while ((line = br.readLine()) != null) {
			// Abaikan baris kosong atau komentar (opsional jika ada #)
			if (line.trim().isEmpty() || !line.contains("="))
				continue;

			// FIX: Gunakan limit 2 agar split hanya membagi pada '=' pertama
			// dan limit -1 (atau penanganan manual) tidak diperlukan jika kita cek length
			String[] parts = line.split("=", 2);

			String key = parts[0].trim();
			String value = "";

			// Cek apakah ada value setelah tanda '='
			if (parts.length > 1) {
				value = parts[1].trim();
			}

			map.put(key, value);
		}
	}

}
