package sample.service;

import com.mysema.query.sql.SQLQuery;
import com.mysema.query.types.Projections;
import com.mysema.query.types.QBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.query.QueryDslJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

import org.springframework.transaction.annotation.Transactional;
import sample.data.Product;
import sample.entity.QProduct;

import java.util.List;

@Service
public class ProductService {
    private QueryDslJdbcTemplate template;
    private QProduct p = QProduct.product;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.template = new QueryDslJdbcTemplate(dataSource);
    }

    public Product findById(long id) {
        SQLQuery q = fromProduct().where(p.id.eq(id));

        return template.queryForObject(q, toProduct());
    }

    public List<Product> findAll() {
        return template.query(fromProduct(), toProduct());
    }

    @Transactional
    public void add(Product data) {
		template.insert(p, cl -> {
			return cl.columns(p.id, p.name, p.price, p.releaseDate)
					.values(data.getId(), data.getName(), data.getPrice(), data.getReleaseDate())
					.execute();
		});
	}

    private SQLQuery fromProduct() {
        return template.newSqlQuery().from(p);
    }

    private QBean<Product> toProduct() {
        return Projections.bean(Product.class, p.id, p.name, p.price, p.releaseDate);
    }
}
