package sample;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import sample.dao.ProductRepository;
import sample.model.Product;

import java.math.BigDecimal;
import java.util.Date;

@ComponentScan
@EnableAutoConfiguration
public class SampleApp implements CommandLineRunner {
    @Autowired
    ProductRepository dao;

    @Override
    public void run(String... args) {
        val key = "s2";

        System.out.println("no data: " + dao.load(key));

        val p = new Product(key, "sample", new BigDecimal("1200"), new Date());

        dao.save(p);

        System.out.println("data: " + dao.load(key));

        dao.update(key, prod -> {
            prod.setName("sample-update");
            return prod;
        });

        System.out.println("data: " + dao.load(key));

    }

    public static void main(String... args) {
        SpringApplication.run(SampleApp.class, args);
    }
}
