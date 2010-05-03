package sample.data;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Customer {
	public int id;
	public String name;
	public Date registeredDate;
}