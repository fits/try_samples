import javax.xml.bind.JAXB;

public class TestMain {

	public static void main(String[] args) {
		User user = new User("a1", "ƒeƒXƒg");
		user.mail.add(new Mail("pc", "aaa@bb.co.jp"));
		user.mail.add(new Mail("mobile", "bbb@c.co.jp"));

		JAXB.marshal(user, System.out);
	}

}
