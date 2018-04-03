package hyerin.broker.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class hyerinController {

	@Autowired
	DataSource dataSource;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	Connection conn = null;
	
	@RequestMapping(value ="/hyerin", method = RequestMethod.GET)
	public String hyerin1() {
		System.out.println("데이터 소스를 보여주실래여?" + dataSource);
		jdbcTemplate.setDataSource(dataSource);
		System.out.println("데이터 베이스 삭제 쿼리도 함 봐야 겠는데?" + "DROP TABLESPACE " + "paasta_oracle_2018_03_26" + " INCLUDING CONTENTS AND DATAFILES");
		jdbcTemplate.execute("DROP TABLESPACE " + "paasta_oracle_2018_03_26" + " INCLUDING CONTENTS AND DATAFILES");
		System.out.println("템프 지우는 문자열좀 봅시다  : " + "DROP TABLESPACE " + "paasta_oracle_2018_03_26" + "_temp INCLUDING CONTENTS AND DATAFILES");
		jdbcTemplate.execute("DROP TABLESPACE " + "paasta_oracle_2018_03_26" + "_temp INCLUDING CONTENTS AND DATAFILES");

		//jdbcTemplate.update(SERVICE_INSTANCES_UPDATE, instance.getServiceInstanceId(), instance.getServiceDefinitionId(), instance.getPlanId(), instance.getOrganizationGuid(), instance.getSpaceGuid());
		return "내가 지금 뭐하는 건지...";
	}
	
	@RequestMapping(value ="/hyerin2", method = RequestMethod.GET)
	public String hyerin2() {
		 try {
	            // 1. 드라이버 로딩
	            Class.forName("oracle.jdbc.driver.OracleDriver");
	            System.out.println("드라이버 로딩 성공");
	            
	        // forName의 인자로 전달된 주소에 드라이버가 없을 경우
	        } catch (ClassNotFoundException e) {
	            System.out.println("드라이버 로딩 실패");
	        }
	        
	        try {
	            // 오라클DB에 연결
	            Connection conn = DriverManager.getConnection(
	                    "jdbc:oracle:thin:@211.104.171.226:59161/xe", "system" , "oracle");
	            System.out.println("커넥션 성공");
	            
	            // 실제 사용 코드
	            
	            // 커넥션은 반드시 닫아주어야 한다.
	            conn.close();
	            System.out.println("커넥션 종료");
	        // 오라클 DB에 연결이 실패하였을때
	        } catch (SQLException e) {
	            System.out.println("커넥션 실패");
	        } 
	        return "허허";
	}
}
