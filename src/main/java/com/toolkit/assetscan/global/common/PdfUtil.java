package com.toolkit.assetscan.global.common;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.toolkit.assetscan.bean.dto.ExcelDataDto;
import com.toolkit.assetscan.bean.po.AssetPo;
import com.toolkit.assetscan.bean.po.ConfigCheckResultPo;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PdfUtil {

    /**
     * 使用浏览器选择路径下载
     * @param response
     * @param fileName
     * @param data
     * @throws Exception
     */
    public static void exportPdf(HttpServletResponse response, String fileName, ExcelDataDto data) throws Exception {

        // 告诉浏览器用什么软件可以打开此文件
        response.setContentType("application/pdf;charset=UTF-8");
        // 下载文件的默认名称
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + ".pdf", "utf-8"));

        exportPdf(data, response.getOutputStream(), fileName);
    }

    private static void exportPdf(ExcelDataDto data, ServletOutputStream out, String fileName) throws Exception {

        BaseFont baseFont = BaseFont.createFont("simsun.ttc,1", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//        BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);

        Font font = new Font(baseFont, 14, Font.NORMAL);

        //创建文件
        Document doc = new Document(new RectangleReadOnly(842F,595F));  // 横版
        //建立一个书写器
        PdfWriter writer = PdfWriter.getInstance(doc, out);
        //打开文件
        doc.open();

        Font font18 = new Font(baseFont, 18, Font.NORMAL);
        Paragraph titleName = new Paragraph(fileName, font18);
        titleName.setAlignment(Element.ALIGN_CENTER);
        //添加内容
        doc.add(titleName);


        List<String> titles = data.getTitles();
        List<List<Object>> rowsDatas = data.getRows();
        int rowNum = rowsDatas.size();
        int colNum = titles.size();

        // 3列的表.
        PdfPTable table = new PdfPTable(colNum);
        table.setWidthPercentage(100); // 宽度100%填充
        table.setSpacingBefore(10f); // 前间距
        table.setSpacingAfter(10f); // 后间距

        List<PdfPRow> listRow = table.getRows();
        //设置列宽
        if (8 == colNum) {
            float[] columnWidths = { 1f, 1f, 1f, 2f, 1f, 1f, 2f, 4f };
            table.setWidths(columnWidths);
        }

        // 表头
        PdfPCell cells1[]= new PdfPCell[colNum];
        PdfPRow row1 = new PdfPRow(cells1);
        int colIndex = 0;
        for (String field : titles) {
            // 设置单元格内容
            cells1[colIndex] = new PdfPCell(new Paragraph(field, font));
            colIndex++;
        }
        listRow.add(row1);


        // 数据
        for (int i=0; i < rowNum; i++) {
            List<Object> rowData = rowsDatas.get(i);

            //行
            PdfPCell cells[]= new PdfPCell[colNum];
            PdfPRow row = new PdfPRow(cells);

            colIndex = 0;
            for (Object cellData : rowData) {
                if (cellData != null) {
                    cells[colIndex] = new PdfPCell(new Paragraph(cellData.toString(), font));
                } else {
                    cells[colIndex] = new PdfPCell(new Paragraph(""));
                }
                colIndex++;
            }
            //把行添加到集合
            listRow.add(row);

        }

        //把表格添加到文件中
        doc.add(table);

        //关闭文档
        doc.close();
        //关闭书写器
        writer.close();
        out.close();

    }

    public static void saveReportPDF(HttpServletResponse response, String fileName, ExcelDataDto data) throws Exception{
        Date now = new Date();
        // 告诉浏览器用什么软件可以打开此文件
        response.setContentType("application/pdf;charset=UTF-8");
        // 下载文件的默认名称
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + ".pdf", "utf-8"));

        ServletOutputStream out = response.getOutputStream();
        BaseFont baseFont = BaseFont.createFont("simsun.ttc,1", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        //创建文件
        Document doc = new Document(PageSize.A4);
        //建立一个书写器
        PdfWriter writer = PdfWriter.getInstance(doc, out);
        //打开文件
        doc.open();

        Font font10 = new Font(baseFont, 10, Font.NORMAL);   // 10号字体
        Font font14 = new Font(baseFont, 14, Font.NORMAL);   // 14号字体
        Font font18 = new Font(baseFont, 18, Font.NORMAL);   // 18号字体
        Font font28 = new Font(baseFont, 18, Font.BOLD);     // 28号加粗字体

        Paragraph titleName = new Paragraph(fileName, font28);
        titleName.setAlignment(Element.ALIGN_CENTER);  // 居中
        doc.add(titleName);

        Paragraph reportTime = new Paragraph("报告生成时间：" + DateFormat.dateToString(now, DateFormat.UTIL_FORMAT), font14);  //报告时间
        reportTime.setAlignment(Element.ALIGN_RIGHT);  // 居右
        doc.add(reportTime);
        doc.add(new Chunk(new LineSeparator(2.0F, 100.0F, null, 1, 0.0F)));  // 单实线


//        任务名称、检测目标、目标IP、问题类型、危害等级、问题描述、检测时间、建议方案
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100); // 宽度100%填充

        List<PdfPRow> listRow = table.getRows();

        PdfPCell cel1 = new PdfPCell(new Paragraph("系统信息:Linux", font14));
        cel1.disableBorderSide(15);
        table.addCell(cel1);
        PdfPCell cel2 = new PdfPCell(new Paragraph("系统版本:18.0.4", font14));
        cel2.disableBorderSide(15);
        table.addCell(cel2);
        PdfPCell cel3 = new PdfPCell(new Paragraph("IP:127.0.0.1", font14));
        cel3.disableBorderSide(15);
        table.addCell(cel3);
        PdfPCell cel4 = new PdfPCell(new Paragraph("问题数:20", font14));
        cel4.disableBorderSide(15);
        table.addCell(cel4);
        PdfPCell cel5 = new PdfPCell(new Paragraph("检测时间:2019-07-08 15:00", font14));
        cel5.disableBorderSide(15);
        table.addCell(cel5);

        PdfPCell cel6 = new PdfPCell(new Paragraph("检测时间:", font14));
        cel6.disableBorderSide(15);
        table.addCell(cel6);
