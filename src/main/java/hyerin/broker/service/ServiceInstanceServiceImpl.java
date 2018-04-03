package hyerin.broker.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import hyerin.broker.domain.CreateServiceInstanceRequest;
import hyerin.broker.domain.DeleteServiceInstanceRequest;
import hyerin.broker.domain.ServiceInstance;
import hyerin.broker.domain.ServiceInstanceBinding;
import hyerin.broker.domain.UpdateServiceInstanceRequest;
import hyerin.broker.exception.ServiceBrokerException;
import hyerin.broker.exception.ServiceInstanceDoesNotExistException;
import hyerin.broker.exception.ServiceInstanceExistsException;
import hyerin.broker.exception.ServiceInstanceUpdateNotSupportedException;
import hyerin.broker.repository.OracleAdminService;

@Service
public class ServiceInstanceServiceImpl implements ServiceInstanceService{
	
	@Autowired
	OracleAdminService oracleAdminService;
		
	@Autowired
	private CatalogService service;
	
//	@Autowired
//	public ServiceInstanceServiceImpl(OracleAdminService oracleAdminService) {
//		this.oracleAdminService = oracleAdminService;
//	}
	
	private ObjectMapper jsonMapper = new ObjectMapper();
	

	@Override
	public ServiceInstance createServiceInstance(CreateServiceInstanceRequest request) throws ServiceInstanceExistsException, ServiceBrokerException {
		System.out.println("이제 여기 들어오는 거야? 꺄옼!!" + request);
		System.out.println("헐 뭐임?");
		String jsonString;
		try {
			jsonString = jsonMapper.writeValueAsString(request);
			System.out.println("제이슨 스트링구" + jsonString);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ServiceInstance findInstance = oracleAdminService.findById(request.getServiceInstanceId());
		System.out.println("파인드 인스턴스 " + findInstance);
		
		ServiceInstance instance = oracleAdminService.createServiceInstanceByRequest(request);
		System.out.println("인스턴스 " + instance);		
		
		if(findInstance != null){
			if(findInstance.getServiceInstanceId().equals(instance.getServiceInstanceId()) &&
					findInstance.getPlanId().equals(instance.getPlanId()) &&
					findInstance.getServiceDefinitionId().equals(instance.getServiceDefinitionId())){
				findInstance.setHttpStatusOK();
				return findInstance;
			}else{
				throw new ServiceInstanceExistsException(instance);
			}
		}
		System.out.println("아~ 잊자나 여기 안오지? 왜?");
		if(oracleAdminService.isExistsService(instance)) {
			oracleAdminService.deleteDatabase(instance);
		}
		oracleAdminService.createDatabase(instance);
		
		oracleAdminService.save(instance);
		return instance;
	}

	@Override
	public ServiceInstance getServiceInstance(String serviceInstanceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceInstance deleteServiceInstance(DeleteServiceInstanceRequest request) throws ServiceBrokerException {
		ServiceInstance instance = oracleAdminService.findById(request.getServiceInstanceId());
		if(instance == null) return null;
		
		oracleAdminService.deleteDatabase(instance);
		
		oracleAdminService.delete(instance.getServiceInstanceId());
		
		List<Map<String,Object>> list = oracleAdminService.findBindByInstanceId(instance.getServiceInstanceId());
		for(Map<String, Object> tmp : list) {
			oracleAdminService.deleteUser(instance.getServiceInstanceId(), (String)tmp.get("binding_id"));
			oracleAdminService.deleteBind((String)tmp.get("binding_id"));
		}
		return instance;
	}

	@Override
	public ServiceInstance updateServiceInstance(UpdateServiceInstanceRequest updateServiceInstanceRequest)
			throws ServiceInstanceUpdateNotSupportedException, ServiceBrokerException,
			ServiceInstanceDoesNotExistException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	
	
	
	
	//일단 추가해봄
	private static final class ServiceInstanceRowMapper implements RowMapper<ServiceInstance> {
        @Override
        public ServiceInstance mapRow(ResultSet rs, int rowNum) throws SQLException {
            CreateServiceInstanceRequest request = new CreateServiceInstanceRequest();
            request.withServiceInstanceId(rs.getString(1));
            request.setServiceDefinitionId(rs.getString(2));
            request.setPlanId(rs.getString(3));
            request.setOrganizationGuid(rs.getString(4));
            request.setSpaceGuid(rs.getString(5));
            return new ServiceInstance(request);
        }
    }
	
	//일단 추가해봄
	private static final class ServiceInstanceBindingRowMapper implements RowMapper<ServiceInstanceBinding> {
        @Override
        public ServiceInstanceBinding mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ServiceInstanceBinding(rs.getString(1), 
            		rs.getString(2), 
            		new HashMap<String, Object>(), 
            		"", 
            		rs.getString(3));
        }
    }

}
