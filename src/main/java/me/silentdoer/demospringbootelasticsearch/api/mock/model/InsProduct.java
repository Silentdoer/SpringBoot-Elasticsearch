package me.silentdoer.demospringbootelasticsearch.api.mock.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
/**
 * 新版本的es不要设置type, 7.x统一默认是_doc, 8.x type将会删除
 * 不过目前SpringBoot最新版本正常情况下只支持Elasticsearch 6.2.2貌似,所以这里为了兼容新版本,一个index里只有一个type,且值为_doc
 *
 * 注意,@Document,@Field的参数要一开始就填写好,否则当index创建好后是不能修改的,只能删了重新弄(如果已经有数据了就更麻烦了,必须创建
 * 结构一样的index[配置可以不一样]最后通过导数据的方式来修改);
 * 即如果一开始@Field的参数是a,后来save了一个Document,然后改了@Field的参数是b,那么启动是会报错的(哪怕index里没数据);
 */
@Document(indexName = "insurance_product", type = "_doc")
public class InsProduct {

    @Id
    private String id;

    @NotBlank(message = "title is blank")
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String title;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String description;

    @Field(type = FieldType.Double)
    private Double coverage;

    @Field(type = FieldType.Double)
    private Double premium;

    @Field(type = FieldType.Keyword)
    private String serialCode;

    @Field(type = FieldType.Keyword)
    private String fromCompany;

    @Field(type = FieldType.Keyword)
    private String channel;
}
