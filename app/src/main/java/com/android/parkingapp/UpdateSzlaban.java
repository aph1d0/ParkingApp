package com.android.parkingapp;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
public class UpdateSzlaban extends StringRequest{
    private static final String EARN_REQUEST_URL = "http://filipsmolinski037.eu/andr/update_szlaban.php";
    private Map<String, String> params;

    public UpdateSzlaban(String sz_username, String rejestracja, Response.Listener<String> listener) {
        super(Method.POST, EARN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("sz_username", sz_username);
        params.put("rejestracja", rejestracja);

    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
