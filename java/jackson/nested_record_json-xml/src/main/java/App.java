
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class App {
    public static void main(String... args) {
        test(
            new Data1("data1", 1, List.of(new Item1("item1"), new Item1("item2")))
        );

        test(
            new Data2("data2", 2, List.of("item1", "item2"))
        );

        test(
            new Data3("data3", 3, List.of("item1", "item2"))
        );
    }

    private static <T> void test(T data) {
        var mapper = new ObjectMapper();
        var mapper2 = new XmlMapper();

        System.out.println("----------");

        try {
            var s1 = mapper.writeValueAsString(data);

            System.out.println("[json]");

            System.out.println(s1);
            System.out.println(mapper.readValue(s1, data.getClass()));

            System.out.println("[xml]");

            var s2 = mapper2.writeValueAsString(data);
            System.out.println(s2);

            System.out.println(mapper2.readValue(s2, data.getClass()));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    record Data1(String name, int value, List<Item1> items) {}
    record Item1(String id) {}

    record Data2(
            String name,
            int value,
            @JacksonXmlElementWrapper(localName = "items")
            @JacksonXmlProperty(localName = "id")
            List<String> items) {}

    static class Data3 {
        public String name;
        public int value;
        @JacksonXmlElementWrapper(localName = "items")
        @JacksonXmlProperty(localName = "id")
        public List<String> items;

        public Data3() {}

        public Data3(String name, int value, List<String> items) {
            this.name = name;
            this.value = value;
            this.items = items;
        }

        @Override
        public String toString() {
            return "Data3: name=" + name + ", value=" + value + ", items=" + items;
        }
    }
}
