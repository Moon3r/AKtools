package com.kkzevip.utils;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.cvm.v20170312.CvmClient;
import com.tencentcloudapi.cvm.v20170312.models.DescribeInstancesRequest;
import com.tencentcloudapi.cvm.v20170312.models.DescribeInstancesResponse;
import com.tencentcloudapi.cvm.v20170312.models.Instance;
import com.tencentcloudapi.tat.v20201028.TatClient;
import com.tencentcloudapi.tat.v20201028.models.*;
import org.apache.commons.codec.binary.Base64;

import java.util.*;

public class TenOperator {


    private String SecretId = "";
    private String SecretKey = "";
    private Credential credential = null;
    public HashMap<String, String> map = new HashMap<>();

    public HashMap<String, String> getMap() {
        return map;
    }

    public TenOperator(String SecretId, String SecretKey) {
        setSecretId(SecretId);
        setSecretKey(SecretKey);
        initRegion();
        initCred();
    }

    public RunCommandResponse runCommand(String regionid, String insID, String comtype, String command) {
        TatClient tatClient = new TatClient(this.credential, regionid);

        String cname = UUID.randomUUID().toString();
        RunCommandRequest request = new RunCommandRequest();
        String[] inslist = new String[]{insID};
        try {
            request.setCommandType(comtype);
            request.setCommandName(cname);
            request.setInstanceIds(inslist);
            String commandb64 = Base64.encodeBase64String(command.getBytes());
            request.setContent(commandb64);
            return tatClient.RunCommand(request);
        } catch (TencentCloudSDKException e) {
            return null;
        }
    }

    public InvokeCommandResponse invokeCommand(String regionid, String insID, String commandID) {
        TatClient tatClient = new TatClient(this.credential, regionid);

        InvokeCommandRequest request = new InvokeCommandRequest();
        request.setCommandId(commandID);
        String[] inslist = new String[]{insID};
        request.setInstanceIds(inslist);
        try {
            return tatClient.InvokeCommand(request);
        } catch (TencentCloudSDKException e) {
            return null;
        }
    }

    public String createCommand(String regionid, String comtype, String command) {
        TatClient tatClient = new TatClient(this.credential, regionid);
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0; i<16; i++) {
            int num = random.nextInt(62);
            stringBuilder.append(str.charAt(num));
        }
        CreateCommandRequest request = new CreateCommandRequest();
        try {
            request.setCommandType(comtype);
            request.setCommandName(stringBuilder.toString());
            String commandb64 = Base64.encodeBase64String(command.getBytes());
            request.setContent(commandb64);
            CreateCommandResponse response = tatClient.CreateCommand(request);
            return response.getCommandId();
        } catch (TencentCloudSDKException e) {
            return null;
        }
    }

    public List<Instance> DescribeIns() {
        List<Instance> list = new ArrayList<>();
        for (String rname: map.keySet()) {
            DescribeInstancesResponse response = this.Connect(rname);
            try {
                Instance[] instances = response.getInstanceSet();
                for (Instance instance: instances) {
                    list.add(instance);
                }
            } catch (NullPointerException e) {
                return null;
            }

        }
        return list;
    }

    public DescribeInstancesResponse Connect(String regionid) {
        CvmClient cvmClient = new CvmClient(this.credential, regionid);

        DescribeInstancesRequest request = new DescribeInstancesRequest();
        try {
            return cvmClient.DescribeInstances(request);
        } catch (TencentCloudSDKException e) {
            return null;
        }
    }

    public void initCred() {
        Credential credential = new Credential(this.SecretId, this.SecretKey);
        this.credential = credential;
    }

    public void initRegion() {
        map.put("ap-bangkok", "亚太东南(曼谷)");
        map.put("ap-beijing", "华北地区(北京)");
        map.put("ap-chengdu", "西南地区(成都)");
        map.put("ap-chongqing", "西南地区(重庆)");
        map.put("ap-guangzhou", "华南地区(广州)");
        map.put("ap-hongkong", "港澳台地区(中国香港)");
        map.put("ap-jakarta", "亚太东南（雅加达）");
        map.put("ap-mumbai", "亚太南部(孟买)");
        map.put("ap-nanjing", "华东地区(南京)");
        map.put("ap-seoul", "亚太东北(首尔)");
        map.put("ap-shanghai", "华东地区(上海)");
        map.put("ap-shanghai-fsi", "华东地区(上海金融)");
        map.put("ap-shenzhen-fsi", "华南地区(深圳金融)");
        map.put("ap-singapore", "亚太东南(新加坡)");
        map.put("ap-tokyo", "亚太东北(东京)");
        map.put("eu-frankfurt", "欧洲地区(法兰克福)");
        map.put("eu-moscow", "欧洲地区(莫斯科)");
        map.put("na-ashburn", "美国东部(弗吉尼亚)");
        map.put("na-siliconvalley", "美国西部(硅谷)");
        map.put("na-toronto", "北美地区(多伦多)");
    }

    public void setSecretId(String secretId) {
        SecretId = secretId;
    }

    public void setSecretKey(String secretKey) {
        SecretKey = secretKey;
    }
}
