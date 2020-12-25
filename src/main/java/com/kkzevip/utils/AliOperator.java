package com.kkzevip.utils;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.ecs.model.v20140526.DescribeInstancesRequest;
import com.aliyuncs.ecs.model.v20140526.DescribeInstancesResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

import java.util.HashMap;
import java.util.Map;

public class AliOperator {

    private String accessKeyId = "";
    private String accessKeySecret = "";
    public Map<String, String> map = new HashMap();

    public AliOperator(String accessKeyId, String accessKeySecret) {
        setAccessKeyId(accessKeyId);
        setAccessKeySecret(accessKeySecret);
        initRegion();
    }

    public DescribeInstancesResponse testConnect() throws  ClientException {
        IClientProfile iClientProfile = DefaultProfile.getProfile("cn-beijing", this.accessKeyId, this.accessKeySecret);
        IAcsClient iAcsClient = new DefaultAcsClient(iClientProfile);

        DescribeInstancesRequest request = new DescribeInstancesRequest();
        try {
            DescribeInstancesResponse response = iAcsClient.getAcsResponse(request);
            return response;
        } catch (ClientException clientException) {
            clientException.printStackTrace();
            return null;
        }
    }

    public void Connect() throws ClientException {
        for (String x: map.keySet()) {
            IClientProfile iClientProfile = DefaultProfile.getProfile(x, this.accessKeyId, this.accessKeySecret);
            IAcsClient iAcsClient = new DefaultAcsClient(iClientProfile);

            // 创建API请求并设置参数
            DescribeInstancesRequest request = new DescribeInstancesRequest();
            try {
                DescribeInstancesResponse response = iAcsClient.getAcsResponse(request);
                System.out.println(JSON.toJSONString(response));
            } catch (ClientException clientException) {
                clientException.printStackTrace();
            }
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

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }
}
