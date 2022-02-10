package com.kkzevip.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.ecs.model.v20140526.*;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.apache.commons.codec.binary.Base64;

import java.util.*;

public class AliOperator {

    private String accessKeyId = "";
    private String accessKeySecret = "";
    public HashMap<String, String> map = new HashMap<>();

    public HashMap<String, String> getMap() {
        return map;
    }

    public AliOperator(String accessKeyId, String accessKeySecret) {
        setAccessKeyId(accessKeyId);
        setAccessKeySecret(accessKeySecret);
        initRegion();
    }

    public RunCommandResponse runCommand(String regionID, String insID, String comtype, String command) {
        IClientProfile iClientProfile = DefaultProfile.getProfile(regionID, this.accessKeyId, this.accessKeySecret);
        IAcsClient iAcsClient = new DefaultAcsClient(iClientProfile);

        RunCommandRequest request = new RunCommandRequest();
        try {
            List<String> inslist = new ArrayList<>();
            inslist.add(insID);
            request.setInstanceIds(inslist);
            request.setType(comtype);
            String cname = UUID.randomUUID().toString();
            request.setName(cname);
            String commandb64 = Base64.encodeBase64String(command.getBytes());
            request.setCommandContent(commandb64);
            return iAcsClient.getAcsResponse(request);
        } catch (ClientException e) {
            return null;
        }
    }

    public InvokeCommandResponse invokeCommand(String regionID, String insID, String commandID) {
        IClientProfile iClientProfile = DefaultProfile.getProfile(regionID, this.accessKeyId, this.accessKeySecret);
        IAcsClient iAcsClient = new DefaultAcsClient(iClientProfile);

        InvokeCommandRequest request = new InvokeCommandRequest();
        request.setCommandId(commandID);
        List<String> inslist = new ArrayList<>();
        inslist.add(insID);
        request.setInstanceIds(inslist);
        try {
            return iAcsClient.getAcsResponse(request);
        } catch (ClientException e) {
            return null;
        }
    }

    public String createCommand(String regionID, String comtype, String command) {
        IClientProfile iClientProfile = DefaultProfile.getProfile(regionID, this.accessKeyId, this.accessKeySecret);
        IAcsClient iAcsClient = new DefaultAcsClient(iClientProfile);
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0; i<16; i++) {
            int num = random.nextInt(62);
            stringBuilder.append(str.charAt(num));
        }
        CreateCommandRequest request = new CreateCommandRequest();
        try {
            request.setType(comtype);
            request.setName(stringBuilder.toString());
            String commandb64 = Base64.encodeBase64String(command.getBytes());
            request.setCommandContent(commandb64);
            CreateCommandResponse response = iAcsClient.getAcsResponse(request);
            return response.getCommandId();
        } catch (ClientException e) {
            return null;
        }
    }

    public List<DescribeInstancesResponse.Instance> DescribeIns() {
        List<DescribeInstancesResponse.Instance> list = new ArrayList<>();
        try {
            for (Object x: this.map.keySet()) {
                DescribeInstancesResponse response = this.Connect((String) x);
                list.addAll(response.getInstances());
            }
        } catch (NullPointerException e) {
            return null;
        }
        return list;
    }

    public DescribeInstancesResponse Connect(String regionid) {
        IClientProfile iClientProfile = DefaultProfile.getProfile(regionid, this.accessKeyId, this.accessKeySecret);
        IAcsClient iAcsClient = new DefaultAcsClient(iClientProfile);

        // 创建API请求并设置参数
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        try {
            return iAcsClient.getAcsResponse(request);
        } catch (ClientException clientException) {
            return null;
        }
    }

    public void initRegion() {
        map.put("me-east-1", "阿联酋（迪拜）");
        map.put("us-east-1", "美国（弗吉尼亚）");
        map.put("ap-northeast-1", "日本（东京）");
        map.put("cn-hongkong", "香港");
        map.put("cn-north-2-gov-1", "华北 2 阿里政务云1");
        map.put("cn-qingdao", "华北1（青岛）");
        map.put("cn-shanghai-finance-1", "上海金融云");
        map.put("cn-shanghai", "华东2（上海）");
        map.put("us-west-1", "美国（硅谷）");
        map.put("cn-hangzhou", "华东1（杭州）");
        map.put("ap-southeast-5", "印度尼西亚（雅加达）");
        map.put("cn-shenzhen", "华南1（深圳）");
        map.put("ap-southeast-3", "马来西亚（吉隆坡）");
        map.put("ap-southeast-2", "澳大利亚（悉尼）");
        map.put("ap-south-1", "印度（孟买）");
        map.put("eu-west-1", "英国（伦敦）");
        map.put("cn-chengdu", "成都");
        map.put("cn-huhehaote", "华北5（呼和浩特）");
        map.put("cn-beijing", "华北2（北京）");
        map.put("cn-heyuan", "华南2（河源）");
        map.put("cn-shenzhen-finance-1", "深圳金融云");
        map.put("ap-southeast-1", "新加坡");
        map.put("eu-central-1", "德国（法兰克福）");
        map.put("cn-zhangjiakou", "华北3（张家口）");
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }
}
