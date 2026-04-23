package com.company.order.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.frameworkset.elasticsearch.ElasticSearchHelper;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.frameworkset.elasticsearch.entity.ESDatas;
import org.frameworkset.elasticsearch.entity.MapRestResponse;
import org.frameworkset.elasticsearch.entity.MapSearchHit;
import org.frameworkset.elasticsearch.entity.MapSearchHits;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.company.framework.util.PropertyUtils;
import com.company.order.es.dto.EsTestDto;
import com.github.javafaker.Faker;
import com.google.common.collect.Maps;

import cn.hutool.json.JSONUtil;

/**
 * 查看索引：http://172.20.33.24:8705/_cat/indices?v
 *
 * @author JQ棣
 */
@RestController
@RequestMapping("/es")
public class ESController {

//	@Autowired
//	private BBossESStarter bbossESStarter;

	/**
	 * 获取客户端
	 *
	 * @return
	 */
	@GetMapping(value = "/clientUtil")
	public Object clientUtil() {
		// 方式1，通过BBossESStarter中提供的工厂方法获取其单实例对象，这些单实例对象是多线程并发安全的
//		ClientInterface clientUtil = bbossESStarter.getRestClient();// 内部调用了ElasticSearchHelper.getRestClientUtil()
//		System.out.println("clientUtil:" + clientUtil);

		// 方式2，创建客户端工具，单实例多线程安全
		ClientInterface clientUtil2 = ElasticSearchHelper.getRestClientUtil();
		System.out.println("clientUtil:" + clientUtil2);

		// 方式3，创建加载配置文件的客户端工具，单实例多线程安全
		ClientInterface clientUtil3 = ElasticSearchHelper.getConfigRestClientUtil("esmapper/shop4IndexSearch.xml");
		System.out.println("clientUtil:" + clientUtil3);
		return true;
	}

	/**
	 * 创建索引（不一定要用）
	 *
	 * <pre>
	 * 使用场景：默认生成的索引字段配置，不是想要的字段属性的情况下使用
	 * </pre>
	 *
	 * @return
	 */
	@GetMapping(value = "/createIndiceMappingByDSL")
	public Object createIndiceMappingByDSL(String indexName) {
		ClientInterface clientUtil = ElasticSearchHelper.getConfigRestClientUtil("esmapper/createIndiceMapping.xml");
		String createIndiceMapping = clientUtil.createIndiceMapping(indexName, "mapping4lsq.candi_test_v2");
		// 查看索引结构：http://172.20.33.24:8705/lsq.candi_test_v2/_mapping?pretty
		return createIndiceMapping;
	}

	/**
	 * 添加或修改索引属性
	 *
	 * @param indexName
	 * @return
	 */
	@GetMapping(value = "/updateIndiceMappingByDSL")
	public Object updateIndiceMappingByDSL(String indexName) {
		ClientInterface clientUtil = ElasticSearchHelper.getConfigRestClientUtil("esmapper/updateIndiceMapping.xml");
		String updateIndiceMapping = clientUtil.updateIndiceMapping(indexName + "/_mapping",
				"mapping4lsq.candi_test_v2");
		return updateIndiceMapping;
	}

	/**
	 * 删除索引（没有权限）
	 *
	 * @return
	 */
	@GetMapping(value = "/dropIndice")
	public Object dropIndice(String indexName) {
		ClientInterface clientUtil = ElasticSearchHelper.getRestClientUtil();
		String dropIndice = clientUtil.dropIndice(indexName);
		return dropIndice;
	}

	/**
	 * 判断索引是否存在
	 *
	 * @return
	 */
	@GetMapping(value = "/existIndice")
	public Object existIndice(String indexName) {
		ClientInterface clientUtil = ElasticSearchHelper.getRestClientUtil();
		boolean existIndice = clientUtil.existIndice(indexName);
		return existIndice;
	}

