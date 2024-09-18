package com.gustavolyra.e_commerce_api.dto.product;

import com.gustavolyra.e_commerce_api.dto.comments.CommentDto;
import com.gustavolyra.e_commerce_api.entities.Product;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class ProductDtoWithComments implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private UUID id;
    private String name;
    private String description;
    private String productType;
    private String seller;
    private String pictureUrl;
    private int stock;
    private List<CommentDto> comments;

    public ProductDtoWithComments(Product product) {
        this.id = product.getUuid();
        this.name = product.getName();
        this.description = product.getDescription();
        this.productType = product.getType().name();
        this.seller = product.getUser().getUsername();
        this.pictureUrl = product.getProductPictueUrl();
        this.stock = product.getStock();
        this.comments = product.getComments().stream()
                .filter(comment -> comment.getParentComment() == null)
                .map(CommentDto::new)
                .collect(Collectors.toList());
    }

}