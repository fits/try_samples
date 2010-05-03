package sample.resources;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import sample.data.Customer;

@Path("customer")
public class CustomerResource {

	@GET
	@Path("{id}/json")
	@ProduceMime("application/json")
	public Customer getCustomerAsJson(@PathParam("id") int id) {
		return this.createCustomer(id, "ABCŠ”Ž®‰ïŽÐ");
	}

	@GET
	@Path("{id}/xml")
	@ProduceMime("application/xml")
	public Customer getCustomerAsXml(@PathParam("id") int id) {
		return this.createCustomer(id, "Š”Ž®‰ïŽÐƒTƒ“ƒvƒ‹");
	}

	private Customer createCustomer(int id, String name) {

		Customer result = new Customer();
		result.id = id;
		result.name = name;
		result.registeredDate = new Date();

		return result;
	}

}