	@GetMapping(value = "/addDocument")
	public Object addDocument(String indexName, String id) {
		Faker faker = new Faker(Locale.CHINA);
		EsTestDto estestdto = new EsTestDto();
		estestdto.setId(id);
		estestdto.setBusinessType("A");
		estestdto.setSearchTxt(faker.name().fullName()+faker.name().fullName()+faker.name().fullName());
		estestdto.setBig(true);
		estestdto.setAccessCount(11);
		estestdto.setVisitCount(12L);
		estestdto.setAddr(faker.address().fullAddress());
		estestdto.setLongitude(new BigDecimal(faker.address().longitude()));
		estestdto.setLatitude(new BigDecimal(faker.address().latitude()));
		estestdto.setUpdateTime(new Date());
		estestdto.setRemark(faker.name().title());
		estestdto.setDistance(BigDecimal.ONE);

		ClientInterface clientUtil = ElasticSearchHelper.getRestClientUtil();
		// 1.索引不存在会自动创建索引并且会添加文档
		// 2.文档已存在会更新文档，根据@ESId注解的字段作唯一
		String addDocument = clientUtil.addDocument(indexName, estestdto);
		return addDocument;
	}

	@GetMapping(value = "/getDocumentById")
	public Object getDocumentById(String indexName, String id) {
		ClientInterface clientUtil = ElasticSearchHelper.getRestClientUtil();
		long countAll = clientUtil.countAll(indexName);
		System.out.println("countAll:" + countAll);
		EsTestDto document = clientUtil.getDocument(indexName, id, EsTestDto.class);
		System.out.println(document);
		return document;
	}

	@GetMapping(value = "/deleteDocumentById")
	public Object deleteDocumentById(String indexName, String id) {
		ClientInterface clientUtil = ElasticSearchHelper.getRestClientUtil();

		// 删除索引文档
		String deleteDocument = clientUtil.deleteDocument(indexName, "_doc", id);// _doc是固定的
		System.out.println("deleteDocument:" + deleteDocument);

		// 批量删除索引文档
		// clientUtil.deleteDocuments(indexName, new String[] { "1", "2", "3"
		// });

		return deleteDocument;
	}

	@GetMapping(value = "/queryAll")
	public Object queryAll(String indexName) {
		String queryAll = "{\"query\": {\"match_all\": {}}}";// 等价于dsl json
		ClientInterface clientUtil = ElasticSearchHelper.getRestClientUtil();
		ESDatas<EsTestDto> searchList = clientUtil.searchList(indexName + "/_search", queryAll, EsTestDto.class);
		List<EsTestDto> datas = searchList.getDatas();

		System.out.println(JSONUtil.toJsonPrettyStr(datas));
		return datas;
	}

	@GetMapping(value = "/searchByDSL")
	public Object searchByDSL(String indexName) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("from", 0);
		params.put("size", 10);
		params.put("lon", "113.93141");
		params.put("lat", "22.53232");

		ClientInterface clientUtil = ElasticSearchHelper.getConfigRestClientUtil("esmapper/testSearch.xml");
		MapRestResponse mapRestResponse = clientUtil.search(indexName + "/_search", "search", params);
		MapSearchHits searchHits = mapRestResponse.getSearchHits();
		List<MapSearchHit> hits = searchHits.getHits();
		List<EsTestDto> datas = hits.parallelStream().map(mapSearchHit -> {
			System.out.println(JSONUtil.toJsonStr(mapSearchHit.getSource()));
			EsTestDto esShopInfoDto = PropertyUtils.copyProperties(mapSearchHit.getSource(), EsTestDto.class);
			// 从sort中获取距离
			BigDecimal distance = Optional.ofNullable(mapSearchHit.getSort()).map(value -> {
				return value.length == 0 ? BigDecimal.ZERO
						: new BigDecimal(value[0].toString()).setScale(2, RoundingMode.HALF_UP);
			}).orElse(BigDecimal.ZERO);
			esShopInfoDto.setDistance(distance);
			return esShopInfoDto;
		}).collect(Collectors.toList());

		System.out.println(JSONUtil.toJsonPrettyStr(datas));
		return datas;
	}

}
