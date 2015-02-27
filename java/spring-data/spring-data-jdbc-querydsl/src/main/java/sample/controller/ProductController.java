package sample.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMethod;
import sample.data.Product;
import sample.service.ProductService;

import java.sql.Date;

@Controller
public class ProductController {
    @Autowired
    private ProductService productService;

    @RequestMapping("/product/{id}")
    public String product(@PathVariable("id") int id, Model model) {
        Product product = productService.findById(id);

        model.addAttribute("product", product);

        return "product";
    }

    @RequestMapping(value = "/product", method = RequestMethod.GET)
    public String list(Model model) {
        model.addAttribute("products", productService.findAll());

        return "product_all";
    }

    @RequestMapping(value = "/product", method = RequestMethod.POST)
    public String add(Product data, Model model) {
        productService.add(data);

        return list(model);
    }
}
