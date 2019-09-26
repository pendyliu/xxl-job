package com.xxl.job.executor.serviceHuoban;

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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.xxl.job.executor.service.jobhandler.PersonHbJobHandler.tableStuckCache;

public class StaffInfoImpl extends BaseHuoBanServ implements IHuoBanService {
    public static String staffInfoTbStruc = "staff_info";
    public static String staffInfoItemsId="staffInfoItemsId";
    Staff_Info staffInfo = new Staff_Info();


    @Override
    public void createFieldsIdMap() {
        if (tableStuckCache.get(staffInfoTbStruc) == null) {
            JSONObject jsonObject = getTables(HbTablesId.team_name);
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
    public Map getItemId(JSONObject paramJson, Element element) {
        return null;
    }

    @Override
    public String getCacheItemId(Element element) {
        return null;
    }

    @Override
    public List readStringXml(String xml) {
        try {
            Document doc = null;
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
                    // 拿到STAFF下的子节点ROW下的字节点组织节点的值
                    String staffInfoItemId = getCacheItemsId(JSONUtil.createObj().put("tableId", HbTablesId.staff_info)
                            .put("field_id", ((Staff_Info) tableStuckCache.get(staffInfoTbStruc)).getStaff_number().getField_id())
                            .put("field_value", itemEle.elementText("STAFF_NO")), this, itemEle);

                }
            }

        } catch (DocumentException e) {
            XxlJobLogger.log(e.getMessage());
        } catch (Exception e) {
            XxlJobLogger.log(e.getMessage());
        } finally {

        }
        return null;
    }

    @Override
    public JSONObject insertTable(Element element) {
        String companyItemId=element.elementText("BRANCH");
        String firDepartCode=getOrgNodeName(element,"DEPARTMENT");
        String sec_depart_code=getOrgNodeName(element,"SECTION");
        String subSectionCode=getOrgNodeName(element,"SUB_SECTION");
        String groupCode=getOrgNodeName(element,"CITY");
        String teamCode=getOrgNodeName(element,"RANK");
        JSONObject staffInfoJson=JSONUtil.createObj().put("fields", JSONUtil.createObj().
                put(staffInfo.getStaff_number().getField_id(),element.elementText("STAFF_NO"))
                .put(staffInfo.getStaff_name().getField_id(),element.elementText("STAFF_NAME"))
                .put(staffInfo.getStaff_birth().getField_id(),element.elementText("DATE_OF_BIRTH"))
                .put(staffInfo.getStaff_gender().getField_id(),element.elementText("SEX"))
                .put(staffInfo.getCompany().getField_id(), JSONUtil.createArray().put(((Map) ((Map) (tableStuckCache.get(CompanyImpl.companyItemsId))).get(companyItemId)).get("itemId")))
                .put(staffInfo.getStaff_name().getField_id(), JSONUtil.createArray().put(((Map) ((Map) (tableStuckCache.get(FirDepartMentImpl.firDpartItemsId))).get(firDepartCode)).get("itemId")))
                .put(staffInfo.getSec_depart().getField_id(), JSONUtil.createArray().put(((Map) ((Map) (tableStuckCache.get(Sec_DepartImpl.secDepartItemsId))).get(sec_depart_code)).get("itemId")))
                .put(staffInfo.getKeClass().getField_id(),JSONUtil.createArray().put(((Map) ((Map) (tableStuckCache.get(KeClassImpl.keClassItemsId))).get(subSectionCode)).get("itemId")))
                .put(staffInfo.getGroup().getField_id(),JSONUtil.createArray().put(((Map) ((Map) (tableStuckCache.get(GroupImpl.groupItemsId))).get(groupCode)).get("itemId")))
                .put(staffInfo.getTeam().getField_id(),JSONUtil.createArray().put(((Map) ((Map) (tableStuckCache.get(TeamNameImpl.rankItemsId))).get(teamCode)).get("itemId")))
                .put(staffInfo.getPost().getField_id(),element.elementText("POSITION"))
                .put(staffInfo.getStaff_status().getField_id(), element.elementText("STATUS"))
                .put(staffInfo.getStaff_edu().getField_id(),element.elementText(""))
                .put(staffInfo.getSepar_time().getField_id(),element.elementText("TERMINATION_DATE"))
                .put(staffInfo.getSuperior().getField_id(),element.elementText("SUPERVISOR_NAME"))
        );

          return null;
    }

    @Override
    public JSONObject updateTable(JSONObject jsonObject, Element element) {
        return null;
    }

    @Override
    public void saveItemsId(Map itemMap, String field_code) {

    }

    /**
     * 创建人员信息表结构对照
     * @param jsonObject
     * @param staffInfo
     */
    static void setFildsMap(JSONObject jsonObject, Staff_Info staffInfo) {
        for (int i = 0; i < ((JSONArray) jsonObject.get("field_layout")).size(); i++) {
            for (Object objects : (JSONArray) jsonObject.get("fields")
                    ) {
                JSONArray field_layout = ((JSONArray) jsonObject.get("field_layout"));
                JSONObject field_id = ((JSONObject) objects);
                if (field_id.get("field_id").equals(field_layout.get(i))) {
                    KeyValueModel valueModel = new KeyValueModel();
                    valueModel.setField_id(field_id.get("field_id").toString());
                    switch (field_id.get("name").toString()) {
                        case "工号":
                            staffInfo.setStaff_number(valueModel);
                            break;
                        case "姓名":
                            staffInfo.setStaff_name(valueModel);
                            break;
                        case "成员":
                            staffInfo.setStaff_member(valueModel);
                            break;
                        case "出生日期":
                            staffInfo.setStaff_birth(valueModel);
                            break;
                        case "性别":
                            staffInfo.setStaff_gender(valueModel);
                            break;
                        case "公司":
                            staffInfo.setCompany(valueModel);
                            break;
                        case "一级部门":
                            staffInfo.setFir_depart(valueModel);
                            break;
                        case "二级部门":
                            staffInfo.setSec_depart(valueModel);
                            break;
                        case "课":
                            staffInfo.setKeClass(valueModel);
                            break;
                        case "班":
                            staffInfo.setGroup(valueModel);
                            break;
                        case "班组":
                            staffInfo.setTeam(valueModel);
                            break;
                        case "岗位":
                            staffInfo.setPost(valueModel);
                            break;
                        case "当前状态":
                            staffInfo.setStaff_status(valueModel);
                            break;
                        case "学历":
                            staffInfo.setStaff_edu(valueModel);
                            break;
                        case "入职时间":
                            staffInfo.setEntry_time(valueModel);
                            break;
                        case "离职时间":
                            staffInfo.setSepar_time(valueModel);
                            break;
                        case "上长":
                            staffInfo.setSuperior(valueModel);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }
}
