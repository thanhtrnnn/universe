'use strict';

const {
  Document, Paragraph, TextRun, Table, TableRow, TableCell,
  ImageRun, HeadingLevel, AlignmentType, WidthType, ShadingType,
  Packer, Footer, PageNumber, PageBreak,
  convertMillimetersToTwip,
} = require('docx');

const fs = require('fs');
const path = require('path');

const REPORT_DIR = __dirname;
const DIAGRAMS_DIR = path.join(REPORT_DIR, 'diagrams');
const MD_DIR = path.join(REPORT_DIR, 'md');
const OUTPUT = path.join(REPORT_DIR, '[TTCS] Báo cáo - Nhóm HTH.docx');

const FONT = 'Times New Roman';
const BODY_SIZE = 28;   // 14pt in half-points
const LINE_SPACING = 360; // 1.5x

const FILE_DIAGRAMS = {
  'requirements_he_thong.md': ['uc_tongquan.png'],
  'module1_auth_users.md': ['uc_module1.png', 'entity_module1.png', 'bce_module1.png', 'seq_uc01_login.png', 'seq_uc03_users.png'],
  'module2_academic.md': ['uc_module2.png', 'entity_module2.png', 'bce_module2.png', 'seq_uc07_enroll.png', 'seq_uc05_class.png'],
  'module3_attendance_grades.md': ['uc_module3.png', 'entity_module3.png', 'bce_module3.png', 'seq_uc10_attendance.png', 'seq_uc12_grades.png'],
};

let figureCounter = 0;

function getPngSize(buf) {
  try {
    const w = buf.readUInt32BE(16);
    const h = buf.readUInt32BE(20);
    const targetW = 530; // ~140mm at 96dpi
    return { width: targetW, height: Math.max(100, Math.round(h * targetW / w)) };
  } catch {
    return { width: 530, height: 350 };
  }
}

// Parse inline markdown: **bold**, *italic*, `code`
function parseInline(text) {
  if (!text || !text.trim()) return [new TextRun({ text: text || '', font: FONT, size: BODY_SIZE })];
  const runs = [];
  const re = /(\*\*\*(.+?)\*\*\*)|(\*\*(.+?)\*\*)|(\*(.+?)\*)|(`([^`]+)`)|([^*`]+)/g;
  let m;
  while ((m = re.exec(text)) !== null) {
    if (m[2]) {
      runs.push(new TextRun({ text: m[2], bold: true, italics: true, font: FONT, size: BODY_SIZE }));
    } else if (m[4]) {
      runs.push(new TextRun({ text: m[4], bold: true, font: FONT, size: BODY_SIZE }));
    } else if (m[6]) {
      runs.push(new TextRun({ text: m[6], italics: true, font: FONT, size: BODY_SIZE }));
    } else if (m[8]) {
      runs.push(new TextRun({ text: m[8], font: 'Courier New', size: BODY_SIZE - 4 }));
    } else if (m[9]) {
      runs.push(new TextRun({ text: m[9], font: FONT, size: BODY_SIZE }));
    }
  }
  return runs.length ? runs : [new TextRun({ text, font: FONT, size: BODY_SIZE })];
}

function bodyPara(text) {
  return new Paragraph({
    children: parseInline(text),
    spacing: { before: 120, after: 0, line: LINE_SPACING, lineRule: 'auto' },
  });
}

function bulletPara(text, level) {
  const indent = convertMillimetersToTwip(10 + level * 7);
  return new Paragraph({
    children: [
      new TextRun({ text: '• ', font: FONT, size: BODY_SIZE }),
      ...parseInline(text),
    ],
    indent: { left: indent },
    spacing: { before: 60, after: 0, line: LINE_SPACING, lineRule: 'auto' },
  });
}

