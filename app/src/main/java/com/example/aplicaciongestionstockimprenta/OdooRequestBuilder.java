package com.example.aplicaciongestionstockimprenta;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class OdooRequestBuilder {

    public static JsonObject buildLoginRequest(String db, String login, String password) {
        JsonObject loginBody = new JsonObject();
        loginBody.addProperty("db", db);
        loginBody.addProperty("login", login);
        loginBody.addProperty("password", password);
        return loginBody;
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
        args.add(db);
        args.add(uid);
        args.add(password);
        args.add(model);
        args.add("search_read");

        // Dominio de búsqueda (opcional, por defecto vacío)
        JsonArray domain = new JsonArray();
        JsonArray condition = new JsonArray();
        condition.add("id");
        condition.add("!=");
        condition.add(0);
        domain.add(condition);

        JsonArray methodArgs = new JsonArray();
        methodArgs.add(domain);
        args.add(methodArgs);

        // kwargs
        JsonObject kwargs = new JsonObject();
        JsonArray fieldsArray = new JsonArray();
        for (String f : fields) {
            fieldsArray.add(f);
        }
        kwargs.add("fields", fieldsArray);

        kwargs.addProperty("limit", 30);  // Ajusta el número si lo necesitas


        // Añadir args y kwargs
        params.add("args", args);
        params.add("kwargs", kwargs);

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

    public static List<String> getGroupNamesFromUserData(JsonObject userData) {
        List<String> groupNames = new ArrayList<>();

        if (userData.has("groups_id") && userData.get("groups_id").isJsonArray()) {
            for (JsonElement groupElement : userData.get("groups_id").getAsJsonArray()) {
                if (groupElement.isJsonArray()) {
                    JsonArray groupArray = groupElement.getAsJsonArray();
                    if (groupArray.size() >= 2) {
                        String groupName = groupArray.get(1).getAsString();
                        groupNames.add(groupName);
                    }
                }
            }
        }

        return groupNames;
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

        // [ [id], { fieldName: value } ]
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
