package sample.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
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
	@OneToMany(mappedBy = "product", cascade= CascadeType.ALL)
	private List<ProductVariation> variationList = new ArrayList<>();
}
