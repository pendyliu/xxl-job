package com.xxl.job.executor.serviceHuoban;

import com.xxl.job.executor.Models.VGORG;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XmlUtil2 {

    public static void readStringXml(String xml) {
        Document doc = null;
        List<VGORG> vgorgs = new ArrayList<>();
        try {
            // 将字符串转为XML
            doc = DocumentHelper.parseText(xml.replace(xml.substring(0, xml.indexOf("?>") + 2), ""));

            // 获取根节点
            Element rootElt = doc.getRootElement();

            // 拿到根节点的名称
            System.out.println("根节点：" + rootElt.getName());

            // 获取根节点下的子节点STAFF
            Iterator iter = rootElt.elementIterator("STAFF");

            // 遍历STAFF节点
            while (iter.hasNext()) {
                Element recordEle = (Element) iter.next();
                // 获取子节点STAFF下的子节点ROW
                Iterator iters = recordEle.elementIterator("ROW");

                // 遍历ROW节点下的Response节点
                while (iters.hasNext()) {
                    Element itemEle = (Element) iters.next();
                    VGORG orgObj = new VGORG();
                    // 拿到STAFF下的子节点ROW下的字节点组织节点的值
                    orgObj.setBRANCH(itemEle.elementTextTrim("BRANCH"));
                    orgObj.setBRANCH_DESCRIPTION(itemEle.elementTextTrim("BRANCH_DESCRIPTION"));
                    orgObj.setBRANCH_END(itemEle.elementTextTrim("BRANCH_END"));
                    orgObj.setCITY(itemEle.elementTextTrim("CITY"));
                    orgObj.setCITY_DESCRIPTION(itemEle.elementTextTrim("CITY_DESCRIPTION"));
                    orgObj.setCITY_END(itemEle.elementTextTrim("CITY_END"));
                    orgObj.setDEPARTMENT(itemEle.elementTextTrim("DEPARTMENT"));
                    orgObj.setDEPARTMENT_DESCRIPTION(itemEle.elementTextTrim("DEPARTMENT_DESCRIPT"));
                    orgObj.setDEPARTMENT_END(itemEle.elementTextTrim("DEPARTMENT_END"));
                    orgObj.setEFFECTIVE_DATE(itemEle.elementTextTrim("EFFECTIVE_DATE"));
                    orgObj.setINVALID_DATE(itemEle.elementTextTrim("INVALID_DATE"));
                    orgObj.setJOB_TITLE(itemEle.elementTextTrim("JOB_TITLE"));
                    orgObj.setMOD_DATE(itemEle.elementTextTrim("MOD_DATE"));
                    orgObj.setPOSITION(itemEle.elementTextTrim("POSITION"));
                    orgObj.setPOSITION_CODE(itemEle.elementTextTrim("POSITION_CODE"));
                    orgObj.setPOSITION_END(itemEle.elementTextTrim("POSITION_END"));
                    orgObj.setRANK(itemEle.elementTextTrim("RANK"));
                    orgObj.setRANK_DESCRIPTION(itemEle.elementTextTrim("RANK_DESCRIPTION"));
                    orgObj.setRANK_END(itemEle.elementTextTrim("RANK_END"));
                    orgObj.setSECTION(itemEle.elementTextTrim("SECTION"));
                    orgObj.setSECTION_DESCRIPTION(itemEle.elementTextTrim("SECTION_DESCRIPTION"));
                    orgObj.setSECTION_END(itemEle.elementTextTrim("SECTION_END"));
                    orgObj.setSUB_DESCRIPTION(itemEle.elementTextTrim("SUB_DESCRIPTION"));
                    orgObj.setSUB_SECTION(itemEle.elementTextTrim("SUB_SECTION"));
                    orgObj.setSUB_SECTION_END(itemEle.elementTextTrim("SUB_SECTION_END"));
                    vgorgs.add(orgObj);
                }

            }

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
