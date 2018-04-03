--service_instance 만드는 쿼리
CREATE TABLE service_instance(
    SERVICE_INSTANCE_ID VARCHAR(255),
    DASHBOARD_URL VARCHAR(255),
    ORGANIZATION_GUID VARCHAR(255),
    PLAN_ID VARCHAR(255),
    SERVICE_ID VARCHAR(255),
    SPACE_GUID VARCHAR(255),
    CONSTRAINT PK_SERVICE_INSTANCE PRIMARY KEY(SERVICE_INSTANCE_ID)
);

--새로운 tablespace만드는 쿼리
create tablespace hyerin 
datafile '/oracle/hyerin.dat' size 10M
autoextend on 
maxsize 10M 
extent management local
uniform size 64K;

-- 시스템이 만든 테이블 보기
select owner, table_name from dba_tables where owner = 'SYSTEM';

--인설트
insert into service_instance(SERVICE_INSTANCE_ID, DASHBOARD_URL, ORGANIZATION_GUID, PLAN_ID, SERVICE_ID, SPACE_GUID) values ('oracle-2018-03-20', 'hyerin.com', 'hyerin-org', 'hyerin-plan', 'hyerin-service', 'hyerin-space');

-- 드라드랍
DROP TABLESPACE ${instance.config.tablespace} INCLUDING CONTENTS AND DATAFILES;

--테이블 스페이스 확인 (대문자여야한다)
select * from USER_TABLESPACES where tablespace_name = 'ORACLE_2018_03_20';


--새로운 temp table 만드는 쿼리
create temporary tablespace hyerin_temp
tempfile '/oracle/hyerin_temp.dat' size 10M
autoextend on next 32m 
maxsize 10M
extent management local;

-- temp 삭제 쿼리
DROP TABLESPACE hyerin_temp INCLUDING CONTENTS AND DATAFILES;

-- 만능키 딜리트 ㅠㅠ
delete from service_instance where service_instance_id like '%oracle%';

-- temp, space 모두 찾는 명령어
select * from USER_TABLESPACES where tablespace_name like '%PAASTA%';



















--잡다한 것

CREATE TABLESPACE oracle_2018_03_20
datafile '/oracle/oracle-2018-03-20.dat' 
size 10M autoextend on maxsize 10M extent management local uniform size 64K;

DROP TABLESPACE oracle_2018_03_20 INCLUDING CONTENTS AND DATAFILES;

select * from service_instance;

select table_name, tablespace_name, status from user_tables where tablespace_name = 'SYSTEM';
select * from USER_TABLESPACES;
--
--desc service_instance;
--
--select * from service_instance;
--
--delete from service_instance where PLAN_ID = 'hyerin-plan';
--
--DROP TABLESPACE HYERIN INCLUDING CONTENTS AND DATAFILES;





--새로운 tablespace만드는 쿼리
create tablespace hyerin 
datafile '/oracle/hyerin.dat' size 10M
autoextend on 
maxsize 10M 
extent management local
uniform size 64K;

커넥션 유저 수도 플랜에서 고려 필 
용량도 실제로 얼마쓰고 있는지 확인 필

cf 오라클 확인 ㅇㅅㅇ?









select * from dba_temp_files;
select * from database_properties;
select * from service_instance;

select service_instance_id, service_id, plan_id, organization_guid, space_guid from service_instance where service_instance_id = 'oracle-2018-03-27';
select owner, table_name from dba_tables where owner = 'SYSTEM';
select * from USER_TABLESPACES where tablespace_name like '%PAASTA%';

select * from service_instance;
select * from USER_TABLESPACES where tablespace_name like '%PAASTA%';
--  딜리트!
delete from service_instance where service_instance_id like '%oracle%';


--새로운 temp table 만드는 쿼리
create temporary tablespace hyerin_temp
tempfile '/oracle/hyerin_temp.dat' size 10M
autoextend on next 32m 
maxsize 10M
extent management local;

-- temp 삭제 쿼리
DROP TABLESPACE PAASTA_ORACLE_2018_03_27 INCLUDING CONTENTS AND DATAFILES;




CREATE TEMPORARY TABLESPACE paasta_oracle_2018_03_27_temp tempfile '/oracle/paasta_oracle_2018_03_27_temp.dat' size 10M autoextend on next 32m  maxsize 10M extent management local;

create temporary tablespace hyerin_temp tempfile '/oracle/hyerin_temp.dat' size 10M  autoextend on next 32m  maxsize 10M extent management local;
