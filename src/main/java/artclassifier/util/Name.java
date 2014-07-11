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
		try (BufferedReader br = new BufferedReader(
				new FileReader("/Users/yura/workspaces/artclassifier/src/main/resources/names/names.txt"))) {

			String s;

			while ((s = br.readLine()) != null) {
				names.add(s);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
