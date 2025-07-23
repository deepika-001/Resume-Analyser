import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ResumeAnalyzer extends JFrame {
    private JTextArea textArea;

    // List of required skills
    private final String[] REQUIRED_SKILLS = {
            "python", "java", "sql", "machine learning", "deep learning", "excel", "tableau", "power bi"
    };

    public ResumeAnalyzer() {
        setTitle("Resume Analyzer");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton button = new JButton("📁 Select Resume Folder");
        textArea = new JTextArea();
        textArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        button.addActionListener(this::handleFolderSelect);

        add(button, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void handleFolderSelect(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File folder = chooser.getSelectedFile();
            textArea.setText("");
            processResumes(folder);
        }
    }

    private void processResumes(File folder) {
        File[] files = folder.listFiles((dir, name) ->
                name.toLowerCase().endsWith(".pdf") || name.toLowerCase().endsWith(".docx")
        );

        if (files == null || files.length == 0) {
            textArea.append("⚠️ No PDF or DOCX resumes found in the selected folder.\n");
            return;
        }

        class Candidate {
            String name, email, phone, skills;
            int skillCount;

            Candidate(String name, String email, String phone, String skills, int skillCount) {
                this.name = name;
                this.email = email;
                this.phone = phone;
                this.skills = skills;
                this.skillCount = skillCount;
            }
        }

        List<Candidate> qualifiedCandidates = new ArrayList<>();

        for (File file : files) {
            try {
                String content = extractTextFromFile(file);
                String email = extractEmail(content);
                String phone = extractPhone(content);
                String skills = extractSkills(content);
                String name = file.getName().replaceAll("_resume|\\.docx|\\.pdf", "");

                int skillCount = skills.isEmpty() ? 0 : skills.split(",").length;

                // Check for internship experience
                boolean hasInternship = content.toLowerCase().contains("intern");

                if (email != null && phone != null && skillCount > 0 && hasInternship) {
                    qualifiedCandidates.add(new Candidate(name, email, phone, skills, skillCount));
                }

            } catch (Exception ex) {
                textArea.append("❌ Failed to process: " + file.getName() + "\n");
            }
        }

        // Sort by skill count descending
        qualifiedCandidates.sort((a, b) -> b.skillCount - a.skillCount);

        int totalResumes = files.length;
        int limit = Math.min(totalResumes / 2, qualifiedCandidates.size());

        textArea.append("🎯 Showing Top " + limit + " Matching Candidates out of " + totalResumes + " resumes:\n\n");

        for (int i = 0; i < limit; i++) {
            Candidate c = qualifiedCandidates.get(i);
            textArea.append("------------------------------------------------------------\n");
            textArea.append("📛 Name   : " + c.name + "\n");
            textArea.append("📧 Email  : " + c.email + "\n");
            textArea.append("📞 Phone  : " + c.phone + "\n");
            textArea.append("💡 Skills : " + c.skills + "\n\n");
        }

        if (qualifiedCandidates.isEmpty()) {
            textArea.append("❗ No qualified candidates found with internship and required skills.\n");
        }
    }

    private String extractTextFromFile(File file) throws Exception {
        if (file.getName().toLowerCase().endsWith(".pdf")) {
            try (PDDocument document = PDDocument.load(file)) {
                return new PDFTextStripper().getText(document);
            }
        } else {
            try (FileInputStream fis = new FileInputStream(file);
                 XWPFDocument doc = new XWPFDocument(fis)) {
                StringBuilder text = new StringBuilder();
                for (XWPFParagraph p : doc.getParagraphs()) {
                    text.append(p.getText()).append("\n");
                }
                return text.toString();
            }
        }
    }

    private String extractEmail(String text) {
        Matcher matcher = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}").matcher(text);
        return matcher.find() ? matcher.group() : null;
    }

    private String extractPhone(String text) {
        String digitsOnly = text.replaceAll("[^0-9]", "");
        Matcher matcher = Pattern.compile("\\d{10}").matcher(digitsOnly);
        return matcher.find() ? matcher.group() : null;
    }

    private String extractSkills(String text) {
        StringBuilder foundSkills = new StringBuilder();
        String lowerText = text.toLowerCase();
        for (String skill : REQUIRED_SKILLS) {
            if (lowerText.contains(skill.toLowerCase())) {
                foundSkills.append(skill).append(", ");
            }
        }
        if (foundSkills.length() > 0) {
            return foundSkills.substring(0, foundSkills.length() - 2); // remove last comma
        }
        return "";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ResumeAnalyzer().setVisible(true));
    }
}
