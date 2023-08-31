package com.publicis.sapient.p2p.service;

import com.publicis.sapient.p2p.exception.BusinessException;
import com.publicis.sapient.p2p.exception.util.ErrorCode;
import com.publicis.sapient.p2p.exception.util.ErrorResolver;
import com.publicis.sapient.p2p.model.Product;
import com.publicis.sapient.p2p.util.DocumentMapper;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.MessageFormat;

@Service
public class IndexingServiceImpl implements IndexingService {

    private final Logger logger = LoggerFactory.getLogger(IndexingServiceImpl.class);
    @Autowired
    ErrorResolver errorResolver;
    private static final String ERR_DETAIL_NOT_FOUND = "ERR_DETAIL_NOT_FOUND";
    private static final String ERR_SAVING_DATA = "ERR_SAVING_DATA";
    private static final String ERR_DETAIL_NOT_FOUND_MESSAGE =  "Product not found with ID : {0}";
    private static final String ERR_SERVICE = "Solr service is unavailable";
    private static final String ERR_UPDATE_FAILURE = "Document is failed to be processed...";
    private static final String ERR_INDEX_FAILURE = "Document is failed to be indexed...";

    @Autowired
    SolrClient solr;

    String status = "status";

    @Override
    public String addDocument(Product product) {

        logger.info("Entered Index Service Impl add document");
        String result;
        try {
            SolrInputDocument doc = DocumentMapper.mapToDocument(product);
            UpdateResponse response = this.solr.add(doc);
            if (response.getResponseHeader().get(status).toString().equals("0")) {
                result = "Document is indexed...";
                logger.info("Document is indexed.");
            } else {
                logger.error(errorResolver.getErrorMessage(ERR_SAVING_DATA, ERR_INDEX_FAILURE));
                throw new BusinessException(ErrorCode.BAD_REQUEST, errorResolver.getErrorMessage(ERR_SAVING_DATA, ERR_INDEX_FAILURE));
            }
            solr.commit();
        }
        catch (SolrServerException | IOException e) {
            logger.error(errorResolver.getErrorMessage(ERR_SAVING_DATA, ERR_SERVICE));
            throw new BusinessException(ErrorCode.SERVICE_NOT_AVAILABLE, errorResolver.getErrorMessage(ERR_SAVING_DATA, ERR_SERVICE));
        }
        return result;
    }

    @Override
    public String updateDocument(String id, Product product) {

        logger.info("Entered Index Service Impl update document");
        String updateResult;
        try {
            if(solr.getById(id) == null) {
                logger.error( errorResolver.getErrorMessage(ERR_DETAIL_NOT_FOUND, MessageFormat.format(ERR_DETAIL_NOT_FOUND_MESSAGE, id)));
                throw new BusinessException(ErrorCode.BAD_REQUEST, errorResolver.getErrorMessage(ERR_DETAIL_NOT_FOUND, MessageFormat.format(ERR_DETAIL_NOT_FOUND_MESSAGE, id)));
            } else {

                product.setId(id);
                SolrInputDocument doc = DocumentMapper.mapToDocument(product);
                UpdateResponse response = this.solr.add(doc);

                if (response.getResponseHeader().get(status).toString().equals("0")) {
                    updateResult = "Document is updated...";
                    logger.info("Document is updated.");
                } else {
                    logger.error(errorResolver.getErrorMessage(ERR_SAVING_DATA, ERR_UPDATE_FAILURE));
                    throw new BusinessException(ErrorCode.BAD_REQUEST, errorResolver.getErrorMessage(ERR_SAVING_DATA, ERR_UPDATE_FAILURE));
                }
            }
            solr.commit();
        }
        catch (SolrServerException | IOException e) {
            logger.error(errorResolver.getErrorMessage(ERR_SAVING_DATA, ERR_SERVICE));
            throw new BusinessException(ErrorCode.SERVICE_NOT_AVAILABLE, errorResolver.getErrorMessage(ERR_SAVING_DATA, ERR_SERVICE));
        }
        return updateResult;
    }

    @Override
    public String deleteDocument(String id) {

        logger.info("Entered Index Service Impl delete document");
        String deleteResult;
        try{
            if(solr.getById(id) == null) {
                logger.error(errorResolver.getErrorMessage(ERR_DETAIL_NOT_FOUND, MessageFormat.format(ERR_DETAIL_NOT_FOUND_MESSAGE, id)));
                throw new BusinessException(ErrorCode.BAD_REQUEST, errorResolver.getErrorMessage(ERR_DETAIL_NOT_FOUND, MessageFormat.format(ERR_DETAIL_NOT_FOUND_MESSAGE, id)));
            }
            else {
                UpdateResponse response = this.solr.deleteById(id);

                if (response.getResponseHeader().get(status).toString().equals("0")) {
                    deleteResult = "Document is deleted...";
                    logger.info("Document is deleted.");
                } else {
                    logger.error(errorResolver.getErrorMessage(ERR_SAVING_DATA, ERR_UPDATE_FAILURE));
                    throw new BusinessException(ErrorCode.BAD_REQUEST, errorResolver.getErrorMessage(ERR_SAVING_DATA, ERR_UPDATE_FAILURE));
                }
                solr.commit();
            }
        }
        catch (SolrServerException | IOException e) {
            logger.error(errorResolver.getErrorMessage(ERR_SAVING_DATA, ERR_SERVICE));
            throw new BusinessException(ErrorCode.SERVICE_NOT_AVAILABLE, errorResolver.getErrorMessage(ERR_SAVING_DATA, ERR_SERVICE));
        }
        return deleteResult;
    }

}
