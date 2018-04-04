package br.net.bmobile.websocketrails;

import android.net.Uri;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpClient.WebSocketConnectCallback;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.WebSocket.StringCallback;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebSocketRailsConnection implements StringCallback, CompletedCallback, WebSocketConnectCallback {

    private URL url;
    private WebSocketRailsDispatcher dispatcher;
    private List<WebSocketRailsEvent> message_queue;
    private WebSocket webSocket;
    private Map<String, String> mHeards;

    public WebSocketRailsConnection(URL url, WebSocketRailsDispatcher dispatcher, Map<String, String> heards) {

        this.url = url;
        this.mHeards = heards;
        this.dispatcher = dispatcher;
        this.message_queue = new ArrayList<WebSocketRailsEvent>();
    }

    public void trigger(WebSocketRailsEvent event) {

        if (!"connected".equals(dispatcher.getState()))
            message_queue.add(event);
        else
            webSocket.send(event.serialize());
    }

    public void flushQueue(String id) {

        for (WebSocketRailsEvent event : message_queue) {
            String serializedEvent = event.serialize();
            webSocket.send(serializedEvent);
        }
    }

    public void connect() {

        try {
            disconnect();
            Uri uri = Uri.parse(url.toURI().toString());
            AsyncHttpGet asyncHttpGet = new AsyncHttpGet(uri);
            if (mHeards != null && !mHeards.isEmpty()) {
                for (Map.Entry<String, String> entry : mHeards.entrySet()) {
                    asyncHttpGet.addHeader(entry.getKey(), entry.getValue());
                }
            }
            AsyncHttpClient.getDefaultInstance().websocket(
                    asyncHttpGet, null, this);

        } catch (Exception e) {
            Log.e("WSRailsConnection", "exception", e);
        }
    }

    public void disconnect() {
        if (webSocket != null) {
            webSocket.close();
            webSocket.setStringCallback(null);
            webSocket.setClosedCallback(null);
        }
    }

    public boolean isconnect() {
        return webSocket != null && webSocket.isOpen();
    }

    @Override
    public void onCompleted(Exception arg0, WebSocket ws) {
        if (ws == null) {
            List<Object> data = new ArrayList<Object>();
            data.add("connection_error");
            data.add(new HashMap<String, Object>());
            WebSocketRailsEvent closeEvent = new WebSocketRailsEvent(data);
            dispatcher.setState("onError");
            dispatcher.dispatch(closeEvent);
            return;
        }
        try {
            ws.setStringCallback(this);
            ws.setClosedCallback(this);

            disconnect();

            this.webSocket = ws;
        } catch (Exception e) {
            List<Object> data = new ArrayList<Object>();
            data.add("connection_error");
            data.add(new HashMap<String, Object>());
            WebSocketRailsEvent closeEvent = new WebSocketRailsEvent(data);
            dispatcher.setState("onError");
            dispatcher.dispatch(closeEvent);
        }
    }

    @Override
    public void onCompleted(Exception arg0) {
        List<Object> data = new ArrayList<Object>();
        data.add("connection_closed");
        data.add(new HashMap<String, Object>());

        WebSocketRailsEvent closeEvent = new WebSocketRailsEvent(data);
        dispatcher.setState("disconnected");
        dispatcher.dispatch(closeEvent);
    }

    @Override
    public void onStringAvailable(String data) {

        ObjectMapper mapper = new ObjectMapper();

        // read JSON from a file
        List<Object> list;
        try {
            list = mapper.readValue(
                    data,
                    new TypeReference<List<Object>>() {
                    });

            dispatcher.newMessage(list);

        } catch (Exception e) {
            Log.e("WSRailsConnection", "exception", e);
        }
    }
}
