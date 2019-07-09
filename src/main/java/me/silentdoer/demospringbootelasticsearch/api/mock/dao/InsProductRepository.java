package me.silentdoer.demospringbootelasticsearch.api.mock.dao;

import me.silentdoer.demospringbootelasticsearch.api.mock.model.InsProduct;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 这里自定义的方法命名是有规范的,比如and/or/between之类的;
 */
@Repository
public interface InsProductRepository extends ElasticsearchRepository<InsProduct, Long> {

    List<InsProduct> findAllByTitle(String title);

    List<InsProduct> findByPremiumBetween(double premium1, double premium2);
}
