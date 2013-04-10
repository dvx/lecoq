package controllers;

import org.codehaus.jackson.node.ObjectNode;

import play.libs.F.Function;
import play.libs.Json;
import play.libs.WS;
import play.mvc.*;

import views.html.*;

public class Lecoq extends Controller {

	public static Result index() {
		return ok(index.render());
	}

	public static Result service(String url) {
		try {
			response().setHeader("Access-Control-Allow-Origin", "*");
			return async(WS.url(url).head()
					.map(new Function<WS.Response, Result>() {
						public Result apply(WS.Response response) {
							ObjectNode result = Json.newObject();
							result.put("status", "OK");
							result.put("type", response.getHeader(CONTENT_TYPE));
							return ok(result);
						}
					}));
		} catch (Exception e) {
			ObjectNode result = Json.newObject();
			result.put("status", "KO");
			result.put("err", e.getMessage());
			return ok(result);
		}
	}
}