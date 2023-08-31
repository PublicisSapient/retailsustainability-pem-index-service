package com.publicis.sapient.p2p.controller;

import com.publicis.sapient.p2p.model.ServiceResponseDto;
import com.publicis.sapient.p2p.model.Product;
import com.publicis.sapient.p2p.service.IndexingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/index")
public class IndexingController {

    private final Logger logger = LoggerFactory.getLogger(IndexingController.class);
    @Autowired
    IndexingService indexingService;

    @PostMapping
    public ServiceResponseDto addDocument(@RequestBody Product product) {

        logger.info("Entered Index Controller add document");
        String message = indexingService.addDocument(product);
        return new ServiceResponseDto(200, message, null);
    }

    @PutMapping("/{id}")
    public ServiceResponseDto updateDocument(@PathVariable("id") String id, @RequestBody Product product) {

        logger.info("Entered Index Controller update document");
        String message = indexingService.updateDocument(id,product);
        return new ServiceResponseDto(200, message, null);
    }

    @DeleteMapping("/{id}")
    public ServiceResponseDto deleteDocument(@PathVariable("id") String id) {

        logger.info("Entered Index Controller delete document");
        String message = indexingService.deleteDocument(id);
        return new ServiceResponseDto(200, message, null);
    }
}






