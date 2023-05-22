package com.fplService.httpService;

import okhttp3.OkHttpClient;

public class FplClient {
    
    private static OkHttpClient client;
    
    private FplClient() {
    }
    
    public static OkHttpClient getFplClient() { 
        if (client == null) {
            client = new OkHttpClient();
        }
        return client;
    }

}
