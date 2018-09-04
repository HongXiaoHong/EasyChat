package i.½âÎöÆ÷;

public class User {

	public static final int LOGIN = 1;
	public static final int REGISTER = 2;
	
	private String name;
	private String pass;
	private String nickName;
	private String mark;
	private String img;
	private String socketId;
	
	public boolean equals(User user, int flag){
		// ... [ ´ý¶¨ ] ...
		return false;
	}

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public String getPass() { return pass; }
	public void setPass(String pass) { this.pass = pass; }

	public String getNickName() { return nickName; }
	public void setNickName(String nickName) { this.nickName = nickName; }

	public String getMark() { return mark; }
	public void setMark(String mark) { this.mark = mark; }

	public String getImg() { return img; }
	public void setImg(String img) { this.img = img; }

	public String getSocketId() { return socketId; }
	public void setSocketId(String socketId) { this.socketId = socketId; }
	
}
