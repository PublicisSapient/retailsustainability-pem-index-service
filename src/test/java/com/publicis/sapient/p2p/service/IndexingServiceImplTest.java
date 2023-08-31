package com.publicis.sapient.p2p.service;

import com.publicis.sapient.p2p.exception.BusinessException;
import com.publicis.sapient.p2p.exception.util.ErrorCode;
import com.publicis.sapient.p2p.model.*;
import com.publicis.sapient.p2p.exception.util.ErrorResolver;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {IndexingServiceImpl.class})
@ExtendWith(SpringExtension.class)

class IndexingServiceImplTest {

    @Autowired
    IndexingServiceImpl indexingService;

    @MockBean
    SolrClient solrClient;

    @MockBean
    ErrorResolver errorResolver;

    @Test
    @Order(1)
    void addDocTest() throws SolrServerException, IOException {

        Product product = new Product("3333", "name", "description", Category.CLOTH,
                OfferType.GIVEAWAY, Arrays.asList("image1.jpg", "image2.jpg"), "location",
                new GeoLocation("40.7128", "-74.0060"), "2", "12121", new Date());

        UpdateResponse up = mock(UpdateResponse.class);
        when(solrClient.add(any(SolrInputDocument.class))).thenReturn(up);
        SimpleOrderedMap<Object> header = new SimpleOrderedMap<>();
        header.add("status", 0);
        when(up.getResponseHeader()).thenReturn(header);

        String response = indexingService.addDocument(product);
        verify(solrClient).commit();

        Assertions.assertEquals("Document is indexed...", response);

    }

    @Test
    @Order(2)
    void addDocTest_WithException() throws SolrServerException, IOException {

        Product product = new Product("3333", "name", "description", Category.CLOTH,
                OfferType.GIVEAWAY, Arrays.asList("image1.jpg", "image2.jpg"), "location",
                new GeoLocation("40.7128", "-74.0060"), "2", "12121", new Date());

        UpdateResponse up = mock(UpdateResponse.class);
        when(solrClient.add(any(SolrInputDocument.class))).thenReturn(up);
        SimpleOrderedMap<Object> header = new SimpleOrderedMap<>();
        header.add("status", 1);
        when(up.getResponseHeader()).thenReturn(header);

        BusinessException ex = Assertions.assertThrows(BusinessException.class, () -> indexingService.addDocument(product));
        assertEquals(ErrorCode.BAD_REQUEST, ex.getErrorCode());
    }

    @Test
    @Order(3)
    void updateDocTest() throws SolrServerException, IOException {

        Product product = new Product("3333", "name", "description", Category.CLOTH,
                OfferType.GIVEAWAY, Arrays.asList("image1.jpg", "image2.jpg"), "location",
                new GeoLocation("40.7128", "-74.0060"), "2", "12121", new Date());

        SolrDocument doc = mock(SolrDocument.class);
        when(solrClient.getById("3333")).thenReturn(doc);
        UpdateResponse up = mock(UpdateResponse.class);
        when(solrClient.add(any(SolrInputDocument.class))).thenReturn(up);
        SimpleOrderedMap<Object> header = new SimpleOrderedMap<>();
        header.add("status", 0);
        when(up.getResponseHeader()).thenReturn(header);

        String response = indexingService.updateDocument("3333",product);
        verify(solrClient).commit();

        Assertions.assertEquals("Document is updated...", response);
    }

    @Test
    @Order(4)
    void updateDocTest_WithException() throws SolrServerException, IOException {

        Product product = new Product("3333", "name", "description", Category.CLOTH,
                OfferType.GIVEAWAY, Arrays.asList("image1.jpg", "image2.jpg"), "location",
                new GeoLocation("40.7128", "-74.0060"), "2", "12121", new Date());

        SolrDocument doc = mock(SolrDocument.class);
        when(solrClient.getById("3333")).thenReturn(doc);
        UpdateResponse up = mock(UpdateResponse.class);
        when(solrClient.add(any(SolrInputDocument.class))).thenReturn(up);
        SimpleOrderedMap<Object> header = new SimpleOrderedMap<>();
        header.add("status", 1);
        when(up.getResponseHeader()).thenReturn(header);

        BusinessException ex = Assertions.assertThrows(BusinessException.class, () -> indexingService.updateDocument("3333",product));
        assertEquals(ErrorCode.BAD_REQUEST, ex.getErrorCode());
    }

