import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.FileInputStream;
import java.io.IOException;

public class ResumeExtractor {
    public static void main(String[] args) {
        try {
            FileInputStream fis = new FileInputStream("sample_resume.docx"); // use your resume file here
            XWPFDocument document = new XWPFDocument(fis);

            StringBuilder text = new StringBuilder();
            for (XWPFParagraph para : document.getParagraphs()) {
                text.append(para.getText()).append("\n");
            }

            document.close();

            System.out.println("📄 Resume Text:\n");
            System.out.println(text.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
