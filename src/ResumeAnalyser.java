import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.*;

class ResumeAnalyzerV2 extends JFrame {

    private static final String[] REQUIRED_SKILLS = {"java", "python", "excel", "sql", "machine learning"};
    private static final String[] INTERNSHIP_KEYWORDS = {"internship", "intern", "trainee"};

    private JTextArea textArea;

    public ResumeAnalyzerV2() {
        setTitle("Resume Analyzer");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JButton selectFolderButton = new JButton("📁 Select Resume Folder");
        textArea = new JTextArea();
        textArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textArea);

        selectFolderButton.addActionListener(e -> chooseAndAnalyzeFolder());

        JPanel topPanel = new JPanel();
        topPanel.add(selectFolderButton);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void chooseAndAnalyzeFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File folder = chooser.getSelectedFile();
            textArea.setText(""); // Clear previous results
            analyzeResumesInFolder(folder);
        }
    }

    private void analyzeResumesInFolder(File folder) {
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf") || name.toLowerCase().endsWith(".docx"));

        if (files == null || files.length == 0) {
            textArea.append("⚠️ No PDF or DOCX resumes found in the selected folder.\n");
            return;
        }

        for (File file : files) {
            try {
                String content = extractTextFromFile(file);
                if (content == null || content.isEmpty()) continue;

                textArea.append("📄 Resume: " + file.getName() + "\n");

                boolean hasRequiredSkill = Arrays.stream(REQUIRED_SKILLS)
                        .anyMatch(skill -> content.toLowerCase().contains(skill.toLowerCase()));
                boolean hasInternship = Arrays.stream(INTERNSHIP_KEYWORDS)
                        .anyMatch(keyword -> content.toLowerCase().contains(keyword.toLowerCase()));

                if (hasRequiredSkill && hasInternship) {
                    textArea.append("✅ Passed Filter\n");
                } else {
                    textArea.append("❌ Did Not Pass Filter\n");
                }

                textArea.append("------------------------------------------------------------\n");

            } catch (Exception ex) {
                textArea.append("❗ Error processing " + file.getName() + ": " + ex.getMessage() + "\n");
            }
        }
    }

    private String extractTextFromFile(File file) throws IOException {
        if (file.getName().toLowerCase().endsWith(".pdf")) {
            try (PDDocument document = PDDocument.load(file)) {
                return new PDFTextStripper().getText(document);
            }
        } else if (file.getName().toLowerCase().endsWith(".docx")) {
            try (FileInputStream fis = new FileInputStream(file)) {
                XWPFDocument document = new XWPFDocument(fis);
                StringBuilder text = new StringBuilder();
                for (XWPFParagraph paragraph : document.getParagraphs()) {
                    text.append(paragraph.getText()).append("\n");
                }
                return text.toString();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ResumeAnalyzer().setVisible(true));
    }
}
