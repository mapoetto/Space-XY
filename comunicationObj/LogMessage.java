package comunicationObj;

public class LogMessage {
	private String type;
	private String content;
	private String data;
	
	public LogMessage(String type, String content, String data) {
		this.type = type;
		this.content = content;
		this.data = data;
	}

	public String getType() {
		return type;
	}

	public String getContent() {
		return content;
	}

	public String getData() {
		return data;
	}
}
