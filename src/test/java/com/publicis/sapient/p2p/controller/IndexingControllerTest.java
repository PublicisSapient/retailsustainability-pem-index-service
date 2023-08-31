package com.publicis.sapient.p2p.controller;

import com.publicis.sapient.p2p.model.Product;
import com.publicis.sapient.p2p.model.ServiceResponseDto;
import com.publicis.sapient.p2p.service.IndexingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {IndexingController.class})
@ExtendWith(SpringExtension.class)
class IndexingControllerTest {

    @MockBean
    IndexingService indexingService;

    @Autowired
    IndexingController indexingController;

    @Test
    @Order(1)
    void addDocTest() {

        Product product = mock(Product.class);
        when(indexingService.addDocument(any(Product.class))).thenReturn("Document is indexed...");
        ServiceResponseDto response = indexingController.addDocument(product);
        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertEquals("Document is indexed...", response.getMessage());
    }

    @Test
    @Order(2)
    void updateDocTest() {

        Product product = mock(Product.class);
        when(indexingService.updateDocument(any(String.class), any(Product.class))).thenReturn("Document is updated...");
        ServiceResponseDto response = indexingController.updateDocument("1", product);
        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertEquals("Document is updated...", response.getMessage());
    }

    @Test
    @Order(3)
    void deleteDocTest() {

        when(indexingService.deleteDocument(any(String.class))).thenReturn("Document is deleted...");
        ServiceResponseDto response = indexingController.deleteDocument("1");
        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertEquals("Document is deleted...", response.getMessage());
    }
}
