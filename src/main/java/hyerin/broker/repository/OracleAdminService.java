package hyerin.broker.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import hyerin.broker.domain.CreateServiceInstanceRequest;
import hyerin.broker.domain.ServiceInstance;
import hyerin.broker.domain.ServiceInstanceBinding;

@Service
public class OracleAdminService {
	
	public static final String SERVICE_INSTANCES_FILDS = "service_instance_id, service_id, plan_id, organization_guid, space_guid";
	
	public static final String SERVICE_INSTANCES_FIND_BY_INSTANCE_ID = "select " + SERVICE_INSTANCES_FILDS + " from service_instance where service_instance_id = ?";
	
	public static final String TABLESPACE_FIND_BY_INSTANCE_ID = "SELECT tablespace_name, status  FROM user_tablespaces where tablespace_name = ?";
	
	public static final String SERVICE_INSTANCES_ADD = "insert into service_instance("+SERVICE_INSTANCES_FILDS+") values(?,?,?,?,?) ";
	
	public static final String SERVICE_INSTANCES_UPDATE = "update service_instance set service_instance_id = ?,  service_id = ?, plan_id = ?, organization_guid = ?, space_guid = ?";
	
	public static final String SERVICE_INSTANCES_DELETE = "delete from service_instance where service_instance_id = ?";
	
	public static final String SERVICE_BINDING_FILDS ="binding_id, instance_id, app_id, username, password";
	
	public static final String SERVICE_BINDING_FIND_BY_INSTANCE_ID = "select " + SERVICE_BINDING_FILDS + " from service_binding where instance_id = ?";
	
	public static final String SERVICE_BINDING_FIND_USERNAME_BY_BINDING_ID = "select username from service_binding where binding_id = ?";
	
	public static final String SERVICE_BINDING_DELETE_BY_BINDING_ID = "delete from service_binding where binding_id = ?";
	
	static String DATABASE_PREFIX = "paasta_";


	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public OracleAdminService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
		
	private static final RowMapper<ServiceInstance> mapper = new ServiceInstanceRowMapper();
	
	private static final RowMapper<ServiceInstanceBinding> mapper2 = new ServiceInstanceBindingRowMapper();
	
	
	public ServiceInstance findById(String id){
		System.out.println("MysqlAdminService.findById");
		ServiceInstance serviceInstance = null;;
		try {
			System.out.println("쿼리를 확인해 보자!" + SERVICE_INSTANCES_FIND_BY_INSTANCE_ID + id);
			serviceInstance = jdbcTemplate.queryForObject(SERVICE_INSTANCES_FIND_BY_INSTANCE_ID, mapper, id);
			serviceInstance.withDashboardUrl(getDashboardUrl(serviceInstance.getServiceInstanceId()));
		} catch (Exception e) {
		}
		return serviceInstance;
	}
	
	// DashboardUrl 생성
	public String getDashboardUrl(String instanceId){
		
		return "http://www.sample.com/"+instanceId;
	}
	
	
	private static class ServiceInstanceRowMapper implements RowMapper<ServiceInstance> {
        @Override
        public ServiceInstance mapRow(ResultSet rs, int rowNum) throws SQLException {
            CreateServiceInstanceRequest request = new CreateServiceInstanceRequest();
            request.withServiceInstanceId(rs.getString(1));
            request.setServiceDefinitionId(rs.getString(2));
            request.setPlanId(rs.getString(3));
            request.setOrganizationGuid(rs.getString(4));
            request.setSpaceGuid(rs.getString(5));
            System.out.println("이게 뭐하는 함수임?" + request.toString());
            return new ServiceInstance(request);
        }
    }
	
