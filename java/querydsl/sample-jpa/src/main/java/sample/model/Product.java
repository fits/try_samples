package sample.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Data
@Entity
public class Product {
	@Id
	private Long id;
	private String name;
	private BigDecimal price;
}
