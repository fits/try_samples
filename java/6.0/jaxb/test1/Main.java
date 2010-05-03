
import javax.xml.bind.*;

public class Main {

	public static void main(String[] args) throws Exception {

		JAXBContext ctx = JAXBContext.newInstance(Data.class);

		Marshaller ms = ctx.createMarshaller();

		ms.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		Data<Integer> data = new Data<Integer>("test", 100);
		data.setDetail(new Item("abc"));
		data.setValue(100000);

		ms.marshal(data, System.out);

		System.out.println();

		Data<String> data2 = new Data<String>("test2", 2);
		data2.setValue("aaaaaaaaaaaa");

		ms.marshal(data2, System.out);

		System.out.println();

		Data<Item> data3 = new Data<Item>("test3", 3);
		data3.setValue(new Item("item3"));

		ms.marshal(data3, System.out);

	}
}