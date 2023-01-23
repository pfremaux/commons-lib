package commons.lib.extra.server.http.handler.testLib.generators;

import commons.lib.main.StringUtils;

public class JsGenerator {
    private JsGenerator() {

    }

    public static String asyncCallSource() {
        return """
                function asyncCall(method, fn) {
                	let xhttp = new XMLHttpRequest();
                	//xhttp.setRequestHeader(header, value);
                	xhttp.onreadystatechange = function() {
                		if (this.readyState == 4 && this.status == 200) {
                			fn(this.responseText);
                			xhttp.open(method, path, true);
                		}
                	};
                	xhttp.send();
                }""";
    }

    public static StringBuilder generateJsCall(DocumentedEndpoint info) {
        final StringBuilder builder = new StringBuilder();
        final String methodName = StringUtils.snakeCase(info.getMethod().toLowerCase() + "_" + info.getPath().replaceAll("/", "_"));
        builder.append("// Calls ");
        builder.append(info.getMethod());
        builder.append(" ");
        builder.append(info.getPath());
        builder.append("function ");
        builder.append(methodName);
        builder.append("(");
        // TODO add parameters
        builder.append(") {\n");
        builder.append("\tasyncCall(");
        builder.append(info.getMethod());
        builder.append(", ");
        //builder.append();
        builder.append(");\n");
        builder.append("}");

        return builder;
    }
}
