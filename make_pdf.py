from reportlab.lib.pagesizes import letter
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import inch
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Image

doc = SimpleDocTemplate("q2.pdf", pagesize=letter,
                        rightMargin=inch, leftMargin=inch,
                        topMargin=inch, bottomMargin=inch)

styles = getSampleStyleSheet()
code_style = ParagraphStyle('code', fontName='Courier', fontSize=9, leading=13, spaceAfter=4)

with open("q2.txt", "r") as f:
    lines = f.readlines()

# split after "t=9" line
split_at = next(i for i, l in enumerate(lines) if l.startswith("t=9")) + 1
top_lines = lines[:split_at]
bottom_lines = lines[split_at:]

def is_code_line(line):
    return line.startswith(" ") or line.startswith("\t") or line.startswith("#") or line.startswith("{") or line.startswith("}")

def lines_to_paragraphs(lines, style):
    result = []
    for line in lines:
        stripped = line.rstrip()
        if stripped == "":
            result.append(Spacer(1, 0.1*inch))
        elif is_code_line(stripped):
            from reportlab.platypus import Preformatted
            result.append(Preformatted(stripped, style))
        else:
            escaped = stripped.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
            result.append(Paragraph(escaped, style))
    return result

content = []
content += lines_to_paragraphs(top_lines, code_style)
content.append(Spacer(1, 0.2*inch))
content.append(Image("q2_timing.png", width=6*inch, height=3*inch))
content.append(Spacer(1, 0.2*inch))
content += lines_to_paragraphs(bottom_lines, code_style)

doc.build(content)
print("q2.pdf created.")
