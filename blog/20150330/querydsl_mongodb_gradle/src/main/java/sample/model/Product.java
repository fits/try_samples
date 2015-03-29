package sample.model;

import org.mongodb.morphia.annotations.*;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Product {
	@Id
	private ObjectId id;

	private String name;

	@Embedded
	private List<Variation> variationList = new ArrayList<>();

	public Product() {
	}

	public Product(String name) {
		setName(name);
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List<Variation> getVariationList() {
		return variationList;
	}
}
