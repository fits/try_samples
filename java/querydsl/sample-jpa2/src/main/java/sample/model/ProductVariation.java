package sample.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "product_variation")
public class ProductVariation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String color;
	private String size;
}
