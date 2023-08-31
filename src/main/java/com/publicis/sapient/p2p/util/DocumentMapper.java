package com.publicis.sapient.p2p.util;

import com.publicis.sapient.p2p.model.Product;
import org.apache.solr.common.SolrInputDocument;

public class DocumentMapper {

    private DocumentMapper(){

    }

    public static SolrInputDocument mapToDocument(Product product) {

        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", product.getId());
        doc.addField("name", product.getName());
        doc.addField("description", product.getDescription());
        doc.addField("category", product.getCategory().toString());
        doc.addField("offerType", product.getOfferType().toString());
        doc.addField("images", product.getImages());
        doc.addField("user", product.getUser());
        doc.addField("location", product.getLocation());
        doc.addField("geoLocation", product.getGeoLocation().getLatitude() + "," + product.getGeoLocation().getLongitude());
        doc.addField("createdTime", product.getCreatedTime());
        doc.addField("price", product.getPrice());

        return doc;
    }
}