function headingPara(text, level) {
  const sizeMap = { 1: 32, 2: 28, 3: 28, 4: 28 };
  const boldMap = { 1: true, 2: true, 3: true, 4: true };
  const italicsMap = { 1: false, 2: false, 3: true, 4: false };
  const levelMap = {
    1: HeadingLevel.HEADING_1,
    2: HeadingLevel.HEADING_2,
    3: HeadingLevel.HEADING_3,
    4: HeadingLevel.HEADING_4,
  };
  const displayText = level === 1 ? text.toUpperCase() : text;
  return new Paragraph({
    heading: levelMap[level] || HeadingLevel.HEADING_4,
    children: [new TextRun({
      text: displayText,
      bold: boldMap[level],
      italics: italicsMap[level],
      font: FONT,
      size: sizeMap[level],
    })],
    spacing: { before: 240, after: 120, line: LINE_SPACING, lineRule: 'auto' },
  });
}

function imageParas(imgPath, title) {
  figureCounter++;
  const data = fs.readFileSync(imgPath);
  const { width, height } = getPngSize(data);
  return [
    new Paragraph({
      alignment: AlignmentType.CENTER,
      children: [new ImageRun({ data, transformation: { width, height } })],
      spacing: { before: 120, after: 60 },
    }),
    new Paragraph({
      alignment: AlignmentType.CENTER,
      children: [new TextRun({
        text: `Hình ${figureCounter}: ${title}`,
        italics: true,
        font: FONT,
        size: BODY_SIZE - 4,
      })],
      spacing: { before: 0, after: 120 },
    }),
  ];
}

function parseMarkdownTableBlock(lines, start) {
  const rows = [];
  let i = start;
  while (i < lines.length) {
    const l = lines[i].trim();
    if (!l.startsWith('|')) break;
    // Skip separator rows: |---|---| or |:---|:---:|
    if (/^\|[\s\-:|]+\|$/.test(l)) { i++; continue; }
    const cells = l.split('|').slice(1, -1).map(c => c.trim());
    if (cells.length > 0) rows.push(cells);
    i++;
  }
  return { rows, next: i };
}

function parseHtmlTableBlock(lines, start) {
  let i = start;
  const htmlParts = [];
  while (i < lines.length) {
    htmlParts.push(lines[i]);
    if (lines[i].toLowerCase().includes('</table>')) { i++; break; }
    i++;
  }
  const html = htmlParts.join('\n');
  const rows = [];
  const trRe = /<tr[^>]*>([\s\S]*?)<\/tr>/gi;
  let trM;
  while ((trM = trRe.exec(html)) !== null) {
    const cells = [];
    const cellRe = /<t[hd][^>]*>([\s\S]*?)<\/t[hd]>/gi;
    let cellM;
    while ((cellM = cellRe.exec(trM[1])) !== null) {
      const txt = cellM[1]
        .replace(/<[^>]+>/g, '')
        .replace(/&lt;/g, '<')
        .replace(/&gt;/g, '>')
        .replace(/&amp;/g, '&')
        .replace(/&nbsp;/g, ' ')
        .trim();
      cells.push(txt);
    }
    if (cells.length) rows.push(cells);
  }
  return { rows, next: i };
}

function makeTable(rows) {
  if (!rows.length) return null;
  // Normalize column count
  const colCount = Math.max(...rows.map(r => r.length));
  const normalizedRows = rows.map(r => {
    while (r.length < colCount) r.push('');
    return r;
  });

  return new Table({
    width: { size: 100, type: WidthType.PERCENTAGE },
    rows: normalizedRows.map((row, ri) =>
      new TableRow({
        children: row.map(cell =>
          new TableCell({
            children: [new Paragraph({
              children: parseInline(cell),
              spacing: { before: 60, after: 60 },
            })],
            shading: ri === 0
              ? { type: ShadingType.CLEAR, color: 'auto', fill: 'D9D9D9' }
              : undefined,
          })
        ),
      })
    ),
  });
}

