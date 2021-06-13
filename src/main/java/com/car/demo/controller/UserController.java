package com.car.demo.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.car.demo.Util.BaiduUtil.Base64Util;
import com.car.demo.dao.entity.Forum;
import com.car.demo.dao.entity.Template;
import com.car.demo.dao.entity.TemplateParam;
import com.car.demo.dao.entity.User;
import com.car.demo.mq.HttpRequest;
import com.car.demo.service.UserService;
import com.car.demo.Util.BaiduUtil.AuthService;
import com.car.demo.Util.BaiduUtil.FileUtil;
import com.car.demo.Util.BaiduUtil.HttpUtil;
import com.car.demo.Util.CommonUtil;
import com.zhenzi.sms.ZhenziSmsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserService userService;
    @RequestMapping(value = "/test")
    public String test(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        return "connect is ok";
    }
    public static JSONObject getToken(){
        JSONObject object = new JSONObject();
        object.put("grant_type","client_credential");
        object.put("appid","wxc53bb2124ced8b83");
        object.put("secret","038060f8928874d1aed5fbda3477af78");
        JSONObject jsonObject = null;
        jsonObject = CommonUtil.httpsRequestJson("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wxc53bb2124ced8b83&secret=038060f8928874d1aed5fbda3477af78", "GET", object.toJSONString());
        return    jsonObject;
    }
    public void uniformSend(String openId,String plate){
        Template template=new Template();
        template.setTemplate_id("MZaUOLCP_vRHvNRc4ZoMS3BU0mOpuUL0RpkehI-I5Qo");
        template.setTouser(openId);
        template.setPage("pages/index/index");
        List<TemplateParam> paras=new ArrayList<TemplateParam>();
        paras.add(new TemplateParam("thing1",plate));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        paras.add(new TemplateParam("time2",sdf.format(new Date())));
        paras.add(new TemplateParam("phrase4","提醒你移车"));
        template.setTemplateParamList(paras);
        JSONObject object= JSON.parseObject(getToken().toString());
        String Access_Token = object.getString("access_token");
        String requestUrl="https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token="+Access_Token;
        CommonUtil.httpsRequestJson(requestUrl, "POST", template.toJSON());
    }
    public static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "abdefghijmnpqrstyABCDEFGHIJKLMNPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
    public void uniformSend2(String set,String openId){
        Template template=new Template();
        template.setTemplate_id("MZaUOLCP_vRHvNRc4ZoMS3BU0mOpuUL0RpkehI-I5Qo");
        template.setTouser(openId);
        template.setPage("pages/index/index");
        List<TemplateParam> paras=new ArrayList<TemplateParam>();
        String y=getRandomString(4);
        redisTemplate.opsForValue().set(set,y,60, TimeUnit.SECONDS);
        paras.add(new TemplateParam("thing1",y));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        paras.add(new TemplateParam("time2",sdf.format(new Date())));
        paras.add(new TemplateParam("phrase4","绑定验证"));
        template.setTemplateParamList(paras);
        JSONObject object= JSON.parseObject(getToken().toString());
        String Access_Token = object.getString("access_token");
        String requestUrl="https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token="+Access_Token;
        CommonUtil.httpsRequestJson(requestUrl, "POST", template.toJSON());
    }
    @RequestMapping(value = "/sendM")
    public String sendM(String openId,String plateNumber,HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        List<String>strings=userService.getBindingM(openId);
        for (String s:strings){
            uniformSend(s,plateNumber);
        }
        uniformSend(openId,plateNumber);
        return "ok";
    }
    @RequestMapping(value = "/sendbinding")
    public String sendbinding(String openId,String openIdOther,HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        uniformSend2(openId,openIdOther);
        return "ok";
    }
    @RequestMapping(value = "/getbinding")
    public String getbinding(String openId,HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        List<String> openIds=userService.getbinding(openId);
        List<String> list=new ArrayList<>();
        for (String s:openIds){
            list.addAll(userService.getPlate(s));
        }
        JSONObject result = new JSONObject();
        result.put("plateList",list);
        return result.toString();
    }
    @RequestMapping(value = "/deletebinding")
    public String deletebinding(String openId,String openIdOther,HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        userService.deletebinding(openId,openIdOther);
        return "ok";
    }
    @RequestMapping(value = "/setbinding")
    public String setbinding(String openId,String openIdOther,String vcode,HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        String v= String.valueOf(redisTemplate.opsForValue().get(openId));
        String openIdBefore=null;
        List<String> openIds=userService.getbinding(openId);
        if (openIds.size()>=2){
            return "out";
        }
        openIdBefore=userService.ybinding(openId,openIdOther);
        if (!(openIdBefore==null)){
            return "no";
        }
        if (v.isEmpty()){
            return "no";
        }
        if (vcode.equals(v)){
            userService.setbinding(openId,openIdOther);
        }else {
            return "no";
        }
        return "ok";
    }
    @RequestMapping(value = "/getOpenid")
    public String getOpenid(String code,HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        if (code == null || code.length() == 0) {
            return "code 不能为空";
        }
        //小程序的appid
        String appId = "wxc53bb2124ced8b83";
        // 小程序的secret
        String appsecret = "038060f8928874d1aed5fbda3477af78";

        //向微信服务器 使用登录凭证 code 获取 session_key 和 openid
        // 请求参数
        String params = "appid=" + appId + "&secret=" + appsecret + "&js_code=" + code + "&grant_type=authorization_code";

        // 发送请求
        String sr = HttpRequest.sendGet("https://api.weixin.qq.com/sns/jscode2session", params);

        JSONObject jsonObject = JSONObject.parseObject(sr);
        JSONObject result1 = new JSONObject();
        result1.put("openid",jsonObject.get("openid"));
        result1.put("unionid",jsonObject.get("unionid"));
        result1.put("session_key",jsonObject.get("session_key"));
        return result1.toString();
    }
    @RequestMapping("/sendmessage")
    public String sendmessage(String phoneNumber,HttpServletRequest request, HttpServletResponse response){
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        String is="ok";
        try {
            JSONObject json = null;
            //生成6位验证码
            String verifyCode = String.valueOf(new Random().nextInt(899999) + 100000);
            //发送短信
            ZhenziSmsClient client = new ZhenziSmsClient("https://sms_developer.zhenzikj.com","106387", "N2U4Yjg2ZWQtMjg2OS00ZTY5LWE1ZGUtNDBmM2Q2MTRjM2Iz");
            Map<String, Object> params = new HashMap<String, Object>();
            String[] i={verifyCode};
            params.put("templateParams",i);
            params.put("number", phoneNumber);
            params.put("templateId","1119");
            String result = client.send(params);
            json = JSONObject.parseObject(result);
            if(json.getIntValue("code") != 0){//发送短信失败
                is="no";
            }
            redisTemplate.opsForValue().set(phoneNumber+"ce",verifyCode,60, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return is;
    }
    @RequestMapping(value = "/signIn")
    public String signIn(String openId,HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        System.out.println(openId);
        User i= userService.getUser(openId);
        if(i!=null){
            return "ok";
        }else {
        userService.signIn(openId);
        }
        return "ok";
    }
    @RequestMapping(value = "/bindingPlate")
    public String bindingPlate(String openId,String plateNumber,HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        System.out.println(openId+plateNumber);
        List<String> i=userService.verifyPlate(plateNumber);
        List<String> nu=userService.getPlate(openId);
        if(!i.isEmpty()){
            return "no";
        }
        if(nu.size()>=4){
            return "out";
        }
        userService.bindingPlate(openId,plateNumber);
        return "ok";
    }
    @RequestMapping(value = "/deletePlate")
    public String deletePlate(String openId,String plateNumber,HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        List<String> nu=userService.getPlate(openId);
        String str=null;
        for (String s:nu){
            if (s.equals(plateNumber)){
                str=s;
                userService.deletePlate(openId,plateNumber);
            }
        }
        if (str==null){
            return "no";
        }
        return "ok";
    }
    @RequestMapping(value = "/verifyPlate")
    public String verifyPlate(String plateNumber,HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        List<String> i=userService.verifyPlate(plateNumber);
        if(!i.isEmpty()){
            return "no";
        }
        return "ok";
    }

    @RequestMapping(value = "/remindMovingCar")
    public String remindMovingCar(String plateNumber,String openId,HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        String i=null;
        try {
            i= String.valueOf(redisTemplate.opsForValue().get(openId));
        }catch (Exception e){}
        i+=plateNumber+"\n";
        redisTemplate.opsForValue().set(openId,i,600, TimeUnit.SECONDS);
        return "ok";
    }
    @RequestMapping(value = "/setIndex")
    public String setIndex(String phoneNumber,String openId,String sex,String birthday ,String contactInformation,String abstractPersion,HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        userService.setIndex(openId,sex,phoneNumber,birthday,contactInformation,abstractPersion);
        return "ok";
    }
    @RequestMapping(value = "/getIndex")
    public String getIndex(String openId,HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        List<String> plateNumber=userService.getPlate(openId);
        User user=userService.getUser(openId);
        JSONObject result = new JSONObject();
        result.put("plateNumber",plateNumber);
        result.put("User",user);
        String i=null;
        try {
            i= String.valueOf(redisTemplate.opsForValue().get(openId));
        }catch (Exception e){}
        result.put("remindMessage",i);
        return result.toString();
    }
    @CrossOrigin
    @RequestMapping(value = "showEInvoice")
    public String showEInvoice(String fileName,HttpServletRequest request,HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Credentials", "true");
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        FileInputStream fis = null;
        OutputStream os = null;
        String filepath = "C:\\upload\\"+fileName;     //path是你服务器上图片的绝对路径
        File file = new File(filepath);
        if(file.exists()){
            try {
                fis = new FileInputStream(file);
                long size = file.length();
                byte[] temp = new byte[(int) size];
                fis.read(temp, 0, (int) size);
                fis.close();
                byte[] data = temp;
                response.setContentType("image/jpg");
                os = response.getOutputStream();
                os.write(data);
                os.flush();
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        JSONObject result = new JSONObject();
        result.put("state","ok");
        return result.toString();
    }
    @RequestMapping(value = "creatForum")
    public String creatForum(String title,String category,String content,String openId,String fileName, HttpServletResponse response, HttpServletRequest request){
        response.setHeader("Access-Control-Allow-Credentials", "true");
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        JSONObject result = new JSONObject();
        String forumId= String.valueOf(UUID.randomUUID());
        userService.creatForum(forumId,title,category,content,fileName,openId);
        return result.toString();
    }
    @RequestMapping(value = "getRandomForum")
    public String getRandomForum( HttpServletResponse response, HttpServletRequest request){
        response.setHeader("Access-Control-Allow-Credentials", "true");
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        JSONObject result = new JSONObject();
        List<Forum>forums=userService.getRandomForum();
        result.put("forumList",forums);
        return result.toString();
    }
    @RequestMapping(value = "getMyForum")
    public String getMyForum(String openId, HttpServletResponse response, HttpServletRequest request){
        response.setHeader("Access-Control-Allow-Credentials", "true");
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        JSONObject result = new JSONObject();
        List<Forum>forums=userService.getMyForum(openId);
        result.put("forumList",forums);
        return result.toString();
    }
    @RequestMapping(value = "setLocation")
    public String setLocation(String E,String N, HttpServletResponse response, HttpServletRequest request){
        response.setHeader("Access-Control-Allow-Credentials", "true");
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        System.out.println(N+E);//这一行连接交换机，物理设备不满足，不启用
        return "ok";
    }
    @RequestMapping(value = "getMaster")
    public String getMaster(String plateNumber, HttpServletResponse response, HttpServletRequest request){
        response.setHeader("Access-Control-Allow-Credentials", "true");
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        List<String> i=userService.verifyPlate(plateNumber);
        JSONObject result = new JSONObject();
        for (String userOpenId:i) {
            User user = userService.getUser(userOpenId);
            result.put("user", user);
        }
        return result.toString();
    }
    @RequestMapping(value = "deleteForum")
    public String deleteForum(String forumId, HttpServletResponse response, HttpServletRequest request){
        response.setHeader("Access-Control-Allow-Credentials", "true");
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        userService.deleteForum(forumId);
        JSONObject result = new JSONObject();
        result.put("state","ok");
        return result.toString();
    }
    @RequestMapping(value = "getForum")
    public String getForum(String forumId, HttpServletResponse response, HttpServletRequest request){
        response.setHeader("Access-Control-Allow-Credentials", "true");
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        Forum forum=userService.getForum(forumId);
        JSONObject result = new JSONObject();
        result.put("forum",forum);
        return result.toString();
    }
    @RequestMapping(value = "setFileName")
    public String setFileName(String forumId,String fileName, HttpServletResponse response, HttpServletRequest request){
        response.setHeader("Access-Control-Allow-Credentials", "true");
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        redisTemplate.opsForValue().set(forumId,fileName,30, TimeUnit.SECONDS);
        return "ok";
    }
    @RequestMapping(value = "getPlateByPicture")
    public String getPlateByPicture(String fileName, HttpServletResponse response, HttpServletRequest request){
        response.setHeader("Access-Control-Allow-Credentials", "true");
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        // 请求url
        String result="failed";
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/license_plate";
        try {
            // 本地文件路径
            String filePath = "C:\\upload\\"+fileName;//[本地文件路径]
            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam;

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = AuthService.getAuth();//[调用鉴权接口获取的token]
            result = HttpUtil.post(url, accessToken, param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    @CrossOrigin
    @RequestMapping(value = "doneLoad")
    public String doneLoad(MultipartFile file, HttpServletResponse response, HttpServletRequest request){
        response.setHeader("Access-Control-Allow-Credentials", "true");
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin",origin);
        String result = null;
        if (file.isEmpty()){
            return "文件为空";
        }
        String fileName=null;
        String name=file.getOriginalFilename();
        System.out.println(name);
        String[] a = name.split("\\.");
        System.out.println(a[0]);
        String type=a[a.length-1];
        System.out.println(type+file.getSize());
        if (type.equals("png")||type.equals("jpg")){
            fileName = String.valueOf(UUID.randomUUID())+"."+type;
            String path="C:\\upload";
            System.out.println(file.getSize());
            InputStream inputStream = null;
            File files = null;
            try {
                files = File.createTempFile("temp", null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                file.transferTo(files);   //sourceFile为传入的MultipartFile
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream = new FileInputStream(files);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            files.deleteOnExit();
            OutputStream os = null;
            try {
                // 2、保存到临时文件
                // 1K的数据缓冲
                byte[] bs = new byte[1024];
                // 读取到的数据长度
                int len=1024;
                // 输出的文件流保存到本地文件
                File tempFile = new File(path);
                if (!tempFile.exists()) {
                    tempFile.mkdirs();
                }
                os = new FileOutputStream(tempFile.getPath() + File.separator + fileName);
                // 开始读取
                while (true) {
                    len = inputStream.read(bs) ;
                    if (len==-1){
                        break;
                    }
                    os.write(bs, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 完毕，关闭所有链接
                try {
                    os.close();
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            result=fileName;
        }
        return result;
    }
}
