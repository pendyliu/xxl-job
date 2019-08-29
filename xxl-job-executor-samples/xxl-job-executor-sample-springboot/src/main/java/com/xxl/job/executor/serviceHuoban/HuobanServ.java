package com.xxl.job.executor.serviceHuoban;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.dialect.Props;
import org.springframework.stereotype.Component;

@Component
public class HuobanServ {
    static Props props = new Props("application.properties");
    String BaseURL = props.getProperty("HuoBanBaseURL");

    public static JSONObject getTicketJson() {
        return (ticketJson == null || DateTime.now().isAfter(ticketJson.getDate("expire_at"))) ?
                getTicket() : ticketJson;
    }

    public static JSONObject ticketJson = null;

    static JSONObject getTicket() {
        String ticketUrl = "https://api.huoban.com/v2/ticket";
        JSONObject paramJson = new JSONObject();
        paramJson.put("application_id", props.getProperty("huoban.application_id"));
        paramJson.put("application_secret", props.getProperty("huoban.application_secret"));
        paramJson.put("expired", props.getProperty("huoban.expired"));

        ticketJson = JSONUtil.parseObj(JSONUtil.formatJsonStr(UnicodeUtil.toString(
                HttpRequest.post(ticketUrl)
                        .header("Content-Type", "application/json")
                        .body(paramJson.toString())
                        .execute().body())));
        return ticketJson;
    }

    public void getSpaces() {
        String result = HttpRequest.get("https://api.huoban.com/v2/spaces/joined").
                header(Header.CONTENT_TYPE, "application/json").
                header(Header.HOST, "api.huoban.com")
                .header("X-Huoban-Ticket", ticketJson.getStr("ticket"))
                .execute().body();
    }
}
