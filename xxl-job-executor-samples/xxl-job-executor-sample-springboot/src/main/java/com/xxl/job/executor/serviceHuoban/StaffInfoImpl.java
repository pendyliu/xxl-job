package com.xxl.job.executor.serviceHuoban;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.Models.HbTablesId;
import com.xxl.job.executor.Models.KeyValueModel;
import com.xxl.job.executor.Models.Staff_Info;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.xxl.job.executor.service.jobhandler.PersonHbJobHandler.tableStuckCache;

public class StaffInfoImpl extends BaseHuoBanServ implements IHuoBanService {
    public static String staffInfoTbStruc = "staff_info";
    public static String staffInfoItemsId = "staffInfoItemsId";
    Staff_Info staffInfo = new Staff_Info();
    String superiorItemId = "";


    @Override
    public void createFieldsIdMap() {
        if (tableStuckCache.get(staffInfoTbStruc) == null) {
            JSONObject jsonObject = getTables(HbTablesId.staff_info);
            setFildsMap(jsonObject, staffInfo);
            /**
             * 将表结构对象存放到缓存当中
             */
            tableStuckCache.put(staffInfoTbStruc, staffInfo);
        }
        if (tableStuckCache.get(staffInfoItemsId) == null) {
            tableStuckCache.put(staffInfoItemsId, new HashMap<>());
        }
    }

    @Override
    public Map getLocalItemId(JSONObject paramJson, Element element) {
        return null;
    }

    @Override
    public String getCacheItemId(Element element) {
        return null;
    }

    @Override
    public List readStringXml(String xml) {
        Document doc = null;
        try {
            // 将字符串转为XML
            doc = DocumentHelper.parseText(xml.replace(xml.substring(0, xml.indexOf("?>") + 2), ""));
            // 获取根节点
            Element rootElt = doc.getRootElement();
            // 拿到根节点的名称
            System.out.println("人员信息更新开始......." );
            // 获取根节点下的子节点STAFF
            Iterator iter = rootElt.elementIterator("STAFF");
            // 遍历STAFF节点
            while (iter.hasNext()) {
                Element recordEle = (Element) iter.next();
                // 获取子节点STAFF下的子节点ROW
                Iterator iters = recordEle.elementIterator("ROW");
                // 遍历ROW节点下的Response节点
                while (iters.hasNext()) {
                    Element element = (Element) iters.next();
                    // 拿到STAFF下的子节点ROW下的字节点组织节点的值
                    System.out.println("开始更新：" + element.elementText("STAFF_NO"));
//                    if (!element.elementText("STAFF_NO").equals("G98778")) {
//                        continue;
//                    }
                    if (Convert.toInt(StrUtil.emptyToDefault(element.elementText("isLeader"), "0")) > 0) {
                        //判断这个人是否有伙伴帐号存在
                        getMemberId(element.elementText("MOBILE_PHONE"));
                    }

                    //获取这个人的上长的ItemId
                    superiorItemId = getCacheItemsId(JSONUtil.createObj().put("tableId", HbTablesId.staff_info)
                            .put("field_id", ((Staff_Info) tableStuckCache.get(staffInfoTbStruc)).getStaff_number().getField_id())
                            .put("field_value", element.elementText("SUPERVISOR_NAME")), this, element, true);

                    String staffInfoItemId = getCacheItemsId(JSONUtil.createObj().put("tableId", HbTablesId.staff_info)
                            .put("field_id", ((Staff_Info) tableStuckCache.get(staffInfoTbStruc)).getStaff_number().getField_id())
                            .put("field_value", element.elementText("STAFF_NO")), this, element, false);
                    XxlJobLogger.log("工号：" + element.elementText("STAFF_NO") + "  ItemId：" + staffInfoItemId + "更新成功！");
                }
            }
        } catch (DocumentException e) {
            XxlJobLogger.log(e.getMessage());
        } catch (Exception e) {
            XxlJobLogger.log(e.getMessage());
        } finally {

        }
        System.out.println("更新完成！");
        return null;
    }

