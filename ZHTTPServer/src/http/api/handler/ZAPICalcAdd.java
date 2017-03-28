/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package http.api.handler;

import http.api.utils.GsonUtils;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Thieu Vo
 */
public class ZAPICalcAdd extends BaseApiHandler{
    
    private static final ZAPICalcAdd instance = new ZAPICalcAdd();

    private ZAPICalcAdd() {
    }

    public static ZAPICalcAdd getInstance() {
        return instance;
    }
    
    @Override
    public String doAction(HttpServletRequest req) {
        try {
            if (req.getMethod().compareToIgnoreCase("POST") != 0)
                return "{result:0,code:405,msg:\"Wrong method. Must be POST\"}";
            
            InputStream is = req.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            int contentLen = req.getContentLength();
            byte buf[] = new byte[contentLen + 1];
            int letti = 0;

            while (contentLen > 0) {
                letti = is.read(buf);
                baos.write(buf, 0, letti);
                contentLen -= letti;
            }

            String js = new String(baos.toByteArray(), Charset.forName("UTF-8"));
            JsonCacl jsCacl = GsonUtils.fromJsonString(js, JsonCacl.class);
            
            //do someting
            System.out.println(jsCacl.number1 + " " + jsCacl.number2);
           
            Integer sum = jsCacl.number1 + jsCacl.number2;
            //return json
            return ("{result:1,code:0,msg:\"sum = " + sum.toString() + "\"}");

        } catch (Exception ex) {
            return "{result:0,code:404,msg:\"exception\"}";
        }
    }
}
