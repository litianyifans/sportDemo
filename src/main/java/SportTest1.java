import com.alibaba.fastjson.JSONObject;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;


import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SportTest1 {

    public static CookieStore cookieStores = null;

    public static String codeUrl = "https://www.materialtools.com/SMSContent/17";

    public static void main(String[] args) throws Exception {
        //getMs();
        //getItemList();
        //getdateListByItemId("32");
        //getTimePeriodByIdAndDay("32","2020/01/02") ;
        getRelayPeriodByIdAndDay("32","2020/01/02") ;
        //testMain();
    }

    public static void testMain() throws Exception {
        String mobileNo = "17109324212";
        String idCard = "530121197006145263";
        String name = "孟丹红";
        String groupName = "保龄球";
        String actDate = "2020-01-02";
        String startStr = "18:00";
        String endStr = "22:00";
        String startTime = URLtoUTF8.toUtf8String(startStr);
        String endTime = URLtoUTF8.toUtf8String(endStr);
        String groundId = "100476";
        String itemId = "32";
        sendRequestGet(null, null, null);
        sendRequestGet("http://www.zhtyzx.cn/zhh_sports/index.jsp", null, null);
        String verifyCode = getVerifyCode();
        List<Header> headerList = addPostHeader();
        List<NameValuePair> list2 = new ArrayList<NameValuePair>();
        list2.add(new BasicNameValuePair("mobile", mobileNo));
        list2.add(new BasicNameValuePair("actDate", actDate));
        list2.add(new BasicNameValuePair("itemId", itemId));
        list2.add(new BasicNameValuePair("groundId", groundId));
        list2.add(new BasicNameValuePair("startTime", startStr));
        list2.add(new BasicNameValuePair("endTime", endStr));
        list2.add(new BasicNameValuePair("identityCode", idCard));
        list2.add(new BasicNameValuePair("type", "0"));
        list2.add(new BasicNameValuePair("verifyCode", verifyCode));
        String sendResult = sendMobileVerifyCode(headerList,list2) ;
        if (sendResult.indexOf("验证码已发送") >= 0) {
            String messageCode = getMessage();
            List ticks = new ArrayList();
            String str = "ticketDetial.groundId=" + groundId +
                    "&ticketDetial.groundName=" + URLtoUTF8.toUtf8String(groupName) +
                    "&ticketDetial.actDate=" + actDate +
                    "&ticketDetial.startTime=" + startTime +
                    "&ticketDetial.endTime=" + endTime +
                    "&orderTicket.itemId=" + itemId +
                    "&orderTicket.itemName=" + URLtoUTF8.toUtf8String(groupName) +
                    "&orderTicket.username=" + URLtoUTF8.toUtf8String(name) +
                    "&orderTicket.identityCode=" + idCard +
                    "&orderTicket.verifyCode=" + verifyCode +
                    "&rand=&orderTicket.mobile=" + mobileNo +
                    "&orderTicket.mobileCheckCode=" + messageCode.trim();
            String sResult = sendRequestPost("http://www.zhtyzx.cn/zhh_sports/orderTicket-saveOrderTicket.action", headerList, ticks, str);
            //if("1" != messageSwitch){
            //					$("#voucherCode").html("<p></p>手机凭证号为："+orderTicket.voucherCode+"，已发送至"+orderTicket.mobile+"手机号码中，请注意查收！");
            //				}else{
            //					$("#voucherCode").html("<p></p>凭证号为："+orderTicket.voucherCode+"！");
            //				}
            System.out.println(getLoginCode());
            System.out.println("end");
        }

    }

    /*
      获取活动项目
   */
    public static List<Map<String,String>> getItemList() throws IOException, URISyntaxException, ClassNotFoundException {
        //sendRequestGet(null, null, null);
        //sendRequestGet("http://www.zhtyzx.cn/zhh_sports/index.jsp", null, null);
        List<Header> headerList = addPostHeader();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        String sResult = sendRequestPost("http://www.zhtyzx.cn/zhh_sports/item-listItem.action",headerList,params,null);
        Map stringToMap =  JSONObject.parseObject(sResult);
        List<JSONObject> objs = (List<JSONObject>)  ((Map)stringToMap.get("__result__")).get("data");
        List results = new ArrayList() ;
        for (JSONObject obj : objs){
                Map<String,String>  map = new HashMap<String, String>() ;
                map.put("id",obj.getString("id")) ;
                map.put("itemName",obj.getString("itemName")) ;
                results.add(map) ;
        }
        return  results ;
    }
    /*
       获取订票日期
    */
    public static HashMap<String,List<JSONObject>> getdateListByItemId(String id) throws IOException, URISyntaxException, ClassNotFoundException {
        List<Header> headerList = addPostHeader();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("itemid", id)) ;
        String sResult = sendRequestPost("http://www.zhtyzx.cn/zhh_sports/commonConfig-dateListByItemId.action",headerList,params,null);
        Map stringToMap =  JSONObject.parseObject(sResult);
        List<JSONObject> objs = (List<JSONObject>)  ((Map)stringToMap.get("__result__")).get("data");
        HashMap<String,List<JSONObject>> result = new HashMap<String, List<JSONObject>>() ;
        result.put("dates",(List<JSONObject>) objs.get(0)) ;
        result.put("xinqi",(List<JSONObject>) objs.get(1)) ;
        result.put("flag",(List<JSONObject>) objs.get(2)) ;
        return result ;
    }

    /*
      获取余票信息
   */
    public static HashMap<String,Object> getTimePeriodByIdAndDay(String id,String day) throws IOException, URISyntaxException, ClassNotFoundException {
        List<Header> headerList = addPostHeader();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("itemid", id)) ;
        params.add(new BasicNameValuePair("itemday", day)) ;
        String sResult = sendRequestPost("http://www.zhtyzx.cn/zhh_sports/commonConfig-timePeriod.action",headerList,params,null);
        Map stringToMap =  JSONObject.parseObject(sResult);
        JSONObject objs = (JSONObject) ((Map)stringToMap.get("__result__")).get("data");
        HashMap<String,Object> result = new HashMap<String, Object>() ;
        result.put("ts",objs.getString("ts")) ;
        result.put("timeList",objs.getString("timeList")) ;
        result.put("siteTimeList",objs.getString("siteTimeList")) ;
        result.put("siteList",objs.getString("siteList")) ;
        System.out.println(sResult);
        return result ;
    }
    /*
        查看剩余时间
     */
    public static Long getRelayPeriodByIdAndDay(String id,String day) throws IOException, URISyntaxException, ClassNotFoundException {
        HashMap<String, Object> result = getTimePeriodByIdAndDay(id,day) ;
        long time = Long.valueOf((String) result.get("ts")) ;
        return time ;
    }
    /*
       获取图片校验码
    */
    public static String getVerifyCode() throws IOException, URISyntaxException, ClassNotFoundException {
        List<Header> headerList = addPostHeader();
        //请求参数
        List<NameValuePair> loginNV = new ArrayList<NameValuePair>();
        Long date = new Date().getTime();
        loginNV.add(new BasicNameValuePair("r", "" + date));
        String url = "http://www.zhtyzx.cn/zhh_sports/orderTicket-getRand.action?r=" + date;
        String result = sendRequestPost(url, headerList, loginNV, null);
        String imgCode = result.substring(13, 17);
        url = "http://www.zhtyzx.cn/zhh_sports/view/item/image.jsp?strRand=" + imgCode;
        sendRequestGet(url, null, null);
        return imgCode;
    }


    /*
     发送手机校验码
    */
    public static String sendMobileVerifyCode(  List<Header> headerList , List<NameValuePair> params) throws IOException, URISyntaxException, ClassNotFoundException {
        String mobileCode = sendRequestPost("http://www.zhtyzx.cn/zhh_sports/commonConfig-checkCode.action", headerList, params, null);
        return mobileCode;
    }

    /*
     提交订单
    */
    public static String saveOrder(String str) throws Exception {
        List<Header> headerList = addPostHeader();
        List ticks = new ArrayList();
        String sResult = sendRequestPost("http://www.zhtyzx.cn/zhh_sports/orderTicket-saveOrderTicket.action", headerList, ticks, str);
        //System.out.println(getLoginCode());
        //System.out.println("end");
        return  sResult ;
    }

    public static String getMessage() throws Exception {
        String messageCode = "";
        for (; ; ) {
            System.out.println("waiting");
            Thread.sleep(10 * 1000);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            //创建get 请求
            HttpGet get = new HttpGet(codeUrl);
            //执行请求
            CloseableHttpResponse response = httpClient.execute(get);
            //获取请求结果
            int code = response.getStatusLine().getStatusCode();
            //System.out.println(code);
            HttpEntity httpEntityt = response.getEntity();
            String result1 = EntityUtils.toString(httpEntityt, "utf-8");
            //凭证号为
            int pos = result1.indexOf("珠海市体育中心免费开放活动日订票的验证码为");
            if (result1.indexOf("珠海市体育中心免费开放活动日订票的验证码为") >= 0) {
                String message = result1.substring(pos, pos + 200);
                Pattern p = Pattern.compile("\\d{6}");
                Matcher m = p.matcher(message);
                while (m.find()) {
                    messageCode = m.group();
                    System.out.println(messageCode);
                }
                break;

            }
        }
        System.out.println(messageCode);
        return messageCode;
    }


    public static String getLoginCode() throws Exception {
        String messageCode = "";
        for (; ; ) {
            System.out.println("waiting");
            Thread.sleep(10 * 1000);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            //创建get 请求
            HttpGet get = new HttpGet(codeUrl);
            //执行请求
            CloseableHttpResponse response = httpClient.execute(get);
            //获取请求结果
            int code = response.getStatusLine().getStatusCode();
            //System.out.println(code);
            HttpEntity httpEntityt = response.getEntity();
            String result1 = EntityUtils.toString(httpEntityt, "utf-8");
            //凭证号为
            int pos = result1.indexOf("凭证号为");
            if (pos >= 0) {
                String message = result1.substring(pos, 15);
                Pattern p = Pattern.compile("\\d{8}");
                Matcher m = p.matcher(message);
                while (m.find()) {
                    messageCode = m.group();
                    System.out.println(messageCode);
                }
                break;

            }
        }
        System.out.println(messageCode);
        return messageCode;
    }




    public static List<Header> addPostHeader() {
        List<Header> headerList = new ArrayList();
        headerList.add(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8"));
        headerList.add(new BasicHeader(HttpHeaders.ACCEPT, "*/*"));
        headerList.add(new BasicHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate"));
        headerList.add(new BasicHeader(HttpHeaders.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.9"));
        headerList.add(new BasicHeader(HttpHeaders.CONNECTION, "keep-alive"));
        headerList.add(new BasicHeader(HttpHeaders.HOST, "www.zhtyzx.cn"));
        headerList.add(new BasicHeader("Origin", "http://www.zhtyzx.cn"));
        headerList.add(new BasicHeader(HttpHeaders.REFERER, "http://www.zhtyzx.cn/zhh_sports/view/item/item_main.jsp"));
        headerList.add(new BasicHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36"));
        headerList.add(new BasicHeader("X-Requested-With", "XMLHttpRequest"));
        return headerList;
    }


    public static void sendRequestGet(String url, List<Header> headers, List<NameValuePair> params) throws IOException, ClassNotFoundException, URISyntaxException {
        if (cookieStores == null) {
            String defaultUrl = "http://www.zhtyzx.cn/";
            //创建一个HttpContext对象，用来保存Cookie
            HttpClientContext httpClientContext = HttpClientContext.create();
            //构造自定义的HttpClient对象
            HttpClient httpClient = HttpClients.custom().build();
            //构造请求对象
            HttpUriRequest httpUriRequest = RequestBuilder.get().setUri(defaultUrl).build();

            //执行请求，传入HttpContext，将会得到请求结果的信息
            httpClient.execute(httpUriRequest, httpClientContext);

            //从请求结果中获取Cookie，此时的Cookie已经带有登录信息了
            cookieStores = httpClientContext.getCookieStore();
            //saveCookieStore(cookieStores,"cookie");

        } else {
            //创建一个HttpContext对象，用来保存Cookie
            HttpClientContext httpClientContext = HttpClientContext.create();
            HttpClient newHttpClient = null;
            //CookieStore cookieStore1 = readCookieStore("cookie");
            if (headers == null) {
                //构造一个带这个Cookie的HttpClient
                newHttpClient = HttpClientBuilder.create().setDefaultCookieStore(cookieStores).build();
            } else {
                //构造一个带这个Cookie的HttpClient
                newHttpClient = HttpClientBuilder.create().setDefaultHeaders(headers).setDefaultCookieStore(cookieStores).build();
            }


            URI uri = null;
            if (params == null) {
                //构造请求资源地址
                uri = new URIBuilder(url).build();
            } else {
                //构造请求资源地址
                uri = new URIBuilder(url).addParameters(params).build();
            }

            //构造请求对象
            HttpUriRequest httpUriRequest = RequestBuilder.get().setUri(uri).build();
            //使用这个新的HttpClient请求就可以了。这时候我们的HttpClient已经带有了之前的登录信息，再爬取就不用登录了
            newHttpClient.execute(httpUriRequest, httpClientContext);
            //从请求结果中获取Cookie，此时的Cookie已经带有登录信息了
            cookieStores = httpClientContext.getCookieStore();
        }
    }


    public static String sendRequestPost(String url, List<Header> headers, List<NameValuePair> params, String pp) throws IOException {
        //创建一个HttpContext对象，用来保存Cookie
        HttpClientContext httpClientContext = HttpClientContext.create();
        HttpPost post2 = new HttpPost(url);
        for (Header header : headers) {
            post2.setHeader(header);
        }
        if (pp != null) {
            //设置请求体
            post2.setEntity(new StringEntity(pp, Charset.forName("UTF-8")));
        } else {
            //包装Entity
            UrlEncodedFormEntity entity2 = new UrlEncodedFormEntity(params, "utf-8");
            //设置请求体
            post2.setEntity(entity2);
        }

        //构造一个带这个Cookie的HttpClient
        HttpClient newHttpClient = HttpClientBuilder.create().setDefaultCookieStore(cookieStores).build();
        HttpResponse response = newHttpClient.execute(post2, httpClientContext);
        int code1 = response.getStatusLine().getStatusCode();
        System.out.println(code1);
        HttpEntity httpEntity3 = response.getEntity();
        String result2 = EntityUtils.toString(httpEntity3, "utf-8");
        return result2;
    }


    //使用序列化的方式保存CookieStore到本地文件，方便后续的读取使用
    private static void saveCookieStore(CookieStore cookieStore, String savePath) throws IOException {

        FileOutputStream fs = new FileOutputStream(savePath);
        ObjectOutputStream os = new ObjectOutputStream(fs);
        os.writeObject(cookieStore);
        os.close();

    }

    //读取Cookie的序列化文件，读取后可以直接使用
    private static CookieStore readCookieStore(String savePath) throws IOException, ClassNotFoundException {

        FileInputStream fs = new FileInputStream("cookie");//("foo.ser");
        ObjectInputStream ois = new ObjectInputStream(fs);
        CookieStore cookieStore = (CookieStore) ois.readObject();
        ois.close();
        return cookieStore;


    }


    //获取短信连接
    public static String getMs() throws Exception {
        String messageCode = "";
        System.out.println("waiting");
        System.setProperty("webdriver.chrome.driver", "D:\\chromeDriver\\chromedriver.exe");// chromedriver服务地址
        WebDriver driver = new ChromeDriver(); // 新建一个WebDriver 的对象，但是new 的是谷歌的驱动
        String url = "https://www.materialtools.com";
        driver.get(url); // 打开指定的网站
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        List<WebElement> pages = driver.findElement(By.className("sms-content-pager")).findElements(By.tagName("a"));
        List<String> pageUrls = new ArrayList<String>() ;
        String pageUrl = "" ;
        int pageSize = 0 ;
        try {
            pageUrl = pages.get(1).getAttribute("href").split("=")[0];
            pageSize = Integer.valueOf(pages.get(pages.size() - 2).getAttribute("href").split("=")[1]);
        }catch (Exception e){
            System.out.println("没有找到分页");
        }
        for (int i = 1 ; i< pageSize ;i++){
            try {
                String hrefUrl = pageUrl+"="+i ;
                System.out.println(hrefUrl);
                driver.get(hrefUrl);
                driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
                List<WebElement> inputElement = driver.findElements(By.className("sms-number-list"));
                for (WebElement element : inputElement) {
                    try {
                        WebElement element1 = element.findElement(By.tagName("img"));
                        if (element1 != null) {
                            String imgSrc = element1.getAttribute("src");
                            if (imgSrc.indexOf("china") >= 0) {
                                String phone = element.findElement(By.className("phone_number-text")).findElement(By.tagName("h3")).getText();
                                String linkText = element.findElement(By.className("sms-number-read")).findElement(By.tagName("a")).getAttribute("href");
                                System.out.println("手机号码:" + phone + "连接:" + linkText);
                            }
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
            }catch (Exception e){
                continue;
            }
        }
        System.out.println(messageCode);
        return messageCode;
    }
}
