package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Suncloud on 2016/1/21.
 */
public class WorkParameter implements Identifiable {

    private long id;
    private String title;
    private ArrayList<WorkParameter> children;
    private String fieldName;
    private String values;

    public WorkParameter(JSONObject jsonObject){
        if(jsonObject!=null){
            id=jsonObject.optLong("id");
            if(id==0){
                id=jsonObject.optLong("set_meal_info_id");
            }
            title= JSONUtil.getString(jsonObject,"title");
            fieldName= JSONUtil.getString(jsonObject,"field_name");
            JSONArray array=jsonObject.optJSONArray("children");
            if(array!=null&&array.length()>0){
                children=new ArrayList<>();
                for (int i=0,size=array.length();i<size;i++){
                    WorkParameter childParameter=new WorkParameter(array.optJSONObject(i));
                    if(!JSONUtil.isEmpty(childParameter.getValues())) {
                        children.add(childParameter);
                    }
                }
            }
            array=jsonObject.optJSONArray("field_value");
            if(array!=null&&array.length()>0){
                StringBuilder builder=new StringBuilder();
                for (int i=0,size=array.length();i<size;i++){
                    String value=array.optString(i);
                    if(!JSONUtil.isEmpty(value)){
                        if(builder.length()>0) {
                            builder.append("\n");
                        }
                        builder.append(value.trim());
                    }
                }
                values=builder.toString();
            }

        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getValues() {
        return values;
    }

    public ArrayList<WorkParameter> getChildren() {
        return children;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getTitle() {
        return title;
    }


    public void setChildren(ArrayList<WorkParameter> children) {
        this.children = children;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public WorkParameter parentClone(ArrayList<String> keys){
        return this;
//        WorkParameter workParameter=new WorkParameter(new JSONObject());
//        workParameter.setTitle(title);
//        if(children!=null&&!children.isEmpty()&&keys!=null&&!keys.isEmpty()) {
//            ArrayList<WorkParameter> parameters = new ArrayList<>();
//            for(WorkParameter parameter:children){
//                if(!JSONUtil.isEmpty(parameter.getFieldName())&&keys.contains(parameter.getFieldName())){
//                    parameters.add(parameter);
//                }
//            }
//            workParameter.setChildren(parameters);
//        }else{
//            workParameter.setChildren(children);
//        }
//        return workParameter;
    }
}
