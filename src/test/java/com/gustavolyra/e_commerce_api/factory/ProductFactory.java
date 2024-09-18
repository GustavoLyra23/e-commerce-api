package com.gustavolyra.e_commerce_api.factory;

import com.gustavolyra.e_commerce_api.dto.product.ProductDtoRequest;
import com.gustavolyra.e_commerce_api.entities.Product;
import com.gustavolyra.e_commerce_api.enums.ProductType;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class ProductFactory {

    public static Product createProduct() {
        return new Product(UUID.fromString("97e630e3-637a-4930-b4e9-8fcf48fda319"),
                "Product Test", "description",
                100.0, ProductType.ELETRONICS, 10,
                null, "https://url-de-exemplo.com", UserFactory.createAdminUser());
    }

    public static ProductDtoRequest createProductDtoRequest() {
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        var product = createProduct();
        return new ProductDtoRequest(product.getName(), product.getDescription(),
                product.getPrice(), String.valueOf(product.getType()), product.getStock(), mockFile);
    }

    public static ProductDtoRequest createInvalidTypeProductDtoRequest() {
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        var product = createProduct();
        return new ProductDtoRequest(product.getName(), product.getDescription(),
                product.getPrice(), "Invalid Type", product.getStock(), mockFile);
    }


}