function processFile(filename, diagrams) {
  const content = fs.readFileSync(path.join(MD_DIR, filename), 'utf8');
  const lines = content.split('\n');
  const elements = [];
  let dIdx = 0;
  let i = 0;

  while (i < lines.length) {
    const line = lines[i];
    const trimmed = line.trim();

    // Blank line
    if (!trimmed) {
      elements.push(new Paragraph({ spacing: { before: 60, after: 0 } }));
      i++; continue;
    }

    // Headings (check longest first)
    let hm;
    if ((hm = trimmed.match(/^####\s+(.*)/))) { elements.push(headingPara(hm[1], 4)); i++; continue; }
    if ((hm = trimmed.match(/^###\s+(.*)/)))  { elements.push(headingPara(hm[1], 3)); i++; continue; }
    if ((hm = trimmed.match(/^##\s+(.*)/)))   { elements.push(headingPara(hm[1], 2)); i++; continue; }
    if ((hm = trimmed.match(/^#\s+(.*)/)))    { elements.push(headingPara(hm[1], 1)); i++; continue; }

    // PlantUML block
    if (trimmed === '```plantuml') {
      let j = i + 1;
      while (j < lines.length && lines[j].trim() !== '```') j++;
      const code = lines.slice(i + 1, j).join('\n');
      const tm = code.match(/^title\s+(.+)/m);
      const title = tm ? tm[1].trim() : 'Biểu đồ';
      if (dIdx < diagrams.length) {
        const imgPath = path.join(DIAGRAMS_DIR, diagrams[dIdx]);
        if (fs.existsSync(imgPath)) {
          elements.push(...imageParas(imgPath, title));
        } else {
          console.warn(`  Missing image: ${diagrams[dIdx]}`);
        }
        dIdx++;
      }
      i = j + 1; continue;
    }

    // Other code block — skip
    if (trimmed.startsWith('```')) {
      let j = i + 1;
      while (j < lines.length && !lines[j].trim().startsWith('```')) j++;
      i = j + 1; continue;
    }

    // Horizontal rule
    if (/^[-*_]{3,}$/.test(trimmed)) {
      elements.push(new Paragraph({ spacing: { before: 120, after: 120 } }));
      i++; continue;
    }

    // HTML table
    if (trimmed.toLowerCase().startsWith('<table')) {
      const { rows, next } = parseHtmlTableBlock(lines, i);
      if (rows.length) {
        const tbl = makeTable(rows);
        if (tbl) { elements.push(tbl); elements.push(new Paragraph({ spacing: { before: 60, after: 60 } })); }
      }
      i = next; continue;
    }

    // Skip other HTML tags (single-line)
    if (trimmed.startsWith('<') && !trimmed.startsWith('<!--')) {
      i++; continue;
    }

    // Markdown table
    if (trimmed.startsWith('|')) {
      const { rows, next } = parseMarkdownTableBlock(lines, i);
      if (rows.length) {
        const tbl = makeTable(rows);
        if (tbl) { elements.push(tbl); elements.push(new Paragraph({ spacing: { before: 60, after: 60 } })); }
      }
      i = next; continue;
    }

    // Bullet list
    let bm;
    if ((bm = line.match(/^(\s*)[*\-]\s+(.*)/))) {
      const level = Math.floor(bm[1].length / 2);
      elements.push(bulletPara(bm[2], level));
      i++; continue;
    }

    // Numbered list
    if ((bm = line.match(/^\d+\.\s+(.*)/))) {
      elements.push(bulletPara(bm[1], 0));
      i++; continue;
    }

    // Blockquote — render as italic body
    if (trimmed.startsWith('> ')) {
      elements.push(new Paragraph({
        children: [new TextRun({ text: trimmed.slice(2), italics: true, font: FONT, size: BODY_SIZE })],
        indent: { left: convertMillimetersToTwip(10) },
        spacing: { before: 60, after: 0, line: LINE_SPACING, lineRule: 'auto' },
      }));
      i++; continue;
    }

    // Regular paragraph
    elements.push(bodyPara(trimmed));
    i++;
  }

  return elements;
}

async function main() {
  console.log('Generating TTCS Report DOCX...\n');

  const children = [];

  // ── Cover Page ──────────────────────────────────────────────────────────────
  const cp = (text, size, bold, spacing) => new Paragraph({
    alignment: AlignmentType.CENTER,
    children: [new TextRun({ text, bold: !!bold, font: FONT, size })],
    spacing,
  });

  children.push(
    cp('HỌC VIỆN CÔNG NGHỆ BƯU CHÍNH VIỄN THÔNG', 28, true, { before: convertMillimetersToTwip(35), after: convertMillimetersToTwip(3) }),
    cp('KHOA CÔNG NGHỆ THÔNG TIN 2', 28, false, { before: convertMillimetersToTwip(3), after: convertMillimetersToTwip(20) }),
    cp('BÁO CÁO BÀI TẬP LỚN', 40, true, { before: convertMillimetersToTwip(20), after: convertMillimetersToTwip(5) }),
    cp('MÔN: NHẬP MÔN CÔNG NGHỆ PHẦN MỀM', 32, false, { before: convertMillimetersToTwip(5), after: convertMillimetersToTwip(25) }),
    cp('ĐỀ TÀI:', 32, true, { before: convertMillimetersToTwip(25), after: convertMillimetersToTwip(3) }),
    cp('UNIVERSE – HỆ THỐNG QUẢN LÝ ĐẠI HỌC THÔNG MINH', 32, true, { before: convertMillimetersToTwip(3), after: convertMillimetersToTwip(30) }),
    cp('Nhóm: HTH', 28, true, { before: convertMillimetersToTwip(30), after: convertMillimetersToTwip(3) }),
    cp('Trần Xuân Thành  –  Nguyễn Bá Hùng  –  Phạm Thị Thiên Hà', 28, false, { before: convertMillimetersToTwip(3), after: convertMillimetersToTwip(3) }),
    cp('GVHD: Đỗ Thị Liên', 28, false, { before: convertMillimetersToTwip(3), after: convertMillimetersToTwip(30) }),
    cp('Hà Nội, 2025', 28, false, { before: convertMillimetersToTwip(30), after: 0 }),
    new Paragraph({ children: [new PageBreak()], spacing: { before: 0, after: 0 } }),
  );

  // ── Content Files ────────────────────────────────────────────────────────────
  const files = [
    { name: 'chuong1_mo_dau.md',               diagrams: [] },
    { name: 'requirements_he_thong.md',         diagrams: FILE_DIAGRAMS['requirements_he_thong.md'] },
    { name: 'module1_auth_users.md',            diagrams: FILE_DIAGRAMS['module1_auth_users.md'] },
    { name: 'module2_academic.md',              diagrams: FILE_DIAGRAMS['module2_academic.md'] },
    { name: 'module3_attendance_grades.md',     diagrams: FILE_DIAGRAMS['module3_attendance_grades.md'] },
  ];

  for (const f of files) {
    console.log(`  Processing ${f.name}...`);
    children.push(...processFile(f.name, f.diagrams));
  }

  // ── Document ─────────────────────────────────────────────────────────────────
  const doc = new Document({
    sections: [{
      properties: {
        page: {
          margin: {
            top:    convertMillimetersToTwip(25),
            bottom: convertMillimetersToTwip(25),
            left:   convertMillimetersToTwip(30),
            right:  convertMillimetersToTwip(20),
          },
        },
      },
      footers: {
        default: new Footer({
          children: [new Paragraph({
            alignment: AlignmentType.CENTER,
            children: [new TextRun({ children: [PageNumber.CURRENT], font: FONT, size: BODY_SIZE - 4 })],
          })],
        }),
      },
      children,
    }],
  });

  console.log('\nPacking DOCX...');
  const buf = await Packer.toBuffer(doc);
  fs.writeFileSync(OUTPUT, buf);
  console.log(`\nDone!`);
  console.log(`Output : ${OUTPUT}`);
  console.log(`Size   : ${(buf.length / 1024 / 1024).toFixed(2)} MB`);
  console.log(`Figures: ${figureCounter}`);
}

main().catch(err => { console.error(err); process.exit(1); });
