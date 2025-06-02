package com.example.aplicaciongestionstockimprenta.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class OdooRequestBuilder {
    public static JsonObject buildSearchReadRequest(String db, int uid, String password, String model, String[] fields) {
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
        args.add(model);
        args.add("search_read");

        JsonArray domain = new JsonArray();
        JsonArray condition = new JsonArray();
        condition.add("id");
        condition.add("!=");
        condition.add(0);
        domain.add(condition);

        JsonArray methodArgs = new JsonArray();
        methodArgs.add(domain);
        args.add(methodArgs);

        JsonObject kwargs = new JsonObject();
        JsonArray fieldsArray = new JsonArray();
        for (String f : fields) {
            fieldsArray.add(f);
        }
        kwargs.add("fields", fieldsArray);

        kwargs.addProperty("limit", 30);

        params.add("args", args);
        params.add("kwargs", kwargs);

        request.add("params", params);

        return request;
    }


    public static JsonObject buildWriteRequest(String db, int uid, String password,
                                               String model, int productId, String fieldName, int value) {
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
        args.add(model);
        args.add("write");

        JsonArray data = new JsonArray();
        JsonArray idList = new JsonArray();
        idList.add(productId);
        data.add(idList);

        JsonObject values = new JsonObject();
        values.addProperty(fieldName, value);
        data.add(values);

        args.add(data);
        params.add("args", args);
        request.add("params", params);

        return request;
    }

}
