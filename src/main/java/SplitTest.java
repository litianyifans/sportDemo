
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SplitTest {
    public static void main(String[] args) throws Exception {
        System.out.println("1");
        SplitTest st = new SplitTest() ;
        st.doGet();

    }

    //创建HttpClient


    public String getJessionId(List<Cookie> cookies){
        String tmpcookies= "";

        for(Cookie c:cookies){

            tmpcookies += c.toString()+";";
        }
        return  tmpcookies ;
    }


    public  void doGet() throws  Exception{
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //创建get 请求
        HttpGet get = new HttpGet("http://www.zhtyzx.cn/");
        get.setHeader("Cookie","td_cookie=825747291");
        CookieStore cookieStore = new BasicCookieStore();
        httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();

        //执行请求
        CloseableHttpResponse response = httpClient.execute(get);


        //获取请求结果
        int code = response.getStatusLine().getStatusCode();
        System.out.println(code);

       List<Cookie> cookies = cookieStore.getCookies();
        saveCookieStore(cookieStore,"cookie");

        HttpEntity entity = response.getEntity();
        String result = EntityUtils.toString(entity, "utf-8");

        CookieStore cookieStore1 = readCookieStore("cookie");
        //System.out.println(result);
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        // HttpClient
        CloseableHttpClient closeableHttpClient = httpClientBuilder.setDefaultCookieStore(cookieStore).build();
        HttpClientContext httpClientContext = HttpClientContext.create();
        HttpGet get1 = new HttpGet("http://www.zhtyzx.cn/zhh_sports/index.jsp");

        //get1.setHeader("Cookie",getJessionId(cookies));
        //关闭
       // CloseableHttpResponse response1 = httpClient.execute(get1);
        CloseableHttpResponse response1  = closeableHttpClient.execute(get1,httpClientContext) ;
        CookieStore cookieStore11 = httpClientContext.getCookieStore();


        //发送验证码
        List sendResult = sendMessage(httpClient, cookies,cookieStore);

        //创建POST请求
        HttpPost post2= new HttpPost("http://www.zhtyzx.cn/zhh_sports/commonConfig-checkCode.action");
        addPostHeader(cookies,post2) ;

        List<NameValuePair> list2 = new ArrayList<NameValuePair>();
        list2.add(new BasicNameValuePair("mobile","17109324200"));
        list2.add(new BasicNameValuePair("actDate","2019-12-27"));
        list2.add(new BasicNameValuePair("itemId","32"));
        list2.add(new BasicNameValuePair("groundId","100476"));
        list2.add(new BasicNameValuePair("startTime","15:00"));
        list2.add(new BasicNameValuePair("endTime","18:00"));
        list2.add(new BasicNameValuePair("identityCode","530121198205289934 "));
        list2.add(new BasicNameValuePair("type","0"));
        list2.add(new BasicNameValuePair("verifyCode",String.valueOf(sendResult.get(0))));

        //包装Entity
        UrlEncodedFormEntity entity2 = new UrlEncodedFormEntity(list2,"utf-8");
        //设置请求体
        post2.setEntity(entity2);
        CloseableHttpResponse rr = httpClient.execute(post2);
        int code1 = rr.getStatusLine().getStatusCode();
        System.out.println(code1);
        HttpEntity httpEntity3 = rr.getEntity();
        String result2 = EntityUtils.toString(httpEntity3,"utf-8");

        System.out.println(result2);

        if(result2.indexOf("验证码已发送")>=0){
            String messageCode = getMessage(new Date()) ;
            //发送验证码
            sendResult = sendMessage(httpClient, cookies,cookieStore);
            //创建POST请求
            HttpPost saveTickt= new HttpPost("http://www.zhtyzx.cn/zhh_sports/orderTicket-saveOrderTicket.action");
            addPostHeader((List<Cookie>) sendResult.get(1),saveTickt) ;
            List ticks = new ArrayList();
            ticks.add(new BasicNameValuePair("ticketDetial.groundId",URLtoUTF8.toUtf8String("100476")));
            ticks.add(new BasicNameValuePair("ticketDetial.groundName",URLtoUTF8.toUtf8String("保龄球")));
            ticks.add(new BasicNameValuePair("ticketDetial.actDate",URLtoUTF8.toUtf8String("2019-12-04")));
            ticks.add(new BasicNameValuePair("ticketDetial.startTime",URLtoUTF8.toUtf8String("15:00")));
            ticks.add(new BasicNameValuePair("ticketDetial.endTime",URLtoUTF8.toUtf8String("18:00")));
            ticks.add(new BasicNameValuePair("orderTicket.itemId",URLtoUTF8.toUtf8String("32")));
            ticks.add(new BasicNameValuePair("orderTicket.itemName",URLtoUTF8.toUtf8String("保龄球")));
            ticks.add(new BasicNameValuePair("orderTicket.username",URLtoUTF8.toUtf8String("殷婷美")));
            ticks.add(new BasicNameValuePair("orderTicket.identityCode","530121198408214246"));
            ticks.add(new BasicNameValuePair("orderTicket.verifyCode",String.valueOf(sendResult.get(0))));
            ticks.add(new BasicNameValuePair("rand",""));
            ticks.add(new BasicNameValuePair("orderTicket.mobile",URLtoUTF8.toUtf8String("17109324203")));
            ticks.add(new BasicNameValuePair("orderTicket.mobileCheckCode",URLtoUTF8.toUtf8String(messageCode.trim())));
            //包装Entity
            UrlEncodedFormEntity saveTicktUrl = new UrlEncodedFormEntity(ticks,"utf-8");
            //设置请求体
            saveTickt.setEntity(saveTicktUrl);
            CloseableHttpClient httpClient1 = HttpClients.createDefault();
            CloseableHttpResponse saveTicktResponse = httpClient1.execute(saveTickt);
            int strCode = saveTicktResponse.getStatusLine().getStatusCode();
            System.out.println(strCode);
            HttpEntity sp = saveTicktResponse.getEntity();
            String strCotent = EntityUtils.toString(sp,"utf-8");
            System.out.println(strCotent);
        }
    }
    public void addPostHeader(List<Cookie> cookies,HttpPost httpPost){

        httpPost.setHeader("Cookie",getJessionId(cookies));
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        httpPost.setHeader("Accept","*/*");
        httpPost.setHeader("Accept-Encoding","gzip, deflate");
        httpPost.setHeader("Accept-Language","zh-CN,zh;q=0.9");
        httpPost.setHeader("Connection","keep-alive");
        httpPost.setHeader("Host","www.zhtyzx.cn");
        httpPost.setHeader("Origin","http://www.zhtyzx.cn");
        httpPost.setHeader("Referer","http://www.zhtyzx.cn/zhh_sports/view/item/item_main.jsp");
        httpPost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36");
        httpPost.setHeader("X-Requested-With","XMLHttpRequest");
    }
    public List<Object>  sendMessage(CloseableHttpClient httpClient,List<Cookie> cookies,CookieStore cookieStore) throws IOException {
        //创建POST请求
        HttpPost post = new HttpPost("http://www.zhtyzx.cn/zhh_sports/orderTicket-getRand.action?r="+new Date().getTime());
        post.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        post.setHeader("Accept","*/*");
        post.setHeader("Accept-Encoding","gzip, deflate");
        post.setHeader("Accept-Language","zh-CN,zh;q=0.9");
        post.setHeader("Connection","keep-alive");
        post.setHeader("Cookie",getJessionId(cookies));
        post.setHeader("Host","www.zhtyzx.cn");
        post.setHeader("Origin","http://www.zhtyzx.cn");
        post.setHeader("Referer","http://www.zhtyzx.cn/zhh_sports/view/item/item_main.jsp");
        post.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36");
        post.setHeader("X-Requested-With","XMLHttpRequest");
        List list = new ArrayList();
        list.add(new BasicNameValuePair("r",""+new Date().getTime()));
        //包装Entity
        UrlEncodedFormEntity entityt = new UrlEncodedFormEntity(list,"utf-8");
        //设置请求体
        post.setEntity(entityt);
        httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();

        //执行请求
        CloseableHttpResponse responset = httpClient.execute(post);

        int codet = responset.getStatusLine().getStatusCode();
        System.out.println(codet);
        HttpEntity httpEntityt = responset.getEntity();
        String result1 = EntityUtils.toString(httpEntityt,"utf-8");
        String imgCode = result1.substring(13,17) ;

        //System.out.println(result1);
        HttpGet get2 = new HttpGet("http://www.zhtyzx.cn/zhh_sports/view/item/image.jsp?strRand="+imgCode);
        CloseableHttpResponse httpGet2  = httpClient.execute(get2);
        int codeGet2 = httpGet2.getStatusLine().getStatusCode();
        System.out.println(codeGet2);
        HttpEntity httpGet21 = httpGet2.getEntity();
        String httpGet211 = EntityUtils.toString(httpGet21,"utf-8");

        List<Cookie> cookies1 = cookieStore.getCookies();
        List<Object> result = new ArrayList<Object>() ;
        result.add(imgCode) ;
        result.add(cookies1);
        //System.out.println(httpGet211);
        return  result ;
    }


    public String getMessage(Date sysdate) throws Exception {
        String messageCode = "" ;
        while (true){
            Thread.sleep(10*1000);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            //创建get 请求
            HttpGet get = new HttpGet("https://www.materialtools.com/SMSContent/12");
            //执行请求
            CloseableHttpResponse response = httpClient.execute(get);
            //获取请求结果
            int code = response.getStatusLine().getStatusCode();
            //System.out.println(code);
            HttpEntity httpEntityt = response.getEntity();
            String result1 = EntityUtils.toString(httpEntityt,"utf-8");
            System.out.println(result1);
            int pos = result1.indexOf("珠海市体育中心免费开放活动日订票的验证码为") ;
            if(result1.indexOf("珠海市体育中心免费开放活动日订票的验证码为")>=0){
                String message = result1.substring(pos,pos+200);
                Pattern p = Pattern.compile("\\d{6}");
                Matcher m = p.matcher(message);
                while (m.find()) {
                    messageCode = m.group() ;
                    System.out.println(messageCode);
                }
                break;

            }
        }
        System.out.println(messageCode);
        return messageCode ;
    }


    //使用序列化的方式保存CookieStore到本地文件，方便后续的读取使用
    private static void saveCookieStore( CookieStore cookieStore, String savePath ) throws IOException {

        FileOutputStream fs = new FileOutputStream(savePath);
        ObjectOutputStream os =  new ObjectOutputStream(fs);
        os.writeObject(cookieStore);
        os.close();

    }

    //读取Cookie的序列化文件，读取后可以直接使用
    private static CookieStore readCookieStore( String savePath ) throws IOException, ClassNotFoundException {

        FileInputStream fs = new FileInputStream("cookie");//("foo.ser");
        ObjectInputStream ois = new ObjectInputStream(fs);
        CookieStore cookieStore = (CookieStore) ois.readObject();
        ois.close();
        return cookieStore;


    }


}