//        把表格添加到文件中
        doc.add(table);

        doc.add(new Chunk(new DottedLineSeparator()));

        PdfPTable table2 = new PdfPTable(1);
        table2.setWidthPercentage(100); // 宽度100%填充

        List<PdfPRow> listRow2 = table2.getRows();

        PdfPCell cel21 = new PdfPCell(new Paragraph("问题描述：", font18));
        cel21.disableBorderSide(15);
        table2.addCell(cel21);

        String ss = "问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述问题描述";
        PdfPCell cel22 = new PdfPCell(new Paragraph(ss, font14));
        cel22.disableBorderSide(15);
        table2.addCell(cel22);
//        把表格添加到文件中
        doc.add(table2);


        doc.add(new Chunk(new LineSeparator(2.0F, 100.0F, null, 1, 0.0F)));  // 单实线
        Paragraph jieshu = new Paragraph("报告结束", font10);
        jieshu.setAlignment(Element.ALIGN_CENTER);  // 居中
        doc.add(jieshu);

        //关闭文档
        doc.close();
        //关闭书写器
        writer.close();
        out.close();
    }

    public static String saveReportPDF(String fileName, String userAccount, List<ConfigCheckResultPo> resultPos, int riskCount, AssetPo assetPo) throws Exception{
        if(assetPo == null) {
            return null;
        }
        BaseFont baseFont = BaseFont.createFont("simsun.ttc,1", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        //创建文件
        Document doc = new Document(PageSize.A4);
        // 文件名
        SimpleDateFormat sdfName = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        String dateName = sdfName.format(new Date());
        String date = sdfDate.format(new Date());
        File file = new File(fileName + dateName + ".pdf");
        String pathName = file.getAbsolutePath();
        //建立一个书写器
        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(file));
        //打开文件
        doc.open();

        Font font10 = new Font(baseFont, 10, Font.NORMAL);   // 10号字体
        Font font12 = new Font(baseFont, 12, Font.NORMAL);   // 12号字体
        Font font14 = new Font(baseFont, 14, Font.NORMAL);   // 14号字体
        Font font18 = new Font(baseFont, 18, Font.NORMAL);   // 18号字体
        Font font20Bold  = new Font(baseFont, 20, Font.BOLD);    // 20号加粗

        Paragraph coverTitle = new Paragraph("\n\n\n" + "资产扫描核查报告", font20Bold);
        coverTitle.setAlignment(Element.ALIGN_CENTER);
        doc.add(coverTitle);
        Paragraph coverdate = new Paragraph("\n\n\n\n\n\n\n\n\n\n\n\n\n" + "核查日期：" + date, font14);
        Paragraph coverchecker = new Paragraph("核查人员：" + userAccount, font14);
        coverdate.setIndentationLeft(180f);
        coverchecker.setIndentationLeft(180f);
        doc.add(coverdate);
        doc.add(coverchecker);
        Paragraph coverPage = new Paragraph("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" + "中国电力科学研究院", font10);
        coverPage.setAlignment(Element.ALIGN_CENTER);
        doc.add(coverPage);

        doc.newPage();
        Paragraph paragraph = new Paragraph("1.基本信息", font18);
        paragraph.setSpacingAfter(10);
        doc.add(paragraph);

        Paragraph paragraph1 = new Paragraph("本次一共核查" + resultPos.size() + "项安全配置，其中存在风险的一共" +
                riskCount + "项，安全配置得分为" + (int)((((float)(resultPos.size() - riskCount)) / resultPos.size()) * 100) + "分。下面是资产基本信息：", font14);
        paragraph1.setFirstLineIndent(24);
        paragraph1.setSpacingAfter(20);
        doc.add(paragraph1);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100); // 宽度100%填充

        PdfPCell cel0 = new PdfPCell(new Paragraph("资产名称: " + assetPo.getName(), font14));
        cel0.disableBorderSide(15);
        table.addCell(cel0);

        String osType = assetPo.getOs_type().equals("1") ? "Windows":"Linux";
        PdfPCell cel1 = new PdfPCell(new Paragraph("系统类型: " + osType, font14));
        cel1.disableBorderSide(15);
        table.addCell(cel1);

        PdfPCell cel2 = new PdfPCell(new Paragraph("系统版本: " + assetPo.getOs_ver(), font14));
        cel2.disableBorderSide(15);
        table.addCell(cel2);

        PdfPCell cel3 = new PdfPCell(new Paragraph("资产IP: " + assetPo.getIp() + "\n\n\n", font14));
        cel3.disableBorderSide(15);
        table.addCell(cel3);
        doc.add(table);

        List<ConfigCheckResultPo> iptablesResultPos = new ArrayList();
        List<ConfigCheckResultPo> syslogResultPos = new ArrayList();
        List<ConfigCheckResultPo> loginResultPos = new ArrayList();
        List<ConfigCheckResultPo> servicesResultPos = new ArrayList();
        List<ConfigCheckResultPo> passowrdResultPos = new ArrayList();
        List<ConfigCheckResultPo> accountsResultPos = new ArrayList();
        List<ConfigCheckResultPo> startupResultPos = new ArrayList();
        for(ConfigCheckResultPo configCheckResultPo: resultPos) {
            if (configCheckResultPo.getConfig_type().equals("startup")) {
                startupResultPos.add(configCheckResultPo);
            } else if (configCheckResultPo.getConfig_type().equals("accounts")) {
                accountsResultPos.add(configCheckResultPo);
            } else if (configCheckResultPo.getConfig_type().equals("services")) {
                servicesResultPos.add(configCheckResultPo);
            } else if (configCheckResultPo.getConfig_type().equals("passowrd")) {
                passowrdResultPos.add(configCheckResultPo);
            } else if (configCheckResultPo.getConfig_type().equals("login")) {
                loginResultPos.add(configCheckResultPo);
            } else if (configCheckResultPo.getConfig_type().equals("syslog")) {
                syslogResultPos.add(configCheckResultPo);
            } else if (configCheckResultPo.getConfig_type().equals("iptables")) {
                iptablesResultPos.add(configCheckResultPo);
            }
        }

        Rectangle rt=doc.getPageSize();  //横向显示
        doc.setPageSize(rt);
        Paragraph paragraph2 = new Paragraph("2.开机安全配置核查详情", font18);
        paragraph2.setSpacingAfter(10);
        doc.add(paragraph2);
        doc.add(getCheckTable(startupResultPos, font14, font10));

        Paragraph paragraph3 = new Paragraph("\n" + "3.账户安全配置核查详情", font18);
        paragraph3.setSpacingAfter(10);
        doc.add(paragraph3);
        doc.add(getCheckTable(accountsResultPos, font14, font10));

        Paragraph paragraph4 = new Paragraph("\n" + "4.密码配置核查详情", font18);
        paragraph4.setSpacingAfter(10);
        doc.add(paragraph4);
        doc.add(getCheckTable(passowrdResultPos, font14, font10));

        Paragraph paragraph5 = new Paragraph("\n" + "5.服务安全配置核查详情", font18);
        paragraph5.setSpacingAfter(10);
        doc.add(paragraph5);
        doc.add(getCheckTable(servicesResultPos, font14, font10));

        Paragraph paragraph6 = new Paragraph("\n" + "6.登录安全配置核查详情", font18);
        paragraph6.setSpacingAfter(10);
        doc.add(paragraph6);
        doc.add(getCheckTable(loginResultPos, font14, font10));

        Paragraph paragraph7 = new Paragraph("\n" + "7.日志安全配置核查详情", font18);
        paragraph7.setSpacingAfter(10);
        doc.add(paragraph7);
        doc.add(getCheckTable(syslogResultPos, font14, font10));

        Paragraph paragraph8 = new Paragraph("\n" + "8.iptables配置核查详情", font18);
        paragraph8.setSpacingAfter(10);
        doc.add(paragraph8);
        doc.add(getCheckTable(iptablesResultPos, font14, font10));

        //关闭文档
        doc.close();
        //关闭书写器
        writer.close();
        return pathName;
    }

    private static PdfPTable getCheckTable(List<ConfigCheckResultPo> resultPos, Font headFont, Font cellFont) throws Exception{
        PdfPTable table = new PdfPTable(26);
        table.setSpacingBefore(10);
        table.addCell(createHCell("编号", 2, headFont));
        table.addCell(createHCell("检查项", 4, headFont));
        table.addCell(createHCell("检查结果", 4, headFont));
        table.addCell(createHCell("漏洞等级", 4, headFont));
        table.addCell(createHCell("是否通过", 4, headFont));
        table.addCell(createHCell("风险描述", 4, headFont));
        table.addCell(createHCell("解决方案", 4, headFont));

        String pass = "通过";
        for (int i = 0; i < resultPos.size(); i++) {
            if (resultPos.get(i).getRisk_level() > 0) {
                pass = "不通过";
            } else {
                pass = "通过";
            }
            table.addCell(createCell((i + 1) + "", 1, 2, cellFont));
            table.addCell(createCell(resultPos.get(i).getCheck_item(), 1, 4, cellFont));
            table.addCell(createCell(resultPos.get(i).getConfig_info(), 1, 4, cellFont));
            table.addCell(createCell(String.valueOf(resultPos.get(i).getRisk_level()), 1, 4, cellFont));
            table.addCell(createCell(pass, 1, 4, cellFont));
            table.addCell(createCell(resultPos.get(i).getRisk_desc(), 1, 4, cellFont));
            table.addCell(createCell(resultPos.get(i).getSolution(), 1, 4, cellFont));
        }
        return table;
    }

    private static PdfPCell createCell(String content, int rowspan, int colspan, Font f) throws Exception{
        PdfPCell cell = new PdfPCell(new Paragraph(content, f));
        cell.setRowspan(rowspan);
        cell.setColspan(colspan);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    private static PdfPCell createHCell(String content, int colspan, Font f) throws Exception{
        PdfPCell cell = new PdfPCell(new Paragraph(content, f));
        cell.setColspan(colspan);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        return cell;
    }
}
