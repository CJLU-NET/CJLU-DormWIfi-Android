package net.fosky.cjlu.wifi.cmcc.utils;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CMCC {

    public static boolean LoginUrl(String login_page, String phone, String pass) {
        String ac_name = "";
        String ac_ip = "";
        String user_ip = "";
        String base_uri = "";

        if (login_page.contains(":7080")) {
            base_uri = login_page.split(":7080")[0] + ":7090/zmcc/";
        } else {
            base_uri = login_page.split(":7090")[0] + ":7090/zmcc/";
        }

        String[] split = login_page.substring(login_page.indexOf("?") + 1).split("&");

        for (String str : split) {
            if (str.startsWith("wlanuserip=")) {
                user_ip = str.split("=")[1];
            }
            if (str.startsWith("wlanacname=")) {
                ac_name = str.split("=")[1];
            }
            if (str.startsWith("wlanacip=")) {
                ac_ip = str.split("=")[1];
            }
        }

        Log.add(user_ip + "|" + ac_name + "|" + ac_ip);

        String pwdRSA;

        try {
            pwdRSA = URLEncoder.encode(RSA.encrypt(pass), "UTF-8");
            Log.add(pwdRSA);
        } catch (Exception e) {
            return false;
        }

        String para_post = "wlanAcName=" + ac_name + "&wlanAcIp=" + ac_ip + "&wlanUserIp=" + user_ip + "&ssid=&userName=" + phone + "&_userPwd=%E8%BE%93%E5%85%A5%E5%9B%BA%E5%AE%9A%E5%AF%86%E7%A0%81%2F%E4%B8%B4%E6%97%B6%E5%AF%86%E7%A0%81&userPwd=" + pwdRSA + "&verifyCode=&verifyHidden=&issaveinfo=&passType=0";
        para_post = "";
        Log.add(para_post);

        Map<String, String> header = new HashMap<>();
        header.put("Referer", login_page);

        String url = base_uri + "portalLogin.wlan?"/* + System.currentTimeMillis()*/;

        String html = HttpClient.Post(url, para_post, header);

        Log.add(html);

        String pattern = "<input type=\"hidden\" name=\"(.*?)\" id=\".*\" value=\"(.*?)\"/>";

        Pattern r = Pattern.compile(pattern);

        StringBuilder para = new StringBuilder();

        Matcher matcher = r.matcher(html);

        while (matcher.find()) {
            para.append("&").append(matcher.group(1)).append("=").append(matcher.group(2));
        }

        return false;

        /*para_post = String.valueOf(para).substring(1);

        url = base_uri + "portalLoginRedirect.wlan";

        html = HttpClient.Post(url, para_post, header);
        Log.add(html);
        return html.contains("<em class=\"per_tel\">" + phone + "</em>");*/

    }
    public static boolean Login(String phone, String pass) {
        String login_page = Wifi.getPortalPage();
        // Log.add(login_page);
        if (login_page.equals("")) return false;

        if (login_page.contains("portal1.cjlu.edu.cn")) {
            /*String[] split = login_page.substring(login_page.indexOf("?") + 1).split("&");
            String ip = "";

            for (String str : split) {
                if (str.startsWith("wlanuserip=")) {
                    ip = str.split("=")[1];
                    break;
                }
            }
            login_page = "https://120.199.39.54:7090/zmcc/indexForce.wlan?wlanacname=0059.0571.571.00&wlanuserip=" + ip + "&wlanacip=120.199.40.105";
            Log.add(login_page);*/
            return false;
        }
        Log.add(login_page);
        return LoginUrl(login_page, phone, pass);

    }
}
