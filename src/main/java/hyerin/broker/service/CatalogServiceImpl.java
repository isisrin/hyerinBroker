package hyerin.broker.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hyerin.broker.domain.Catalog;
import hyerin.broker.domain.ServiceDefinition;
import hyerin.broker.exception.ServiceBrokerException;

@Service
public class CatalogServiceImpl implements CatalogService{
	
	private Catalog catalog;
	private Map<String, ServiceDefinition> serviceDefs = new HashMap<String, ServiceDefinition>();
	
	@Autowired
	public CatalogServiceImpl(Catalog catalog) {
		this.catalog = catalog;
		initializeMap();
	}
	
	@Override
	public Catalog getCatalog() throws ServiceBrokerException {
		return catalog;
	}

	@Override
	public ServiceDefinition getServiceDefinition(String serviceId) throws ServiceBrokerException {
		System.out.println("카탈로그 - 서비스 데피니션 왔어요 " + serviceId);
		return serviceDefs.get(serviceId);
	}
	
	private void initializeMap() {
		for (ServiceDefinition def: catalog.getServiceDefinitions()) {
			serviceDefs.put(def.getId(), def);
		}
	}


}