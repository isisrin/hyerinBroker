package hyerin.broker.domain.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import hyerin.broker.domain.Catalog;
import hyerin.broker.domain.Plan;
import hyerin.broker.domain.ServiceDefinition;

/**
 * Spring boot 구동시 Catalog API 에서 사용하는 Catalog Bean 를 생성하는 클래스
 * 
 * @author 김한종
 *
 */
@Configuration
public class CatalogConfig {
	
	@Bean
	public Catalog catalog() {
		return new Catalog( Arrays.asList(
				new ServiceDefinition(
					"hyerin-service",
					"Oracle-DB",
					"A simple oracle implementation",
					true,
					true,
					Arrays.asList(
							new Plan("코드값이 들어갈 예정", "플랜 이름이 들어갈 예정", "플랜에 대한 설명이 들어감", getPlanMetadata("A"), true),
							new Plan("코드값이 들어갈 예정2", "플랜 이름이 들어갈 예정2", "플랜에 대한 설명이 들어감2", getPlanMetadata("B"), false)
							),
					Arrays.asList("oracle", "document"),
					getServiceDefinitionMetadata(),
					getRequires(),
					null
				)));
	}
	
/* Used by Pivotal CF console */	
	
	private Map<String,Object> getServiceDefinitionMetadata() {
		Map<String,Object> sdMetadata = new HashMap<String,Object>();
		sdMetadata.put("displayName", "oracleDB");
		sdMetadata.put("imageUrl","http://www.openpaas.org/rs/oracle/images/oracleDB_Logo_Full.png");
		sdMetadata.put("longDescription","oracleDB Service");
		sdMetadata.put("providerDisplayName","OpenPaas");
		sdMetadata.put("documentationUrl","http://www.openpaas.org");
		sdMetadata.put("supportUrl","http://www.openpaas.org");
		return sdMetadata;
	}
	
	private Map<String,Object> getPlanMetadata(String planType) {		
		Map<String,Object> planMetadata = new HashMap<String,Object>();
		planMetadata.put("costs", getCosts(planType));
		planMetadata.put("bullets", getBullets(planType));
		return planMetadata;
	}
	
	private List<Map<String,Object>> getCosts(String planType) {
		Map<String,Object> costsMap = new HashMap<String,Object>();
		
		Map<String,Object> amount = new HashMap<String,Object>();
		
		if(planType.equals("A")){
			amount.put("usd", new Double(0.0));
			costsMap.put("amount", amount);
			costsMap.put("unit", "MONTHLY");
			
		}else if(planType.equals("B")){
			amount.put("usd", new Double(100.0));
			costsMap.put("amount", amount);
			costsMap.put("unit", "MONTHLY");
			
		}else {
			amount.put("usd", new Double(0.0));
			costsMap.put("amount", amount);
			costsMap.put("unit", "MONTHLY");
		}
	
		
		return Arrays.asList(costsMap);
	}
	
	private List<String> getBullets(String planType) {
		if(planType.equals("A")){
			return Arrays.asList("Shared oracleDB server", 
					"10 concurrent connections (not enforced)");
		}else if(planType.equals("B")){
			return Arrays.asList("Shared oracleDB server", 
					"100 concurrent connections (not enforced)");
		}
		return Arrays.asList("Shared oracleDB server", 
				"10 concurrent connections (not enforced)");
	}
	
	private List<String> getRequires() {
		
		return Arrays.asList("syslog_drain");
	}
	
	
}