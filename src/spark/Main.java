package spark;

import static spark.Spark.*;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import org.eclipse.jetty.server.session.Session;

class Message {
  public String message;
}

public class Main {
  public static ArrayList<String> messages = new ArrayList<>();

  public static String processRoute(Request req, Response res) {
    Set<String> params = req.queryParams();
    for (String param : params) {
      // possible for query param to be an array
      System.out.println(param + " : " + req.queryParamsValues(param)[0]);
    }
    // do stuff with a mapped version http://javadoc.io/doc/com.sparkjava/spark-core/2.8.0
    // http://sparkjava.com/documentation#query-maps
    // print the id query value
    System.out.println(req.queryMap().get("userid").value());
    return "done!";
  }

  public static void main(String[] args) {
    messages.add("A sample message");
    Gson gson = new Gson();
    port(1234);
    webSocket("/ws", WebSocketHandler.class);
    // It needs this for development because our react server is on a different port!
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

    post("/api/sendmessage", (req, res) -> {
      String body = req.body();
      System.out.println(body);
      Message messageData = gson.fromJson(body, Message.class);
      messages.add(messageData.message);
      // Message type
      JsonObject broadcastMessage = new JsonObject();
      broadcastMessage.addProperty("type", "MESSAGE_BROADCAST");
      broadcastMessage.addProperty("message", messageData.message);

      /*
      {
      type: "MESSAGE_BROADCAST",
      message: data
      }
       */
      WebSocketHandler.broadcast(broadcastMessage.toString());
      return "OK";
    });

    // Slightly more advanced routing
    path("/api", () -> {
      get("/messages", (req, res) -> {
        return gson.toJson(messages);
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
