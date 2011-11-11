import java.io.*;
import java.util.*;

import groovy.lang.*;
import groovy.text.*;

public class Sample {
	public static void main(String[] args) throws Exception {

		GStringTemplateEngine eng = new GStringTemplateEngine();

		Map<String, TestData> param = new HashMap<String, TestData>();
		TestData data = new TestData();
		data.name = "テストデータ";
		data.itemList.add(new ItemData("詳細1"));
		data.itemList.add(new ItemData("詳細2"));
		data.itemList.add(new ItemData("チェックデータ"));

		param.put("data", data);

		Template temp = eng.createTemplate(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"));

		StringWriter sw = new StringWriter();

		Writable writer = temp.make(param);
		writer.writeTo(sw);

		System.out.println(sw.toString());
	}

	static class TestData {
		String name;
		List<ItemData> itemList = new ArrayList<ItemData>();
	}

	static class ItemData {
		String itemName;

		ItemData(String itemName) {
			this.itemName = itemName;
		}
	}

}