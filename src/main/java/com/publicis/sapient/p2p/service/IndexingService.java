package com.publicis.sapient.p2p.service;

import com.publicis.sapient.p2p.model.Product;

public interface IndexingService {

    String addDocument(Product product);

    String updateDocument(String id, Product product);

    String deleteDocument(String id);
}
