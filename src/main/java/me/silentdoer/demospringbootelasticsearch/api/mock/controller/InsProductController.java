package me.silentdoer.demospringbootelasticsearch.api.mock.controller;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import me.silentdoer.demospringbootelasticsearch.api.ApiResult;
import me.silentdoer.demospringbootelasticsearch.api.ApiResultEnum;
import me.silentdoer.demospringbootelasticsearch.api.mock.dao.InsProductRepository;
import me.silentdoer.demospringbootelasticsearch.api.mock.model.InsProduct;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.DisMaxQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mock/ins_product")
@Slf4j
public class InsProductController {

    private static final Integer PAGE_NUM = 10;

    @Resource
    private InsProductRepository insProductRepository;

    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

    @DeleteMapping("/id/{id}")
    public ApiResult<String> delete(@PathVariable String id) {

        String deletedId = this.elasticsearchTemplate.delete(InsProduct.class, id);
        return ApiResultEnum.SUCCESS.getResult(deletedId);
    }

    @PutMapping("/id/{id}")
    public ApiResult<String> update(@PathVariable String id, @RequestBody @Valid InsProduct product) {

        UpdateQuery updateQuery = new UpdateQuery();
        updateQuery.setId(id);
        updateQuery.setClazz(InsProduct.class);
        product.setId(null);
        UpdateRequest request = new UpdateRequest();
        request.doc(JSON.toJSONString(product), XContentType.JSON);
        updateQuery.setUpdateRequest(request);
        UpdateResponse response = elasticsearchTemplate.update(updateQuery);

        return ApiResultEnum.SUCCESS.getResult(response.getId());
        // 不需要状态判断,没有数据es自己就会抛异常
        /*if (response.status() == RestStatus.OK) {
            return ApiResultEnum.SUCCESS.getResult(response.getId());
        } else {
            throw new RuntimeException("没有此数据");
        }*/
    }

    @PostMapping("/save")
    public ApiResult<InsProduct> save2(@RequestBody @Valid InsProduct product) {

        product.setId(String.valueOf(System.currentTimeMillis()));
        product.setChannel("齐欣");
        product.setCoverage(300000.0);
        //product.setDescription("此保险产品适合。。。");
        product.setFromCompany("中国人寿");
        product.setPremium(1280.0);
        product.setSerialCode(UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());

        InsProduct saved = this.insProductRepository.save(product);

        return ApiResultEnum.SUCCESS.getResult(saved);
    }

    @PostMapping
    public ApiResult<String> save(@RequestBody @Valid InsProduct product) {

        //product.setId(String.valueOf(System.currentTimeMillis()));
        product.setChannel("小雨伞");
        product.setCoverage(350000.0);
        product.setDescription("此保险产品适合XX人群购买,。。。");
        product.setFromCompany("平安保险");
        product.setPremium(1230.0);
        product.setSerialCode(UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());

        IndexQuery indexQuery = new IndexQueryBuilder().withId(product.getId()).withObject(product).build();
        elasticsearchTemplate.index(indexQuery);

        String index = this.elasticsearchTemplate.index(indexQuery);

        return ApiResultEnum.SUCCESS.getResult(index);
    }

    @GetMapping("/title/{title}")
    public ApiResult<List<InsProduct>> patchQuery(@PathVariable String title) {

        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(structureQuery(title)).build();
        List<InsProduct> content = this.insProductRepository.search(searchQuery).getContent();

        return ApiResultEnum.SUCCESS.getResult(content);
    }

    private DisMaxQueryBuilder structureQuery(String title) {
        //使用dis_max直接取多个query中，分数最高的那一个query的分数即可
        DisMaxQueryBuilder disMaxQueryBuilder = QueryBuilders.disMaxQuery();
        //boost 设置权重,只搜索匹配name和disrector字段
        QueryBuilder ikNameQuery = QueryBuilders.matchQuery("title", title);//.boost(2f);
        //QueryBuilder pinyinNameQuery = QueryBuilders.matchQuery("name.pinyin", content);
        //QueryBuilder ikDirectorQuery = QueryBuilders.matchQuery("director", content).boost(2f);
        disMaxQueryBuilder.add(ikNameQuery);
        //disMaxQueryBuilder.add(pinyinNameQuery);
        //disMaxQueryBuilder.add(ikDirectorQuery);
        return disMaxQueryBuilder;
    }

    @GetMapping("/page/{page}")
    public ApiResult<List<InsProduct>> page(@PathVariable Integer page) {

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                //.withQuery(QueryBuilders.boolQuery().must(QueryBuilders.matchPhraseQuery("authorList.name", "张三")))
                .withPageable(PageRequest.of(page, PAGE_NUM)) //加上分页参数
                .build();
        List<InsProduct> collect = this.elasticsearchTemplate.queryForPage(searchQuery, InsProduct.class).get().collect(Collectors.toList());
        return ApiResultEnum.SUCCESS.getResult(collect);
    }

    @GetMapping("/id/{id}")
    public ApiResult<InsProduct> search(@PathVariable String id) {

        GetQuery query = new GetQuery();
        query.setId(id);
        InsProduct result = this.elasticsearchTemplate.queryForObject(query, InsProduct.class);
        return ApiResultEnum.SUCCESS.getResult(result);
    }

    @GetMapping("/premium/{starts}/{ends}")
    public ApiResult<List<InsProduct>> search(@PathVariable Double starts, @PathVariable Double ends) {

        List<InsProduct> products = this.insProductRepository.findByPremiumBetween(starts, ends);
        return ApiResultEnum.SUCCESS.getResult(products);
    }
}
