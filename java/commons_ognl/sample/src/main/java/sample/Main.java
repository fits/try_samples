package sample;

import java.math.BigDecimal;
import java.util.*;
import org.apache.commons.ognl.Ognl;

public class Main {
	public static void main(String... args) throws Exception {
		Sample data = new Sample();
		data.getLines().add(new Item(1, new BigDecimal("100")));
		data.getLines().add(new Item(2, new BigDecimal("200")));
		data.getLines().add(new Item(3, new BigDecimal("300")));

		// map 処理 ArrayList[false, true, false]
		System.out.println(Ognl.getValue("lines.{ #this.code == 2 }", data));

		// filter 処理 code == 2 のオブジェクト(code=2)を取得（ArrayList）
		System.out.println(Ognl.getValue("lines.{? #this.code == 2 }", data));

		// 条件に合致する最初のオブジェクト(code=2)を取得
		System.out.println(Ognl.getValue("lines.{^ #this.code > 1 }", data));

		// 条件に合致する最後のオブジェクト(code=3)を取得
		System.out.println(Ognl.getValue("lines.{$ #this.code > 1 }", data));

		// 条件に合致するオブジェクト(cod=1, code=3)を取得
		System.out.println(Ognl.getValue("lines.{? #this.code in {1, 3} }", data));

		// true
		System.out.println(Ognl.getValue("lines.{? #this.code in {1, 3} }.size() > 1", data));

		System.out.println(Ognl.getValue("lines.{? #this.price > 150 }", data));
		System.out.println(Ognl.getValue("true", data));
	}

	static class Sample {
		private List<Item> lines = new ArrayList<Item>();

		public List<Item> getLines() {
			return lines;
		}
	}

	static class Item {
		private int code;
		private BigDecimal price = BigDecimal.ZERO;

		public Item(int code) {
			this.code = code;
		}

		public Item(int code, BigDecimal price) {
			this.code = code;
			this.price = price;
		}

		public int getCode() {
			return code;
		}

		public BigDecimal getPrice() {
			return price;
		}

		@Override
		public String toString() {
			return "Item(code=" + code + ")";
		}
	}
}
