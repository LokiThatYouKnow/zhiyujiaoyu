package com.mengzhidui.a7springboot.controller;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import fr.opensagres.poi.xwpf.converter.core.BasicURIResolver;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.HWPFOldDocument;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.usermodel.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.core.io.ClassPathResource;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/convert")
public class ConversionController {

    // 转换为TXT
    @PostMapping("/txt")
    public ResponseEntity<byte[]> convertToTxt(@RequestBody Map<String,String> params) {
        String markdown = params.get("markdown");
        byte[] content = markdown.getBytes(StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "document.txt");
        return ResponseEntity.ok().headers(headers).body(content);
    }

    // 转化为DOC
    @PostMapping("/doc")
    public ResponseEntity<byte[]> convertToDoc(@RequestBody Map<String, String> params) throws Exception {
        String markdown = params.get("markdown");
        String html = convertMarkdownToHtml(markdown);

        XWPFDocument document = new XWPFDocument();

        org.jsoup.nodes.Document htmlDoc = Jsoup.parse(html);
        Elements elements = htmlDoc.body().children();

        for (Element element : elements) {
            String tag = element.tagName().toLowerCase();
            if (tag.startsWith("h") && tag.length() == 2) {
                // 标题处理（保持不变）
                int level = Integer.parseInt(tag.substring(1));
                XWPFParagraph paragraph = document.createParagraph();
                XWPFRun run = paragraph.createRun();
                run.setText(element.text());
                setHeadingStyle(run, level);
            } else if (tag.equalsIgnoreCase("p")) {
                // 段落处理（保持不变）
                XWPFParagraph paragraph = document.createParagraph();
                XWPFRun run = paragraph.createRun();
                run.setText(element.text());
            } else if (tag.equalsIgnoreCase("ul") || tag.equalsIgnoreCase("ol")) {
                // 列表处理（保持不变）
                for (Element li : element.children()) {
                    XWPFParagraph paragraph = document.createParagraph();
                    paragraph.setNumID(tag.equalsIgnoreCase("ul") ? BigInteger.valueOf(1) : BigInteger.valueOf(2));
                    XWPFRun run = paragraph.createRun();
                    run.setText(li.text());
                }
            } else if (tag.equalsIgnoreCase("pre")) {
                // 新增：处理代码块（<pre>标签，Markdown代码块转换后的标签）
                XWPFParagraph paragraph = document.createParagraph();
                XWPFRun run = paragraph.createRun();
                // 设置代码字体（等宽字体更适合代码显示）
                run.setFontFamily("Courier New");
                run.setFontSize(10);
                // 关键：获取原始代码内容（包含换行符），并按\n分割成多行
                String codeContent = element.text();
                String[] lines = codeContent.split("\n");
                // 逐行添加，每行会自动换行
                for (String line : lines) {
                    run.setText(line);
                    run.addBreak(); // 用换行符强制在docx中换行
                }
            }
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        document.write(out);
        byte[] content = out.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "document.docx");

        return ResponseEntity.ok().headers(headers).body(content);
    }

    // 转换为DOCX
    @PostMapping("/docx")
    public ResponseEntity<byte[]> convertToDocx(@RequestBody Map<String, String> params) throws Exception {
        String markdown = params.get("markdown");
        String html = convertMarkdownToHtml(markdown);

        XWPFDocument document = new XWPFDocument();

        org.jsoup.nodes.Document htmlDoc = Jsoup.parse(html);
        Elements elements = htmlDoc.body().children();

        for (Element element : elements) {
            String tag = element.tagName().toLowerCase();
            if (tag.startsWith("h") && tag.length() == 2) {
                // 标题处理（保持不变）
                int level = Integer.parseInt(tag.substring(1));
                XWPFParagraph paragraph = document.createParagraph();
                XWPFRun run = paragraph.createRun();
                run.setText(element.text());
                setHeadingStyle(run, level);
            } else if (tag.equalsIgnoreCase("p")) {
                // 段落处理（保持不变）
                XWPFParagraph paragraph = document.createParagraph();
                XWPFRun run = paragraph.createRun();
                run.setText(element.text());
            } else if (tag.equalsIgnoreCase("ul") || tag.equalsIgnoreCase("ol")) {
                // 列表处理（保持不变）
                for (Element li : element.children()) {
                    XWPFParagraph paragraph = document.createParagraph();
                    paragraph.setNumID(tag.equalsIgnoreCase("ul") ? BigInteger.valueOf(1) : BigInteger.valueOf(2));
                    XWPFRun run = paragraph.createRun();
                    run.setText(li.text());
                }
            } else if (tag.equalsIgnoreCase("pre")) {
                // 新增：处理代码块（<pre>标签，Markdown代码块转换后的标签）
                XWPFParagraph paragraph = document.createParagraph();
                XWPFRun run = paragraph.createRun();
                // 设置代码字体（等宽字体更适合代码显示）
                run.setFontFamily("Courier New");
                run.setFontSize(10);
                // 关键：获取原始代码内容（包含换行符），并按\n分割成多行
                String codeContent = element.text();
                String[] lines = codeContent.split("\n");
                // 逐行添加，每行会自动换行
                for (String line : lines) {
                    run.setText(line);
                    run.addBreak(); // 用换行符强制在docx中换行
                }
            }
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        document.write(out);
        byte[] content = out.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "document.docx");

        return ResponseEntity.ok().headers(headers).body(content);
    }

    private void setHeadingStyle(XWPFRun run, int level) {
        // 简单设置字体大小和加粗来区分标题级别
        switch (level) {
            case 1:
                run.setFontSize(24);
                run.setBold(true);
                break;
            case 2:
                run.setFontSize(20);
                run.setBold(true);
                break;
            case 3:
                run.setFontSize(16);
                run.setBold(true);
                break;
            case 4:
                run.setFontSize(14);
                run.setBold(true);
                break;
            case 5:
                run.setFontSize(12);
                run.setBold(true);
                break;
            case 6:
                run.setFontSize(10);
                run.setBold(true);
                break;
            default:
                break;
        }
    }


    // Markdown转HTML的工具方法
    private String convertMarkdownToHtml(String markdown) {
        MutableDataSet options = new MutableDataSet();
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
        Node document = parser.parse(markdown);
        return renderer.render(document);
    }
}
