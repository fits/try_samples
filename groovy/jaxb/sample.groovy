
import javax.xml.bind.JAXBContext
import javax.xml.bind.annotation.*

//groovy ‚ÌƒNƒ‰ƒX‚ªŽ×–‚‚·‚é‚Ì‚Å @XmlAccessorType(XmlAccessType.NONE) ‚ðŽg‚¤
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Customer {
	@XmlElement
	String id

	@XmlElement
	String name
}

def ctx = JAXBContext.newInstance(Customer.class)

ctx.createMarshaller().marshal(new Customer(id: "id:1", name: "test"), System.out)

