package controllers;

import org.codehaus.jackson.node.ObjectNode;

import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.Json;
import play.libs.WS;
import play.mvc.*;
import play.mvc.Http.Context;
import views.html.*;

public class Lecoq extends Controller {

	public static Result index() {
		return ok(index.render());
	}

	public static Result service(String url) {
		try {
	        Promise<Result> res = WS
	                .url(url).head()
	                .map(new Function<WS.Response, Result>() {
	                    public Result apply(WS.Response response) {
							ObjectNode result = Json.newObject();
							result.put("status", "OK");
							result.put("type", response.getHeader(CONTENT_TYPE));
							return ok(result);
	                        }
	                    }
	                );			
	        
			Context.current().response().setHeader("Access-Control-Allow-Origin", "*");
	        return res.getWrappedPromise().await().get();
		} catch (Exception e) {
			ObjectNode result = Json.newObject();
			result.put("status", "KO");
			result.put("err", e.getMessage());
			return ok(result);
		}
	}
}