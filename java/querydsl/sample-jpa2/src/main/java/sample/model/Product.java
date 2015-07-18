package sample.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private BigDecimal price;

	@Setter(AccessLevel.NONE)
	@OneToMany(fetch = FetchType.EAGER, cascade= CascadeType.ALL)
	@JoinColumn(name = "product_id")
	private List<ProductVariation> variationList = new ArrayList<>();
}
