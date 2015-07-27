package sample.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
public class Product implements Serializable{
    private String id;
    private String name;
    private BigDecimal price;
    private Date registeredDate;
}
