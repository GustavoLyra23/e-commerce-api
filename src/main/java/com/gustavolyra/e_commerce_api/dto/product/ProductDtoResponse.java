package com.gustavolyra.e_commerce_api.dto.product;

import com.gustavolyra.e_commerce_api.entities.Product;
import com.gustavolyra.e_commerce_api.enums.ProductType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor
@Getter
public class ProductDtoResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private UUID id;
    private String name;
    private String description;
    private ProductType productType;
    private String seller;
    private String pictureUrl;
    private Integer stock;
    private Double price;

    public ProductDtoResponse(Product product) {
        id = product.getUuid();
        name = product.getName();
        description = product.getDescription();
        productType = product.getType();
        seller = product.getUser().getUsername();
        pictureUrl = product.getProductPictueUrl();
        stock = product.getStock();
        price = product.getPrice();
    }


}
