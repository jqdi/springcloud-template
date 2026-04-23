package com.company.order.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomUtils;
import org.frameworkset.elasticsearch.ElasticSearchHelper;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.frameworkset.elasticsearch.entity.ESDatas;
import org.frameworkset.elasticsearch.entity.MapRestResponse;
import org.frameworkset.elasticsearch.entity.MapSearchHit;
import org.frameworkset.elasticsearch.entity.MapSearchHits;
import org.frameworkset.elasticsearch.entity.geo.GeoPoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.company.framework.util.PropertyUtils;
import com.company.order.es.dto.Brand;
import com.company.order.es.dto.Brand.Product;
import com.company.order.es.dto.Brand.Product.Address;
import com.company.order.es.dto.EsTestDto;
import com.github.javafaker.Faker;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cn.hutool.json.JSONUtil;

/**
 * 查看索引：http://172.20.33.24:8705/_cat/indices?v
 *
 * @author JQ棣
 */
@RestController
@RequestMapping("/brand")
public class BrandController {

	public static void main(String[] args) {
		double lon = RandomUtils.nextDouble(73.66, 135.05);
		double lat = RandomUtils.nextDouble(3.86, 53.55);
		System.out.println(lon);
	}

	@GetMapping(value = "/addDocumentBat")
	public Object addDocumentBat(String indexName, Integer count) {
		for (int i = 0; i < count; i++) {
			addDocument2(indexName, (i + 1) + "");
		}
		return null;
	}
	@GetMapping(value = "/addDocument")
	public Object addDocument2(String indexName, String id) {
		Faker faker = new Faker(Locale.CHINA);

		Brand estestdto = new Brand();
		estestdto.setId(id);
		estestdto.setName(faker.name().fullName());
		estestdto.setAccessCount(11);
		estestdto.setAddr(faker.address().fullAddress());

//		纬度3.86~53.55，经度73.66~135.05
		GeoPoint location = new GeoPoint();
		double lon = RandomUtils.nextDouble(93.66, 115.05);
		double lat = RandomUtils.nextDouble(20.86, 43.55);
		location.setLon(lon);
		location.setLat(lat);
		estestdto.setLocation(location);

//		estestdto.setLongitude(new BigDecimal(faker.address().longitude()));
//		estestdto.setLatitude(new BigDecimal("22.53332"));
		estestdto.setUpdateDate(new Date());
		estestdto.setRemark(faker.name().title());
		estestdto.setDistance(BigDecimal.ONE);

		List<Product> productList = Lists.newArrayList();
		for (int i = 0; i < 3; i++) {
			Product product = new Product();
			product.setCode(faker.code().asin());
			product.setName(faker.name().title());
			product.setUpdateDate(new Date());

			List<Address> addressList = Lists.newArrayList();
			for (int j = 0; j < 2; j++) {
				Address address = new Address();
				address.setProvince(faker.address().city());
				address.setCity(faker.address().city());
				address.setDistrict(faker.address().country());
				address.setAddr(faker.address().streetName());

				GeoPoint location2 = new GeoPoint();
				double lon2 = RandomUtils.nextDouble(93.66, 115.05);
				double lat2 = RandomUtils.nextDouble(20.86, 43.55);
				location2.setLon(lon2);
				location2.setLat(lat2);
				address.setLocation(location2);

				address.setUpdateDate(new Date());
				addressList.add(address);
			}
			product.setAddressList(addressList);
			productList.add(product);
		}
		estestdto.setProductList(productList);

		ClientInterface clientUtil = ElasticSearchHelper.getRestClientUtil();
		// 1.索引不存在会自动创建索引并且会添加文档
		// 2.文档已存在会更新文档，根据@ESId注解的字段作唯一
		String addDocument = clientUtil.addDocument(indexName, estestdto);
		return addDocument;
	}

	@GetMapping(value = "/getDocumentById")
	public Object getDocumentById2(String indexName, String id) {
		ClientInterface clientUtil = ElasticSearchHelper.getRestClientUtil();
		long countAll = clientUtil.countAll(indexName);
		System.out.println("countAll:" + countAll);
		Brand document = clientUtil.getDocument(indexName, id, Brand.class);
		System.out.println(document);
		return document;
	}

	@GetMapping(value = "/queryAll")
	public Object queryAll(String indexName) {
		String queryAll = "{\"query\": {\"match_all\": {}}}";// 等价于dsl json
		ClientInterface clientUtil = ElasticSearchHelper.getRestClientUtil();
		ESDatas<Brand> searchList = clientUtil.searchList(indexName + "/_search", queryAll, Brand.class);
		List<Brand> datas = searchList.getDatas();

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
