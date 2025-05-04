package com.example.aplicaciongestionstockimprenta;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class OdooRequestBuilder {

    public static JsonObject buildLoginRequest(String db, String username, String password) {
        JsonObject request = new JsonObject();
        request.addProperty("jsonrpc", "2.0");
        request.addProperty("method", "call");
        request.addProperty("id", 1);

        JsonObject params = new JsonObject();
        params.addProperty("service", "common");
        params.addProperty("method", "login");

        JsonArray args = new JsonArray();
        args.add(db);
        args.add(username);
        args.add(password);
        params.add("args", args);

        request.add("params", params);
        return request;
    }

    public static JsonObject buildSearchReadRequest(String db, int uid, String password, String model, String[] fields) {
        JsonObject request = new JsonObject();
        request.addProperty("jsonrpc", "2.0");
        request.addProperty("method", "call");
        request.addProperty("id", 1);

        JsonObject params = new JsonObject();
        params.addProperty("service", "object");
        params.addProperty("method", "execute_kw");

        JsonArray args = new JsonArray();
        args.add(new JsonPrimitive(db));
        args.add(new JsonPrimitive(uid));
        args.add(new JsonPrimitive(password));
        args.add(new JsonPrimitive(model));
        args.add(new JsonPrimitive("search_read"));

        // Dominio correctamente envuelto
        JsonArray condition = new JsonArray();
        condition.add("id");
        condition.add("=");
        condition.add(uid);
        JsonArray domain = new JsonArray();
        domain.add(condition);
        JsonArray domainWrapper = new JsonArray();
        domainWrapper.add(domain);
        args.add(domainWrapper);

        // kwargs
        JsonObject kwargs = new JsonObject();
        JsonArray fieldsArray = new JsonArray();
        for (String f : fields) {
            fieldsArray.add(f);
        }
        kwargs.add("fields", fieldsArray);
        args.add(kwargs);

        params.add("args", args);
        request.add("params", params);

        return request;
    }

    public static JsonObject buildUserGroupsRequest(String db, int uid, String password, int userId) {
        JsonObject request = new JsonObject();
        request.addProperty("jsonrpc", "2.0");
        request.addProperty("method", "call");
        request.addProperty("id", 1);

        JsonObject params = new JsonObject();
        params.addProperty("service", "object");
        params.addProperty("method", "execute_kw");

        JsonArray args = new JsonArray();
        args.add(db);
        args.add(uid);
        args.add(password);
        args.add("res.users");
        args.add("read");

        JsonArray readArgs = new JsonArray();
        JsonArray userIdList = new JsonArray();
        userIdList.add(userId);
        readArgs.add(userIdList);
        args.add(readArgs);

        JsonObject kwargs = new JsonObject();
        JsonArray fields = new JsonArray();
        fields.add("groups_id");
        kwargs.add("fields", fields);
        args.add(kwargs);

        params.add("args", args);
        request.add("params", params);

        return request;
    }

}
