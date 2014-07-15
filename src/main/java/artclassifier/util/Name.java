package artclassifier.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

public class Name {

	private static final Logger log = Logger.getLogger(Name.class);

	private static Set<String> names = new HashSet<>();

	public static boolean isName(String str) {
		return names.contains(str);
	}

	static {
		String namesFilePath = "/names.txt";
		InputStream is = Name.class.getResourceAsStream(namesFilePath);

		try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
			String s;

			while ((s = br.readLine()) != null) {
				names.add(s);
			}

		} catch (Exception e) {
			log.error("Error when reading names from file", e);
		}
	}

}
