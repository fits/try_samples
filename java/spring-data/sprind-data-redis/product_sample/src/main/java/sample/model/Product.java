package sample.model;

import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Value
public class Product implements Serializable{
    private String id;
    private String name;
    private BigDecimal price;
    private Date registeredDate;
}