    @Override
    public JSONObject insertTable(Element element) {
        staffInfo = (Staff_Info) tableStuckCache.get(staffInfoTbStruc);
        String companyItemId = new CompanyImpl().getCacheItemId(element);
        String firDepartItemId = new FirDepartMentImpl().getCacheItemId(element);
        String sec_depart_code = new Sec_DepartImpl().getCacheItemId(element);
        String subSectionCode = new KeClassImpl().getCacheItemId(element);
        String groupCode = new GroupImpl().getCacheItemId(element);
        String teamCode = new TeamNameImpl().getCacheItemId(element);
        String postItemId = new PostNameImpl().getCacheItemId(element);
        int sex ="W".equals(element.elementText("SEX"))  ? 2 : 1;
        int status = "T".equals(element.elementText("STATUS")) ? 2 : 1;
        int isLeader = Convert.toInt(StrUtil.blankToDefault(element.elementText("isLeader"), "2"));
        String memberUserId="";
        if (Convert.toInt(StrUtil.emptyToDefault(element.elementText("isLeader"), "0")) > 0) {
            //判断这个人是否有伙伴帐号存在
            memberUserId= getMemberId(element.elementText("MOBILE_PHONE"));
        }
        String TERMINATION_DATE = element.elementText("TERMINATION_DATE") != null && !(element.elementText("TERMINATION_DATE").equals("")) ? new SimpleDateFormat("yyyy-MM-dd").
                format(DateUtil.parse(element.elementText("TERMINATION_DATE").substring(0, 10).replace(" ", "/").replace("//", "/"), "MM/dd/yyyy")) : "";
        String DATE_OF_BIRTH = element.elementText("DATE_OF_BIRTH") != null && !("".equals(element.elementText("DATE_OF_BIRTH"))) ? new SimpleDateFormat("yyyy-MM-dd").
                format(DateUtil.parse(element.elementText("DATE_OF_BIRTH").substring(0, 10).replace(" ", "/").replace("//", "/"), "MM/dd/yyyy")) : "";
        JSONObject dataJson = JSONUtil.createObj().put("fields", JSONUtil.createObj().
                put(staffInfo.getStaff_number().getField_id(), element.elementText("STAFF_NO"))
                .put(staffInfo.getStaff_name().getField_id(), element.elementText("STAFF_NAME"))
                .put(staffInfo.getStaff_birth().getField_id(), DATE_OF_BIRTH)
                .put(staffInfo.getStaff_gender().getField_id(), JSONUtil.createArray().put(sex))
                .put(staffInfo.getCompany().getField_id(), JSONUtil.createArray().put(companyItemId))
                .put(staffInfo.getFir_depart().getField_id(), JSONUtil.createArray().put(firDepartItemId))
                .put(staffInfo.getSec_depart().getField_id(), JSONUtil.createArray().put(sec_depart_code))
                .put(staffInfo.getKeClass().getField_id(), JSONUtil.createArray().put(subSectionCode))
                .put(staffInfo.getGroup().getField_id(), JSONUtil.createArray().put(groupCode))
                .put(staffInfo.getTeam().getField_id(), JSONUtil.createArray().put(teamCode))
                .put(staffInfo.getStaff_status().getField_id(), JSONUtil.createArray().put(status))
                .put(staffInfo.getStaff_edu().getField_id(), element.elementText(""))
                .put(staffInfo.getSepar_time().getField_id(), TERMINATION_DATE)
                .put(staffInfo.getIs_leader().getField_id(), JSONUtil.createArray().put(isLeader))
        );
        if (!"".equals(postItemId)) {
            dataJson.put(staffInfo.getPost().getField_id(), JSONUtil.createArray().put(postItemId));
        }
        if (!"".equals(superiorItemId)) {
            dataJson.put(staffInfo.getSuperior().getField_id(), JSONUtil.createArray().put(superiorItemId));
        }
        if (!"".equals(memberUserId)){
            dataJson.put(staffInfo.getStaff_member().getField_id(),JSONUtil.createArray().put(memberUserId));
        }
        JSONObject result = insertTable(dataJson, HbTablesId.staff_info);
        return result;
    }

