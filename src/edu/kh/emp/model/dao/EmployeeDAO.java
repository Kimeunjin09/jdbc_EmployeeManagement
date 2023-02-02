package edu.kh.emp.model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.kh.emp.model.vo.Employee;

// View에서 가져다 쓸거 선언
// DAO(Data Access Object, 데이터 접근 객체)
// -> 데이터베이스에 접근(연결)하는 객체
// --> JDBC 코드 작성
/**
 * @author user1
 *
 */
/**
 * @author user1
 *
 */
public class EmployeeDAO {
	
	// JDBC 객체 참조 변수 필드 선언 (class 내부에서 공통 사용)
	// 필드 선언 -> heap영역에 생성됨
	// 메소드 안에다 만든건 -> stack 영역에 생성됨
	
	private Connection conn; // 필드(heap영역에 생성, 변수가 비어있을 수 없음)
							 // 이미 초기값이 null이라 null선언x -> 참조형의 초기값은 null
							 // 초기값 설정을 안하면 jvm이 지정한 기본값으로 자동설정해줌
	private Statement stmt;
	private ResultSet rs = null; // 위에 두개랑 똑같이 선언한거와 같음

	private PreparedStatement pstmt;
	// Statement의 자식으로 향상된 기능 제공
	// -> ? 기호 (palceholder / 위치홀더)를 이용해서
	// SQL에 작성되어지는 리터럴을 동적으로 제어함
	
	// SQL ? 기호에 추가되는 값은
	// 숫자인 경우 '' 없이 대입
	// 문자열인 경우 ''가 자동으로 추가되어 대입
	
	
	/*
	public void method() {
		Connection conn2; // 메소드 안에 선언,지역 변수(Stack, 변수가 비어있을 수 있음)
	}
     */
	
