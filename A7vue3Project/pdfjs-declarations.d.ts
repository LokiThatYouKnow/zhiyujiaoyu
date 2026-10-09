declare module 'pdf.js' {
    export class PDFDocument {
        // 这里可以根据pdf.js实际的PDFDocument类结构，补充相关方法和属性声明
        // 如果不清楚具体结构，可以先简单声明为any类型
        [prop: string]: any;
    }
}