    @Override
    public JSONObject updateTable(JSONObject jsonObject, Element element) {
        JSONObject resultJson = null;
        try {
            resultJson = JSONUtil.createObj();
            String companyItemId = new CompanyImpl().getCacheItemId(element);
            String firDepartItemId = new FirDepartMentImpl().getCacheItemId(element);
            String sec_depart_code = new Sec_DepartImpl().getCacheItemId(element);
            String subSectionCode = new KeClassImpl().getCacheItemId(element);
            String groupCode = new GroupImpl().getCacheItemId(element);
            String teamCode = new TeamNameImpl().getCacheItemId(element);
            String postItemId = new PostNameImpl().getCacheItemId(element);
            int status = "T".equals(element.elementText("STATUS")) ? 2 : 1;
            int sex = element.elementText("SEX").equals("W") ? 2 : 1;
            int isLeader = Convert.toInt(StrUtil.blankToDefault(element.elementText("isLeader"), "2"));
            String memberUserId="";
            if (Convert.toInt(StrUtil.emptyToDefault(element.elementText("isLeader"), "0")) > 0) {
                //判断这个人是否有伙伴帐号存在
                memberUserId= getMemberId(element.elementText("MOBILE_PHONE"));
            }
            String cnName = StrUtil.split(((JSONObject) ((JSONArray) jsonObject.get("items")).get(0)).get("title").toString(), "")[0];
            String fieldCnName = jsonObject.getStr("fieldCnName") == null ? element.elementText("STAFF_NAME") : jsonObject.getStr("fieldCnName");
            resultJson.put("fieldCnName", fieldCnName);
            Staff_Info staffInfo = (Staff_Info) tableStuckCache.get(staffInfoTbStruc);
            String itemId = ((JSONObject) ((JSONArray) jsonObject.get("items")).get(0)).get("item_id").toString();
            String TERMINATION_DATE = element.elementText("TERMINATION_DATE") != null && !(element.elementText("TERMINATION_DATE").equals("")) ? new SimpleDateFormat("yyyy-MM-dd").
                    format(DateUtil.parse(element.elementText("TERMINATION_DATE").substring(0, 10).replace(" ", "/").replace("//", "/"), "MM/dd/yyyy")) : "";
            String DATE_OF_BIRTH = element.elementText("DATE_OF_BIRTH") != null && !("".equals(element.elementText("DATE_OF_BIRTH"))) ? new SimpleDateFormat("yyyy-MM-dd").
                    format(DateUtil.parse(element.elementText("DATE_OF_BIRTH").substring(0, 10).replace(" ", "/").replace("//", "/"), "MM/dd/yyyy")) : "";
            JSONObject dataJson = JSONUtil.createObj().put(staffInfo.getStaff_name().getField_id(), element.elementText("STAFF_NAME"))
                    .put(staffInfo.getStaff_name().getField_id(), element.elementText("STAFF_NAME"))
                    .put(staffInfo.getStaff_birth().getField_id(), DATE_OF_BIRTH)
                    .put(staffInfo.getStaff_gender().getField_id(), JSONUtil.createArray().put(sex))
                    .put(staffInfo.getCompany().getField_id(), JSONUtil.createArray().put(companyItemId))
                    .put(staffInfo.getFir_depart().getField_id(), JSONUtil.createArray().put(firDepartItemId))
                    .put(staffInfo.getSec_depart().getField_id(), JSONUtil.createArray().put(sec_depart_code))
                    .put(staffInfo.getKeClass().getField_id(), JSONUtil.createArray().put(subSectionCode))
                    .put(staffInfo.getGroup().getField_id(), JSONUtil.createArray().put(groupCode))
                    .put(staffInfo.getTeam().getField_id(), JSONUtil.createArray().put(teamCode))
                    .put(staffInfo.getPost().getField_id(), JSONUtil.createArray().put(postItemId))
                    .put(staffInfo.getStaff_status().getField_id(), JSONUtil.createArray().put(status))
                    .put(staffInfo.getStaff_edu().getField_id(), element.elementText(""))
                    .put(staffInfo.getSepar_time().getField_id(), TERMINATION_DATE)
                    .put(staffInfo.getIs_leader().getField_id(), JSONUtil.createArray().put(isLeader));
            if (!"".equals(superiorItemId)) {
                dataJson.put(staffInfo.getSuperior().getField_id(), JSONUtil.createArray().put(superiorItemId));
            }
            if (!"".equals(memberUserId)){
                dataJson.put(staffInfo.getStaff_member().getField_id(),JSONUtil.createArray().put(memberUserId));
            }
            JSONObject paramJson = JSONUtil.createObj().put("item_ids", itemId).put("filter", JSONUtil.createObj().put("and",
                    JSONUtil.createArray().put(JSONUtil.createObj().put("field", staffInfo.getStaff_number().getField_id())
                            .put("query", JSONUtil.createObj().put("eq", element.elementText("STAFF_NO"))))))
                    .put("data", dataJson);
            resultJson.put("rspStatus", updateTable(HbTablesId.staff_info, paramJson));
        } catch (Exception e) {
            e.printStackTrace();
            XxlJobLogger.log(element.elementText("STAFF_NO") + e.getMessage());
        }
        return resultJson;
    }