	private static class ServiceInstanceBindingRowMapper implements RowMapper<ServiceInstanceBinding> {
        @Override
        public ServiceInstanceBinding mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ServiceInstanceBinding(rs.getString(1), 
            		rs.getString(2), 
            		new HashMap<String, Object>(), 
            		"", 
            		rs.getString(3));
        }
    }


	public ServiceInstance createServiceInstanceByRequest(CreateServiceInstanceRequest request){
		System.out.println("OracleAdminService.createServiceInstanceByRequest");
		return new ServiceInstance(request).withDashboardUrl(getDashboardUrl(request.getServiceInstanceId()));
	}

	public boolean isExistsService(ServiceInstance instance) {
		System.out.println("서비스가 존재 하나여?" + getDatabase(instance.getServiceInstanceId()));
		//List<String> databases = jdbcTemplate.queryForList("SELECT TABLESPACE_NAME, STATUS  FROM USER_TABLESPACES where TABLESPACE_NAME = " + instance.getServiceInstanceId(), String.class);
		List<String> databases = jdbcTemplate.queryForList(TABLESPACE_FIND_BY_INSTANCE_ID, String.class, getDatabase(instance.getServiceInstanceId()));
		System.out.println("데이터 베이시스 크기?" + databases.size());
		return false;
	}

	public void deleteDatabase(ServiceInstance instance) {
		System.out.println("데이터 베이스 삭제 "); // -를 _로 바꾸는 것 해야함
		System.out.println("데이터 베이스 삭제 쿼리도 함 봐야 겠는데?" + "DROP TABLESPACE " + getDatabase(instance.getServiceInstanceId()) + " INCLUDING CONTENTS AND DATAFILES");
		jdbcTemplate.execute("DROP TABLESPACE " + getDatabase(instance.getServiceInstanceId()) + " INCLUDING CONTENTS AND DATAFILES");
		System.out.println("템프 지우는 문자열좀 봅시다  : " + "DROP TABLESPACE " + getDatabase(instance.getServiceInstanceId()) + "_temp INCLUDING CONTENTS AND DATAFILES");
		jdbcTemplate.execute("DROP TABLESPACE " + getDatabase(instance.getServiceInstanceId()) + "_temp INCLUDING CONTENTS AND DATAFILES");
		
	}

	public void createDatabase(ServiceInstance instance) {
		System.out.println("데이터 베이스 생성");
		String hoho = "CREATE TABLESPACE " + getDatabase(instance.getServiceInstanceId()) + " datafile '/oracle/" + getDatabase(instance.getServiceInstanceId()) + ".dat' size 10M autoextend on maxsize 10M extent management local uniform size 64K";
		System.out.println("집에 가고 싶다 " + hoho);
		jdbcTemplate.execute(hoho);
		
		//템프
		String hoho2 = "CREATE TEMPORARY TABLESPACE " + getDatabase(instance.getServiceInstanceId()) + "_temp tempfile '/oracle/" + getDatabase(instance.getServiceInstanceId()) + "_temp.dat' size 10M autoextend on next 32m  maxsize 10M extent management local";
		System.out.println("템프 만드는 문자열  : " + hoho2);
		jdbcTemplate.execute(hoho2);
		
	}

	public void save(ServiceInstance instance) {
		System.out.println("오라클 어드민 서비스 세이브");
		String hoho = "SERVICE_INSTANCES_ADD";
		if(findById(instance.getServiceInstanceId()) == null)  {//service_instance_id, service_id, plan_id, organization_guid, space_guid
			System.out.println(SERVICE_INSTANCES_ADD + instance.getServiceInstanceId() + instance.getServiceDefinitionId() + instance.getPlanId() + instance.getOrganizationGuid() + instance.getSpaceGuid());
			jdbcTemplate.update(SERVICE_INSTANCES_ADD, instance.getServiceInstanceId(), instance.getServiceDefinitionId(), instance.getPlanId(), instance.getOrganizationGuid(), instance.getSpaceGuid());
		} else {
			jdbcTemplate.update(SERVICE_INSTANCES_UPDATE, instance.getServiceInstanceId(), instance.getServiceDefinitionId(), instance.getPlanId(), instance.getOrganizationGuid(), instance.getSpaceGuid());
		}
	}

	public void delete(String serviceInstanceId) {
		System.out.println("데이터 한 줄을 지워 봅니다.");
		jdbcTemplate.update(SERVICE_INSTANCES_DELETE, serviceInstanceId);
		
	}

	public List<Map<String, Object>> findBindByInstanceId(String id) {
		System.out.println("파인드 바인드 바이 인스턴스 아이뒤 ");
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		list = jdbcTemplate.queryForList(SERVICE_BINDING_FIND_BY_INSTANCE_ID, id);
		return list;
	}

	public void deleteUser(String serviceInstanceId, String bindingId) {
		System.out.println("딜리트 유절");
		String userId = jdbcTemplate.queryForObject(SERVICE_BINDING_FIND_USERNAME_BY_BINDING_ID, String.class, bindingId);
		jdbcTemplate.execute("DROP USER '"+userId+"'@'%'");
		
	}

	public void deleteBind(String id) {
		System.out.println("딜리트 바인드");
		jdbcTemplate.update(SERVICE_BINDING_DELETE_BY_BINDING_ID, id);
	}
	
	
	// Database명 생성
	public String getDatabase(String id){
		
		String database;
		String s = id.replaceAll("-", "_");
	    database = DATABASE_PREFIX+s;
	    
	  return database;
	}
	
	

}