    @Test
    @Order(5)
    void deleteDocTest() throws SolrServerException, IOException {

        String id="333";
        SolrDocument doc = mock(SolrDocument.class);
        when(solrClient.getById(id)).thenReturn(doc);
        UpdateResponse up = mock(UpdateResponse.class);
        when(solrClient.deleteById(id)).thenReturn(up);
        SimpleOrderedMap<Object> header = new SimpleOrderedMap<>();
        header.add("status", 0);
        when(up.getResponseHeader()).thenReturn(header);

        String response = indexingService.deleteDocument("333");
        Assertions.assertEquals("Document is deleted...", response);
    }

    @Test
    @Order(6)
    void deleteDocTest_WithException() throws SolrServerException, IOException {

        String id="333";
        SolrDocument doc = mock(SolrDocument.class);
        when(solrClient.getById(id)).thenReturn(doc);
        UpdateResponse up = mock(UpdateResponse.class);
        when(solrClient.deleteById(id)).thenReturn(up);
        SimpleOrderedMap<Object> header = new SimpleOrderedMap<>();
        header.add("status", 1);
        when(up.getResponseHeader()).thenReturn(header);

        BusinessException ex = Assertions.assertThrows(BusinessException.class, () -> indexingService.deleteDocument(id));
        assertEquals(ErrorCode.BAD_REQUEST, ex.getErrorCode());
    }

    @Test
    @Order(7)
    void NoIdFoundException() throws SolrServerException, IOException {

        Product product = mock(Product.class);
        String id="333";
        when(solrClient.getById(id)).thenReturn(null);

        BusinessException ex1 = Assertions.assertThrows(BusinessException.class, () -> indexingService.updateDocument(id,product));
        BusinessException ex2 = Assertions.assertThrows(BusinessException.class, () -> indexingService.deleteDocument(id));
        assertEquals(ErrorCode.BAD_REQUEST, ex1.getErrorCode());
        assertEquals(ErrorCode.BAD_REQUEST, ex2.getErrorCode());
    }

    @Test
    @Order(8)
    void ServerUnavailabilityException() throws SolrServerException, IOException {

        Product product = new Product("333", "name", "description", Category.CLOTH,
                OfferType.GIVEAWAY, Arrays.asList("image1.jpg", "image2.jpg"), "location",
                new GeoLocation("40.7128", "-74.0060"), "2", "12121", new Date());
        doThrow(new SolrServerException("Solr server error")).when(solrClient).add(any(SolrInputDocument.class));

        String id = "333";
        SolrDocument doc1 = mock(SolrDocument.class);
        when(solrClient.getById(id)).thenReturn(doc1);
        when(solrClient.deleteById(id)).thenThrow(new SolrServerException("exception"));

        BusinessException ex1 = Assertions.assertThrows(BusinessException.class, () -> indexingService.addDocument(product));
        BusinessException ex2 = Assertions.assertThrows(BusinessException.class, () -> indexingService.updateDocument(id,product));
        BusinessException ex3 = Assertions.assertThrows(BusinessException.class, () -> indexingService.deleteDocument(id));

        assertEquals(ErrorCode.SERVICE_NOT_AVAILABLE, ex1.getErrorCode());
        assertEquals(ErrorCode.SERVICE_NOT_AVAILABLE, ex2.getErrorCode());
        assertEquals(ErrorCode.SERVICE_NOT_AVAILABLE, ex3.getErrorCode());
    }

}
