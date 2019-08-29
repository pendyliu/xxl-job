package com.xxl.job.executor.serviceHuoban;


import cn.hutool.core.util.XmlUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.setting.dialect.Props;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@Component
public class VantopServ {

    static Props props = new Props("application.properties");
    static Document xmlRs;

    /**
     * 获取机构变更信息接口
     */
    public static Object PER_INF_WBS_ORG(String startDate, String endDate) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("Appname", "HRsoft2000");
        paramMap.put("prgname", "PER_INF_WBS_ORG");
        paramMap.put("ARGUMENTS", MessageFormat.format("-A{0},-A{1},-A{2},-A{3},-A{4},-A{5}",
                SecureUtil.md5(props.getProperty("wg.Token")), 1, startDate, endDate, 1, 100));
        String o = HttpUtil.get(props.getProperty("vg.IBasURL"), paramMap);
        XmlUtil2.readStringXml(o.toString());
        xmlRs = XmlUtil.parseXml(o.replace(o.substring(0, o.indexOf("?>") + 2), ""));

        Object xmlObj = xmlRs.getChildNodes();
        return xmlRs;
    }
}
