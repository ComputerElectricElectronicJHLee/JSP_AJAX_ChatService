package chat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class ChatDAO {
		
		//Connection pool�� �̿��ϱ� ����
		DataSource dataSource;
		
		//��ü�� �������ڸ��� �����ͺ��̽��� ������ �� �ִ� ������
		public ChatDAO() {
			try {
				InitialContext initContext = new InitialContext();
				Context envContext = (Context) initContext.lookup("java:/comp/env"); //�ҽ��� ���� �� �� �ֵ��� �ϴ� ���
				dataSource = (DataSource) envContext.lookup("jdbc/UserChat"); //�ҽ� �߰��ϰ� �Ǹ� ������Ʈ ����
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		//Ư�� ID�� ���� ä�� ���� get
		public ArrayList<ChatDTO> getChatListByID(String fromID, String toID, String chatID){
			ArrayList<ChatDTO> chatList = null;
			Connection conn = null;
			PreparedStatement pstmt = null; //SQL Injection���� ��ŷ������ ������ְ� ���������� SQL���� �����ϰ� ����
			ResultSet rs = null;
			String SQL = "SELECT * FROM CHAT WHERE ((fromID = ? AND toID = ?) OR (fromID = ? AND toID = ?)) AND chatID > ? ORDER BY chatTime";
			try {
				conn = dataSource.getConnection();
				pstmt = conn.prepareStatement(SQL);
				pstmt.setString(1, fromID);
				pstmt.setString(2, toID);
				pstmt.setString(3, toID);
				pstmt.setString(4, fromID);
				pstmt.setInt(5, Integer.parseInt(chatID));
				rs = pstmt.executeQuery();
				chatList = new ArrayList<ChatDTO>();
				while (rs.next()) {
					ChatDTO chat = new ChatDTO();
					chat.setChatID(rs.getInt("chatID"));
					//SQL injection, cross site scripting ���� � ���� ����ϱ� ���� Ư������ ġȯ
					chat.setFromID(rs.getString("fromID").replaceAll(" ", "&nbsp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>")); 
					chat.setToID(rs.getString("toID").replaceAll(" ", "&nbsp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>"));
					chat.setChatContent(rs.getString("chatContent").replaceAll(" ", "&nbsp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>"));
					int chatTime = Integer.parseInt(rs.getString("chatTime").substring(11, 13));
					String timeType = "����";
					if (chatTime == 12) {
						timeType = "����";
					}
					else if(chatTime > 12) {
						timeType = "����";
						chatTime -= 12;
					}
					chat.setChatTime(rs.getString("chatTime").substring(0, 11) + " " + timeType + " " + chatTime + ":" + rs.getString("chatTime").substring(14, 16) + "");
					chatList.add(chat);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(rs != null) rs.close();
					if(pstmt != null) pstmt.close();
					if(conn != null) conn.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return chatList; //����Ʈ ��ȯ
		}
		
		//�ֱ� ��ȭ ���� ��ȯ
		public ArrayList<ChatDTO> getChatListByRecent(String fromID, String toID, int number){
			ArrayList<ChatDTO> chatList = null;
			Connection conn = null;
			PreparedStatement pstmt = null; //SQL Injection���� ��ŷ������ ������ְ� ���������� SQL���� �����ϰ� ����
			ResultSet rs = null;
			String SQL = "SELECT * FROM CHAT WHERE ((fromID = ? AND toID = ?) OR (fromID = ? AND toID = ?) AND chatID > (SELECT MAX(chatID) - ? FROM CHAT) ORDER BY chatTime";
			try {
				conn = dataSource.getConnection();
				pstmt = conn.prepareStatement(SQL);
				pstmt.setString(1, fromID);
				pstmt.setString(2, toID);
				pstmt.setString(3, toID);
				pstmt.setString(4, fromID);
				pstmt.setInt(5, number);
				rs = pstmt.executeQuery();
				chatList = new ArrayList<ChatDTO>();
				while (rs.next()) {
					ChatDTO chat = new ChatDTO();
					chat.setChatID(rs.getInt("chatID"));
					//SQL injection, cross site scripting ���� � ���� ����ϱ� ���� Ư������ ġȯ
					chat.setFromID(rs.getString("fromID").replaceAll(" ", "&nbsp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>")); 
					chat.setToID(rs.getString("toID").replaceAll(" ", "&nbsp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>"));
					chat.setChatContent(rs.getString("chatContent").replaceAll(" ", "&nbsp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>"));
					int chatTime = Integer.parseInt(rs.getString("chatTime").substring(11, 13));
					String timeType = "����";
					if (chatTime == 12) {
						timeType = "����";
					}
					else if(chatTime > 12) {
						timeType = "����";
						chatTime -= 12;
					}
					chat.setChatTime(rs.getString("chatTime").substring(0, 11) + " " + timeType + " " + chatTime + ":" + rs.getString("chatTime").substring(14, 16) + "");
					chatList.add(chat);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(rs != null) rs.close();
					if(pstmt != null) pstmt.close();
					if(conn != null) conn.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return chatList; //����Ʈ ��ȯ
		}
		
		//���� ���(���� ���� �ľ��� ���� int)
		public int submit(String fromID, String toID, String chatContent){
			Connection conn = null;
			PreparedStatement pstmt = null; //SQL Injection���� ��ŷ������ ������ְ� ���������� SQL���� �����ϰ� ����
			ResultSet rs = null;
			String SQL = "INSERT INTO CHAT VALUES (NULL, ?, ?, ?, NOW())"; //NULL ������ chatID�� 1�� ����
			try {
				conn = dataSource.getConnection();
				pstmt = conn.prepareStatement(SQL);
				pstmt.setString(1, fromID);
				pstmt.setString(2, toID);
				pstmt.setString(3, chatContent);
				return pstmt.executeUpdate(); //1�� ��ȯ�ǰ� SQL���� �����
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(rs != null) rs.close();
					if(pstmt != null) pstmt.close();
					if(conn != null) conn.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return -1; //�����ͺ��̽� ����
		}
}