	// 다른 메서드에서도 공동으로 쓸 구문이니까 필드에 작성
	private String driver = "oracle.jdbc.driver.OracleDriver";
	private String url = "jdbc:oracle:thin:@localhost:1521:XE";
	private String user = "kh";
	private String pw = "kh1234";
	
	
	
	
	// alt + shift + j
	/** 전체 사원 정보 조회 DAO 2번
	 * @return empList --> view select all 에서 부르면 empList가 반환
	 */
	public List<Employee> selectAll() {
		// 1. 결과 저장용 변수 선언
		List<Employee> empList = new ArrayList<>();
		
		try {
			// 2. JDBC 참조 변수에 객체 대입
			// -> conn, stmt, rs에 객체 대입
			Class.forName(driver); // oracle.jdbc.driver.OracleDriver 이거 넣은거랑 똑같음
			// 오라클 jdbc 드라이버 객체 메모리 로드
			
			conn = DriverManager.getConnection(url, user, pw);
			// 오라클 jdbc 드라이버 객체를 이용하여 DB 접속 방법 생성
			
			// Employee vo에 작성해둔 사원번호~급여를 
			// 하나의 객체로 만들어 Employee list에 담을거임
			String sql = "SELECT EMP_ID, EMP_NAME, EMP_NO, EMAIL, PHONE,\r\n"
					+ "NVL(DEPT_TITLE, '부서없음') DEPT_TITLE,\r\n"
					+ "JOB_NAME, SALARY\r\n"
					+ "FROM EMPLOYEE\r\n"
					+ "LEFT JOIN DEPARTMENT ON (DEPT_ID = DEPT_CODE)\r\n"
					+ "JOIN JOB USING(JOB_CODE)";
			// \r\n 지우려면 "뒤에 띄어쓰기 해줘야함
			
			// Statement 객체 생성
			stmt = conn.createStatement();			
			
			// SQL을 수행 후 결과(ResultSet) 반환 받음
			rs = stmt.executeQuery(sql); // 위에서 만든 sql을 가지고 DB에가서 ResultSet을가지고 자바로 돌아옴
			
			// 3. 조회 결과를 얻어와 한 행씩 접근하여
			// Employee 객체 생성 후 컬럼값 옮겨 담기
			// -> 만들어놓은 List에 추가
			
			while(rs.next()) {
				
				int empId = rs.getInt("EMP_ID");
				// EMP_ID 컬럼은 문자열 컬럼(VARCHAR2)이지만
				// 저장된 값들이 모두 숫자형태
				// -> DB에서 자동으로 형변환을 진행해서 얻어옴
				
				String empName = rs.getString("EMP_NAME");
				String empNo = rs.getString("EMP_NO");
				String email = rs.getString("EMAIL");
				String phone = rs.getString("PHONE");
				String departmentTitle = rs.getString("DEPT_TITLE");
				String jobName = rs.getString("JOB_NAME");
				int salary = rs.getInt("SALARY");
				
				// 위에 만든걸 Employee 객체화
				Employee emp = new Employee(empId, empName, empNo, email, phone, departmentTitle, jobName, salary);
				
				// 객체화한걸 List에 담기
				empList.add(emp);
				
			} // while 종료
			
			
		} catch(Exception e) {
			e.printStackTrace();
			
		} finally {
			// 4. JDBC 객체 자원 반환
			try {
				if(rs != null) rs.close();
				if(stmt != null) stmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
			
		}
		
		// 5. 결과 반환, try-catch문 밖에서 작성
		return empList;
	}

	/** 주민등록번호가 일치하는 사원 정보 조회 9번
	 * @param(매개변수) empNo 받을거임
	 * @return emp
	 */
	// 주민등록번호 일치면 Employee 객체 하나니까 List사용x
	public Employee selectEmpNo(String empNo) {
		
		// 결과 저장용 변수 선언
		Employee emp = null;
		
		try {
			// Connection 생성
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, pw);
			
			// SQL 작성
			String sql = "SELECT EMP_ID, EMP_NAME, EMP_NO, EMAIL, PHONE,\r\n"
					+ "NVL(DEPT_TITLE, '부서없음') DEPT_TITLE,\r\n"
					+ "JOB_NAME, SALARY\r\n"
					+ "FROM EMPLOYEE\r\n"
					+ "LEFT JOIN DEPARTMENT ON (DEPT_ID = DEPT_CODE)\r\n"
					+ "JOIN JOB USING(JOB_CODE)\r\n"
					+ "WHERE EMP_NO = ?";
								   // ? = placeholder, 나중에 어떤값이 들어올거라는 의미
			
			// Statement 객체 사용 시 순서
			// SQL작성 -> Statement 생성 -> SQL 수행 후 결과 반환
			
			// PreparedStatement 객체 사용 시 순서
			// SQL 작성
			// -> PreparedStatement 객체 생성(? 가 포함된 SQL을 매개변수로 사용)
			// -> ?에 알맞은 값 대입 (DB에 보내기전에 필요한 값을 동적으로 대입)
			// -> SQL 수행 후 결과 반환 
			
			// PreparedStatement 객체 생성
			pstmt = conn.prepareStatement(sql); // 이미 타고 있음
			
			// ? 에 알맞은 값 대입, ?는 여러개 올 수 있고 하나있을때는 첫번째 자리를 의미
			pstmt.setString(1, empNo); // 첫번째자리에 empNo가 들어올거다
			
			//SQL 수행 후 결과 반환
		    rs = pstmt.executeQuery();
		    // PreparedStatement는
		    // 객체 생성 시 이미 SQL이 답겨져 있는 상태이므로
		    // SQL 수행(executeQuery()) 시 매개변수로 전달할 필요가 없다!
		    
		    // ex) pstmt.executeQuery(sql);
		    // -> ?에 작성되어 있던 값이 모두 사라져 수행 시 오류발생
		    
		    // 한명밖에 없으니까 while문 말고 if사용
		    if(rs.next()) {
		   
		    	int empId = rs.getInt("EMP_ID");
				String empName = rs.getString("EMP_NAME");
				//String empNo = rs.getString("EMP_NO"); // 파라미터와 같은 값이라 필요 없음
				String email = rs.getString("EMAIL");
				String phone = rs.getString("PHONE");
				String departmentTitle = rs.getString("DEPT_TITLE");
				String jobName = rs.getString("JOB_NAME");
				int salary = rs.getInt("SALARY");
				
				// 위에 만든걸 Employee 객체화
				emp = new Employee(empId, empName, empNo, email, phone, departmentTitle, jobName, salary);
				// 위에서 Employee emp = null 이라고 만들었으니까 Employee emp = 안해도 괜찮음
		    } 
		} catch(Exception e) {
			e.printStackTrace();
			
		} finally {
			
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
				
			} catch(Exception e) {
				e.printStackTrace();
			}
			
		}
		