    @Override
    public void saveItemsId(Map itemMap, String field_code) {

    }

    @Override
    public boolean deleteTable(Element element) {
        return false;
    }


    public JSONObject getLeaderNoHbaccount() {
        staffInfo = (Staff_Info) tableStuckCache.get(staffInfoTbStruc);
        JSONObject paramJson = JSONUtil.createObj();
        JSONArray andWhere = JSONUtil.createArray().put(JSONUtil.createObj().put("field", staffInfo.getIs_leader().getField_id())
                .put("query", JSONUtil.createObj().put("eq", JSONUtil.createArray().put(1))))
                .put(JSONUtil.createObj().put("field", staffInfo.getStaff_member().getField_id()).put("query", JSONUtil.createObj()
                        .put("em", true)));
        paramJson.put("where", JSONUtil.createObj().put("and", andWhere));
        JSONObject result = getJsonObject(paramJson, HbTablesId.staff_info,"find");
        return result;
    }

    /**
     * 创建人员信息表结构对照
     *
     * @param jsonObject
     * @param staffInfo
     */
    static void setFildsMap(JSONObject jsonObject, Staff_Info staffInfo) {
        for (Object objects : (JSONArray) jsonObject.get("fields")
                ) {
            JSONObject field_id = ((JSONObject) objects);
            KeyValueModel valueModel = new KeyValueModel();
            valueModel.setField_id(field_id.get("field_id").toString());
            switch (field_id.get("alias").toString()) {
                case "staff_info.staff_number":
                    staffInfo.setStaff_number(valueModel);
                    break;
                case "staff_info.staff_name":
                    staffInfo.setStaff_name(valueModel);
                    break;
                case "staff_info.staff_member":
                    staffInfo.setStaff_member(valueModel);
                    break;
                case "staff_info.staff_birth":
                    staffInfo.setStaff_birth(valueModel);
                    break;
                case "staff_info.staff_gender":
                    staffInfo.setStaff_gender(valueModel);
                    break;
                case "staff_info.company":
                    staffInfo.setCompany(valueModel);
                    break;
                case "staff_info.fir_depart":
                    staffInfo.setFir_depart(valueModel);
                    break;
                case "staff_info.sec_depart":
                    staffInfo.setSec_depart(valueModel);
                    break;
                case "staff_info.class":
                    staffInfo.setKeClass(valueModel);
                    break;
                case "staff_info.group":
                    staffInfo.setGroup(valueModel);
                    break;
                case "staff_info.team":
                    staffInfo.setTeam(valueModel);
                    break;
                case "staff_info.post":
                    staffInfo.setPost(valueModel);
                    break;
                case "staff_info.staff_status":
                    staffInfo.setStaff_status(valueModel);
                    break;
                case "staff_info.staff_edu":
                    staffInfo.setStaff_edu(valueModel);
                    break;
                case "staff_info.entry_time":
                    staffInfo.setEntry_time(valueModel);
                    break;
                case "staff_info.separ_time":
                    staffInfo.setSepar_time(valueModel);
                    break;
                case "staff_info.superior":
                    staffInfo.setSuperior(valueModel);
                    break;
                case "staff_info.is_leader":
                    staffInfo.setIs_leader(valueModel);
                    break;
                default:
                    break;
            }
        }
    }
}
