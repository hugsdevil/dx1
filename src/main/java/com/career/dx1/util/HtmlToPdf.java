package com.career.dx1.util;

import com.github.jhonnymertz.wkhtmltopdf.wrapper.Pdf;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.WrapperConfig;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.XvfbConfig;

public class HtmlToPdf {
    private static String xvfb;
    private static String wkhtmltopdf;

    public static void init(String xvfb, String wkhtmltopdf) {
        HtmlToPdf.xvfb = xvfb;
        HtmlToPdf.wkhtmltopdf = wkhtmltopdf;
    }
    
    public static byte[] convert(String html) throws Exception {
        try {
            WrapperConfig wrapperConfig = new WrapperConfig(wkhtmltopdf);
            if (!"none".equals(xvfb)) {
                XvfbConfig xvfbConfig = new XvfbConfig(xvfb);
                wrapperConfig.setXvfbConfig(xvfbConfig);
            }
            Pdf pdf = new Pdf(wrapperConfig);
            pdf.addPageFromString(html);
            pdf.setAllowMissingAssets();
            byte[] bytes = pdf.getPDF();
            return bytes;
        } catch (Exception e) {
            throw e;
        }
    }
}