		return emp;
	}

	/** 새로운 사원 정보 추가 1번
	 * @param emp
	 * @return result(INSERT 성공한 행의 개수 반환)
	 */
	public int insertEmployee(Employee emp) {
		
		// 결과 저장용 변수 선언
		int result = 0;
		
		try {
			
			// 커넥션 생성
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, pw);
			
			// ** DML 수행할 예정 **
			// 트랜잭션에 DML 구문이 임시 저장
			// --> 정상적인 DML인지를 판별해서 개발자가 직접 commit, rollback을 수행
			
			// Connection 개체 생성 시(+닫을 때)
			// AutoCommit이 활성화 되어 있는 상태이기 때문에
			// 이를 해제하는 코드를 추가
			conn.setAutoCommit(false); // AutoCommit 비활성화
			
			// AutoCommit 비활성화를 해도
			// conn.close(); 구문을 수행하면 자동으로 Commit이 수행 됨
			// --> close() 수행 전에 트랜잭션 제어 코드를 작성해야 한다
			
			// SQL 작성
			String sql 
				= "INSERT INTO EMPLOYEE VALUES(?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ?, SYSDATE, NULL, DEFAULT)";
			// 입사날짜는 SYSDATE, 퇴직날짜는 퇴직할때 업데이트 되니까 NULL
			// 퇴사여부 컬럼의 DEFAULT == 'N'
			
			// PreparedStatement 객체 생성(매개변수에 SQL 추가)
			pstmt = conn.prepareStatement(sql);
			
			// ?(placeholder)에 알맞은 값 대입
			pstmt.setInt(1, emp.getEmpId());
			pstmt.setString(2, emp.getEmpName());
			pstmt.setString(3, emp.getEmpNo());
			pstmt.setString(4, emp.getEmail());
			pstmt.setString(5, emp.getPhone());
			pstmt.setString(6, emp.getDeptCode());
			pstmt.setString(7, emp.getJobCode());
			pstmt.setString(8, emp.getSalLevel());
			pstmt.setInt(9, emp.getSalary());
			pstmt.setDouble(10, emp.getBonus());
			pstmt.setInt(11, emp.getManagerId());
			
			// SQL 수행 후 결과 반환 받기
			 result = pstmt.executeUpdate(); 
			 // 위에 만들어 놓은 결과 저장용 변수 사용 - int result = 0;
			 // executeQuery() : SELECT 수행 후 ResultSet 반환
			 // executeUpdate() : DML(INSERT, UPDATE, DELETE) 수행 후 결과 행 개수 반환
			
			// *** 트랜잭션 제어 처리 ***
			// -> DML 성공 여부에 따라서 commit, rollback 제어
			
			if(result > 0) conn.commit(); // DML 성공 시 commit
			else          conn.rollback(); // DML 실패 시 rollback
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
				
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	
	
	/** 사번이 일치하는 사원 정보 수정 DAO 4번
	 * @param emp
	 * @return result
	 */
	public int updateEmployee(Employee emp) {
		int result = 0;
		
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, pw);
			conn.setAutoCommit(false); // AutoCommit 비활성화
			
			String sql = "UPDATE EMPLOYEE SET "
					+ "EMAIL = ?, PHONE = ?, SALARY = ? "
					+ "WHERE EMP_ID = ?";
			
			// PrepareStatement 생성
			pstmt = conn.prepareStatement(sql);
			
			// ?에 알맞은 값 세팅
			pstmt.setString(1, emp.getEmail());
			pstmt.setString(2, emp.getPhone());
			pstmt.setInt(3, emp.getSalary());
			pstmt.setInt(4, emp.getEmpId());
			
			result = pstmt.executeUpdate(); // 반영된 행의 개수 반환
			
			
			// 트랜잭션 제어 처리
			if(result == 0) conn.rollback();
			else            conn.commit();
			
		} catch(Exception e) {
			e.printStackTrace();
			
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	/** 사원이 일치하는 사원 정보 삭제 DAO 5번
	 * @param empId
	 * @return result
	 */
	public int deleteEmployee(int empId) {
		
		int result = 0; // 결과 저장용 변수
		
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, pw);
			conn.setAutoCommit(false); // AutoCommit 비활성화
			
			String sql = "DELETE FROM EMPLOYEE WHERE EMP_ID = ?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, empId);

			result = pstmt.executeUpdate();
			
			// 트랜잭션 제어 처리
			if(result == 0) conn.rollback();
			else            conn.commit();
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
				
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	
	
	/** 입력받은 부서와 일치하는 모든 사원 조회 DAO 6번
	 * @param departmentTtilte
	 * @return empList
	 */
	
	public List<Employee> selectDeptEmp(String departmentTitle) {
		
		// 결과 저장용 변수
		List<Employee> empList = new ArrayList<>();
		
		try {
			// JDBC 드라이버 메모리에 로드
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, pw);
			
			String sql = "SELECT EMP_ID, EMP_NAME, EMP_NO, EMAIL, PHONE,\r\n"
					+ "NVL(DEPT_TITLE, '부서없음') DEPT_TITLE,\r\n"
					+ "JOB_NAME, SALARY\r\n"
					+ "FROM EMPLOYEE\r\n"
					+ "LEFT JOIN DEPARTMENT ON (DEPT_ID = DEPT_CODE)\r\n"
					+ "JOIN JOB USING(JOB_CODE)\r\n"
					+ "WHERE DEPT_TITLE = ?";
			
			pstmt = conn.prepareStatement(sql); // pstmt는 이미 sql이 실려있음
			
			pstmt.setString(1, departmentTitle);
			
			// DB로 보내서 실행결과 가지고 자바로 돌아오기, resultSet에 담아줌
			rs = pstmt.executeQuery();
			
			
			while(rs.next()) {
				
				// 값 가공
				int empId = rs.getInt("EMP_ID");
				String empName = rs.getString("EMP_NAME");
				String empNo = rs.getString("EMP_NO");
				String email = rs.getString("EMAIL");
				String phone = rs.getString("PHONE");
				String jobName = rs.getString("JOB_NAME");
				int salary = rs.getInt("SALARY");
				
				Employee emp = new Employee(empId, empName, empNo, email, phone, 
								departmentTitle, jobName, salary);
				
				// 리스트에 값 담아줌, empList를 반환
				empList.add(emp);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close(); // 부서명은 리터럴값이라 '' 필요하니까 pstmt활용
				if(conn != null) conn.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return empList;
	}
	
	
	/** 사번이 일치하는 사원 정보 조회 DAO 3번
	 * @param empId
	 * @return emp
	 */
	public Employee selectEmpId(int empId) {
		// 뷰에서 int empId 이렇게 만들었으니까 반환형 int
		// 매개변수 empId(사번)
		// 얘를 받으면 반환값(리턴)이 뭔지 생각해보기
		// 조회된 사원정보니까 임플로이 객체 반환형 Employee
		// 반환형 이름 (반환형 매개변수)
		
		// 결과 저장용 변수 선언
		
		Employee emp = null;
		// 만약에 조회 결과가 있으면 Employee 객체를 생성해서 emp에 대입(null 이 아님)
		// 만약 조회 결과가 없으면 emp에 아무것도 대입하지 않음
		// -> 초기값 null을 가지고 돌아감
		
		
		try {
			
			// 오라클 JDBC 드라이버 메모리 로드
			Class.forName(driver); // 필드에 만들어놨음
			conn = DriverManager.getConnection(url, user, pw);
			// 커넥션 생성해서 얻오기
			
			
			// SQL 작성
			String sql = "SELECT EMP_ID, EMP_NAME, EMP_NO, EMAIL, PHONE,\r\n"
					+ "NVL(DEPT_TITLE, '부서없음') DEPT_TITLE,\r\n"
					+ "JOB_NAME, SALARY\r\n"
					+ "FROM EMPLOYEE\r\n"
					+ "LEFT JOIN DEPARTMENT ON (DEPT_ID = DEPT_CODE)\r\n"
					+ "JOIN JOB USING(JOB_CODE)\r\n"                       
					+ "WHERE EMP_ID = " + empId; //view에서 입력받은 사원 = 매개변수로 전달받음
			//																위의 int empId
			
			
			// Statement 생성
			stmt = conn.createStatement();
			
			// SQL 수행 후 결과(ResultSet)반환 받기
			rs = stmt.executeQuery(sql); //조회는 쿼리 dml은 업데이트
			// 업데이트는 반환값이 리절트셋이 아니라 행의 개수(인트형으로 받음)
			
			// ** 조회 결과가 최대 1행인 경우
			//    불필요한 조건 검사를 줄이기 위해서 if문 사용 권장
			
		    if(rs.next()) { // 조회결과가 있을 경우
		    	
		    	// 반환하기로 한 Employee 객체에 담아줄 값 가공
		    	
		    	// empId는 위에 int empId매개변수로 받아놔서(파라미터와 같은 값) 작성x, 불필요
				String empName = rs.getString("EMP_NAME"); // rs가 가지고온 애를 empName에 담아줌
				String empNo = rs.getString("EMP_NO"); 
				String email = rs.getString("EMAIL");
				String phone = rs.getString("PHONE");
				String departmentTitle = rs.getString("DEPT_TITLE");
				String jobName = rs.getString("JOB_NAME");
				int salary = rs.getInt("SALARY");
				
			// 가공한 값을 가지고 객체 만들기
		    // 위에 결과값 null이라고 선언해둔 결과저장용 변수에 담아주기
			emp = new Employee(empId, empName, empNo, email, phone, departmentTitle, jobName, salary);
	
		    } 
		} catch(Exception e) {
			e.printStackTrace();
			
		} finally {
			
			try { // JDBC 객체 참조변수 닫기
				if(rs != null) rs.close();
				if(stmt != null) stmt.close(); // pstmt로 해도 상관없음
				if(conn != null) conn.close();
				// 객체 생성 역순으로 닫기(권장사항)
				
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return emp;
	}
	
	/** 입력받은 급여 이상을 받는 모든 사원 정보 조회 DAO 7번
	 * @param salary
	 * @return empList
	 */
	public List<Employee> selectSalaryEmp(int salary) {
    // 다른 클래스도 가져다 써야하니까 public
    // Employee 객체를 가지고있는 List반환
		
		// 결과 저장용 변수
		List<Employee> empList = new ArrayList<>();
		
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, pw);
			
			String sql ="SELECT EMP_ID, EMP_NAME, EMP_NO, EMAIL, PHONE,\r\n"
					+ "NVL(DEPT_TITLE, '부서없음') DEPT_TITLE,\r\n"
					+ "JOB_NAME, SALARY\r\n"
					+ "FROM EMPLOYEE\r\n"
					+ "LEFT JOIN DEPARTMENT ON (DEPT_ID = DEPT_CODE)\r\n"
					+ "JOIN JOB USING(JOB_CODE)\r\n"
					+ "WHERE SALARY >= ?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, salary);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				int empId = rs.getInt("EMP_ID");
				String empName = rs.getString("EMP_NAME");
				String empNo = rs.getString("EMP_NO"); 
				String email = rs.getString("EMAIL");
				String phone = rs.getString("PHONE");
				String departmentTitle = rs.getString("DEPT_TITLE");
				String jobName = rs.getString("JOB_NAME");
				int selectSalary = rs.getInt("SALARY");
				
				Employee emp = new Employee(empId, empName, empNo, email, phone, 
							departmentTitle, jobName, selectSalary);
				
				empList.add(emp);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
				
			} catch(Exception e) {
				e.printStackTrace();
			}
			
		}
		return empList;
	}
	
	/** 부서별 급여 합 전체 조회 DAO
	 * @return map
	 */
	public Map<String, Integer> selectDeptTotalSalary() {
		// 맵에 저장할거니까 맵반환
		
		//Map<String, Integer> map = new HashMap<>();
		
		// 맵으로 결과 저장용 변수 선언
		Map<String, Integer> map = new LinkedHashMap<>();
		// LinkedHashMap : key 순서가 유지되는 HashMap
		// -> (ORDER BY절 정렬 결과를 그대로 저장 가능)
		
		
		
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, pw);
			
			String sql ="SELECT NVL(DEPT_CODE, '부서없음') DEPT_CODE, SUM(SALARY) TOTAL\r\n"
					+ "FROM EMPLOYEE\r\n"
					+ "LEFT JOIN DEPARTMENT ON(DEPT_ID = DEPT_CODE)\r\n"
					+ "GROUP BY DEPT_CODE\r\n"
					+ "ORDER BY DEPT_CODE\r\n";
			// 맵은 순서가 없어서 order by해도 정렬x
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				String deptCode = rs.getString("DEPT_CODE");
				int total  = rs.getInt("TOTAl");
				
				map.put(deptCode, total);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(stmt != null) stmt.close();
				if(conn != null) conn.close();
				
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}
	
	
	/** 직급별 급여 합 평균 조회 DAO
	 * @return map
	 */
	public Map<String, Double> selectJobAvgSalary() {
		
			
			Map<String, Double> map = new LinkedHashMap<>();
			
			try {
				Class.forName(driver);
				conn = DriverManager.getConnection(url, user, pw);
				
				String sql ="SELECT JOB_NAME, ROUND(AVG(SALARY), 1) AVERAGE\r\n"
						+ "FROM EMPLOYEE\r\n"
						+ "JOIN JOB USING(JOB_CODE)\r\n"
						+ "GROUP BY JOB_NAME, JOB_CODE\r\n"
						+ "ORDER BY JOB_CODE";
				// GROUP BY 절에 JOB_CODE를 포함시켜
				// ORDER BY절 정렬 컬럼으로 사용 가능하게 만든 것임
				
				stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);
				
				while(rs.next()) {
					String jobName = rs.getString("JOB_NAME");
					double average = rs.getDouble("AVERAGE");
					
					map.put(jobName, average);
				}
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(rs != null) rs.close();
					if(stmt != null) stmt.close();
					if(conn != null) conn.close();
					
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			return map;
	}
}