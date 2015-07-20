package sample.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@ToString(exclude = "product")
@Entity
@Table(name = "product_variation")
public class ProductVariation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String color;
	private String size;

	@ManyToOne
	private Product product;
}
