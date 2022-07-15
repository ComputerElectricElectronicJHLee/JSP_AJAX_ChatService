package user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;


//ȸ���� ������ �����ͺ��̽� ���� �� ó���� ���
//Data Access Object(������ ���� ��ü) : ���������� �����ͺ��̽��� �����ؼ� ��� �����͸� �������ų� ���ų� �ϴ� ���� ����
public class UserDAO {

	//Connection pool�� �̿��ϱ� ����
	DataSource dataSource;
	
	//��ü�� �������ڸ��� �����ͺ��̽��� ������ �� �ִ� ������
	public UserDAO() {
		try {
			InitialContext initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env"); //�ҽ��� ���� �� �� �ֵ��� �ϴ� ���
			dataSource = (DataSource) envContext.lookup("jdbc/UserChat"); //�ҽ� �߰��ϰ� �Ǹ� ������Ʈ ����
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//ȸ�� ���� ���̹����̽� ó���� �޼���
	public int login(String userID, String userPassword) {
		Connection conn = null;
		PreparedStatement pstmt = null; //SQL Injection���� ��ŷ������ ������ְ� ���������� SQL���� �����ϰ� ����
		ResultSet rs = null;
		String SQL = "SELECT * FROM USER WHERE userID = ?"; //�Է¹��� �� ���� �־���
		try {
			//getConnection() : ���������� �����ͺ��̽� Connection pool�� �����ϵ��� ����� ��
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				//����ڰ� �Է��� ��ȣ�� ������ ���̽� �� ��ȣ�� ��ġ�Ѵٸ� 
				if(rs.getString("userPassword").equals(userPassword)) {
					return 1; //�α��� ����
				}
				return 2; //��й�ȣ�� Ʋ��
			} else {
				return 0; //�ش� ����ڰ� �������� ����
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return -1; //������ ���̽� ������ �߻��� ��� 
	}
	
	//���� ���� ���� üũ
	public int registerCheck(String userID) {
		Connection conn = null;
		PreparedStatement pstmt = null; //SQL Injection���� ��ŷ������ ������ְ� ���������� SQL���� �����ϰ� ����
		ResultSet rs = null;
		String SQL = "SELECT * FROM USER WHERE userID = ?"; //�Է¹��� �� ���� �־���
		try {
			//getConnection() : ���������� �����ͺ��̽� Connection pool�� �����ϵ��� ����� ��
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			rs = pstmt.executeQuery();
			if(rs.next() || userID.equals("")) {
				return 0; //�̹� �����ϴ� ȸ��
			} else {
				return 1; //���� ������ ȸ�� ���̵�
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return -1; //������ ���̽� ������ �߻��� ��� 
	}
	
	public int register(String userID, String userPassword, String userName, String userAge, String userGender, String userEmail, String userProfile) {
		Connection conn = null;
		PreparedStatement pstmt = null; //SQL Injection���� ��ŷ������ ������ְ� ���������� SQL���� �����ϰ� ����
		String SQL = "INSERT INTO USER VALUES (?, ?, ?, ?, ?, ?, ?)"; //�Է¹��� �� ���� �־���
		try {
			//getConnection() : ���������� �����ͺ��̽� Connection pool�� �����ϵ��� ����� ��
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			pstmt.setString(2, userPassword);
			pstmt.setString(3, userName);
			pstmt.setInt(4, Integer.parseInt(userAge)); //���� ���°� �ƴ� ���� ���°� ������ ��� return -1 ���� ó��
			pstmt.setString(5, userGender);
			pstmt.setString(6, userEmail);
			pstmt.setString(7, userProfile);
			return pstmt.executeUpdate();
			//rs = pstmt.executeQuery(); executeQuery()�� �����͸� ������ �� ��� insert�� ���� ����
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return -1; //������ ���̽� ������ �߻��� ��� 
	}
}
