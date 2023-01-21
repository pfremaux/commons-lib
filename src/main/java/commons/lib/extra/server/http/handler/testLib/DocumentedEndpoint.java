package commons.lib.extra.server.http.handler.testLib;

public class DocumentedEndpoint {
	private String method;
	private String path;
	private String description;
	private String bodyExample;
	private String responseExample;

	public String getMethod() {
		return method;
	}

	public String getPath() {
		return path;
	}

	public String getDescription() {
		return description;
	}

	public String getResponseExample() {
		return responseExample;
	}

	public String getBodyExample() {
		return bodyExample;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setBodyExample(String bodyExample) {
		this.bodyExample = bodyExample;
	}

	public void setResponseExample(String responseExample) {
		this.responseExample = responseExample;
	}

}
