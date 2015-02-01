package sample.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import sample.model.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {

	List<Product> findByPriceLessThanEqual(BigDecimal price);
}