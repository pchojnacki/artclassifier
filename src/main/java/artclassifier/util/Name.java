package artclassifier.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

public class Name {

	private static Set<String> names = new HashSet<>();

	public static boolean isName(String str) {
		return names.contains(str);
	}

	static {
		// TODO: ability to configure path
		String namesFilePath = "src/main/resources/names/names.txt";

		try (BufferedReader br = new BufferedReader(new FileReader(namesFilePath))) {

			String s;

			while ((s = br.readLine()) != null) {
				names.add(s);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
