package spark;

import static spark.Spark.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;


public class Main {

  public static String processRoute(Request req, Response res) {
    Set<String> params = req.queryParams();
    for (String param : params) {
      // possible for query param to be an array
      System.out.println(param + " : " + req.queryParamsValues(param)[0]);
    }
    // do stuff with a mapped version http://javadoc.io/doc/com.sparkjava/spark-core/2.8.0
    // http://sparkjava.com/documentation#query-maps
    // print the id query value
    System.out.println(req.queryMap().get("id").value());
    return "done!";
  }

  public static void main(String[] args) {
    port(1234);
    // calling get will make your app start listening for the GET path with the /hello endpoint
    get("/hello", (req, res) -> "Hello World");

    // showing a lambda expression with block body
    get("/test", (req, res) -> {
      // print some stuff about the request
      // http://sparkjava.com/documentation#routes
      System.out.println(req.attributes());
      System.out.println(req.headers());
      System.out.println(req.ip());
      System.out.println(req.url());
      System.out.println(req.userAgent());
      return "This one has a block body";
    });

    // Slightly more advanced routing
    path("/api", () -> {
      get("/users", (req, res) -> {
        return "This one has a block body";
      });
      get("/posts", Main::processRoute);
      get("/404test", (req, res) -> {
        // print some stuff about the request
        res.status(404);
        return "";
      });
    });
  }
}
