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

	// goto index/API doc
	public static Result index() {
		return ok(index.render());
	}
	
	// this does all the work
	public static Result service(String url) {
		try {
			// this is really messy. I used to like Play, but not so much recently :/
	        Promise<Result> res = WS
	                .url(url).head()
	                .map(new Function<WS.Response, Result>() {
	                    public Result apply(WS.Response response) {
							ObjectNode result = Json.newObject();
							result.put("status", "OK");
							// assuming the server is being honest...
							result.put("type", response.getHeader(CONTENT_TYPE));
							return ok(result);
	                        }
	                    }
	                );
	        
	        // for that CORS goodness
			Context.current().response().setHeader("Access-Control-Allow-Origin", "*");
			
			// don't waste server resources, 5 seconds to reach your destination
	        return res.getWrappedPromise().await(5, java.util.concurrent.TimeUnit.SECONDS).get();
		}
        // need to use pokemon pattern here (gotta' catch'em all)
        // in reality, we need TimeoutException, IllegalArgumentException, and a bunch of others
        // but Promises are fucked in Play 2.0.x and exceptions don't trickle down
		catch (Exception e) {
			ObjectNode result = Json.newObject();
			result.put("status", "KO");
			result.put("err", e.getMessage());
			return ok(result);
		}
	}
}