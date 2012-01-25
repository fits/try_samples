import java.util.*;
import javax.xml.bind.annotation.*;

@XmlRootElement
public class User {
	public String id;
	public String name;
	public List<Mail> mail = new ArrayList<>();

	public User() {
	}

	public User(String id, String name) {
		this.id = id;
		this.name = name;
	}
}
