'use strict';
/**
 * Standalone DOCX generator for module3_attendance_grades.md
 * - PlantUML blocks → rendered as PNG via plantuml.com server
 * - Code blocks → monospace text
 * - All other markdown elements rendered normally
 */

const {
  Document, Paragraph, TextRun, Table, TableRow, TableCell,
  ImageRun, HeadingLevel, AlignmentType, WidthType, ShadingType,
  Packer, Footer, PageNumber,
  convertMillimetersToTwip,
} = require('docx');

const fs   = require('fs');
const path = require('path');
const zlib = require('zlib');
const https = require('https');

const MD_FILE = path.join(__dirname, 'md', 'module3_attendance_grades.md');
const OUTPUT  = path.join(__dirname, 'module3_attendance_grades.docx');

const FONT       = 'Times New Roman';
const CODE_FONT  = 'Courier New';
const BODY_SIZE  = 28;  // 14pt in half-points
const CODE_SIZE  = 22;  // 11pt
const LINE_SPACE = 360; // 1.5x

// ── PlantUML encoding (deflate + custom base64) ─────────────────────────────

function encodePlantUML(code) {
  const deflated = zlib.deflateRawSync(Buffer.from(code, 'utf8'), { level: 9 });
  const encode6 = b => {
    if (b < 10) return String.fromCharCode(48 + b);
    b -= 10;
    if (b < 26) return String.fromCharCode(65 + b);
    b -= 26;
    if (b < 26) return String.fromCharCode(97 + b);
    b -= 26;
    if (b === 0) return '-';
    if (b === 1) return '_';
    return '?';
  };
  let r = '';
  for (let i = 0; i < deflated.length; i += 3) {
    const b0 = deflated[i], b1 = deflated[i + 1] || 0, b2 = deflated[i + 2] || 0;
    r += encode6((b0 >> 2) & 0x3F);
    r += encode6(((b0 & 0x3) << 4) | ((b1 >> 4) & 0xF));
    r += encode6(((b1 & 0xF) << 2) | ((b2 >> 6) & 0x3));
    r += encode6(b2 & 0x3F);
  }
  return r;
}

function fetchPNG(code) {
  const encoded = encodePlantUML(code);
  const url = `https://www.plantuml.com/plantuml/png/${encoded}`;
  return new Promise((resolve, reject) => {
    const chunks = [];
    const req = https.get(url, res => {
      if (res.statusCode !== 200) {
        reject(new Error(`PlantUML HTTP ${res.statusCode} for diagram`));
        return;
      }
      res.on('data', c => chunks.push(c));
      res.on('end', () => resolve(Buffer.concat(chunks)));
    });
    req.on('error', reject);
    req.setTimeout(30000, () => { req.destroy(); reject(new Error('Timeout fetching diagram')); });
  });
}

// ── PNG dimension reader ────────────────────────────────────────────────────

function pngSize(buf) {
  try {
    const w = buf.readUInt32BE(16), h = buf.readUInt32BE(20);
    const targetW = 530;
    return { width: targetW, height: Math.max(80, Math.round(h * targetW / w)) };
  } catch { return { width: 530, height: 300 }; }
}

// ── Inline markdown parser ──────────────────────────────────────────────────

function parseInline(text, codeFont = false) {
  if (!text || !text.trim()) return [new TextRun({ text: text || '', font: FONT, size: BODY_SIZE })];
  const runs = [];
  const re = /(\*\*\*(.+?)\*\*\*)|(\*\*(.+?)\*\*)|(\*(.+?)\*)|(`([^`]+)`)|([^*`]+)/g;
  let m;
  while ((m = re.exec(text)) !== null) {
    if (m[2])  runs.push(new TextRun({ text: m[2], bold: true, italics: true, font: FONT, size: BODY_SIZE }));
    else if (m[4])  runs.push(new TextRun({ text: m[4], bold: true, font: FONT, size: BODY_SIZE }));
    else if (m[6])  runs.push(new TextRun({ text: m[6], italics: true, font: FONT, size: BODY_SIZE }));
    else if (m[8])  runs.push(new TextRun({ text: m[8], font: CODE_FONT, size: CODE_SIZE }));
    else if (m[9])  runs.push(new TextRun({ text: m[9], font: codeFont ? CODE_FONT : FONT, size: codeFont ? CODE_SIZE : BODY_SIZE }));
  }
  return runs.length ? runs : [new TextRun({ text, font: FONT, size: BODY_SIZE })];
}

// ── Element factories ───────────────────────────────────────────────────────

const bodyPara = text => new Paragraph({
  children: parseInline(text),
  spacing: { before: 120, after: 0, line: LINE_SPACE, lineRule: 'auto' },
});

const codePara = text => new Paragraph({
  children: [new TextRun({ text, font: CODE_FONT, size: CODE_SIZE })],
  indent: { left: convertMillimetersToTwip(10) },
  spacing: { before: 40, after: 0 },
});

const bulletPara = (text, level) => new Paragraph({
  children: [new TextRun({ text: '• ', font: FONT, size: BODY_SIZE }), ...parseInline(text)],
  indent: { left: convertMillimetersToTwip(10 + level * 7) },
  spacing: { before: 60, after: 0, line: LINE_SPACE, lineRule: 'auto' },
});

const headingPara = (text, level) => {
  const sizeMap = { 1: 32, 2: 28, 3: 28, 4: 28 };
  const levelMap = { 1: HeadingLevel.HEADING_1, 2: HeadingLevel.HEADING_2, 3: HeadingLevel.HEADING_3, 4: HeadingLevel.HEADING_4 };
  return new Paragraph({
    heading: levelMap[level] || HeadingLevel.HEADING_4,
    children: [new TextRun({ text: level === 1 ? text.toUpperCase() : text, bold: true, italics: level === 3, font: FONT, size: sizeMap[level] })],
    spacing: { before: 240, after: 120, line: LINE_SPACE, lineRule: 'auto' },
  });
};

let figCount = 0;
const imageParas = (data, title) => {
  figCount++;
  const { width, height } = pngSize(data);
  return [
    new Paragraph({
      alignment: AlignmentType.CENTER,
      children: [new ImageRun({ data, transformation: { width, height } })],
      spacing: { before: 120, after: 60 },
    }),
    new Paragraph({
      alignment: AlignmentType.CENTER,
      children: [new TextRun({ text: `Hình ${figCount}: ${title}`, italics: true, font: FONT, size: BODY_SIZE - 4 })],
      spacing: { before: 0, after: 120 },
    }),
  ];
};

// ── Table helpers ───────────────────────────────────────────────────────────

function splitCellContent(text) {
  const segs = [];
  const re = /(<table[\s\S]*?<\/table>)/gi;
  let last = 0, m;
  while ((m = re.exec(text)) !== null) {
    if (m.index > last) segs.push({ type: 'text', content: text.slice(last, m.index) });
    segs.push({ type: 'table', content: m[1] });
    last = m.index + m[0].length;
  }
  if (last < text.length) segs.push({ type: 'text', content: text.slice(last) });
  return segs.length ? segs : [{ type: 'text', content: text }];
}

function makeCellChildren(cell) {
  const segs = splitCellContent(cell);
  const children = [];
  for (const seg of segs) {
    if (seg.type === 'text') {
      const raw = seg.content
        .replace(/<br\s*\/?>/gi, '\n')
        .replace(/<[^>]+>/g, '')
        .replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&amp;/g, '&').replace(/&nbsp;/g, ' ');
      for (const line of raw.split('\n')) {
        const t = line.trim();
        if (t) children.push(new Paragraph({ children: parseInline(t), spacing: { before: 60, after: 0 } }));
      }
    } else {
      const rows = [];
      const trRe = /<tr[^>]*>([\s\S]*?)<\/tr>/gi;
      let trM;
      while ((trM = trRe.exec(seg.content)) !== null) {
        const cells = [];
        const cRe = /<t[hd][^>]*>([\s\S]*?)<\/t[hd]>/gi;
        let cM;
        while ((cM = cRe.exec(trM[1])) !== null)
          cells.push(cM[1].replace(/<[^>]+>/g, '').replace(/&lt;/g,'<').replace(/&gt;/g,'>').replace(/&amp;/g,'&').replace(/&nbsp;/g,' ').trim());
        if (cells.length) rows.push(cells);
      }
      if (rows.length) { const t = makeTable(rows); if (t) children.push(t); }
    }
  }
  return children.length ? children : [new Paragraph({ children: [new TextRun({ text: '', font: FONT, size: BODY_SIZE })] })];
}

function makeTable(rows) {
  if (!rows.length) return null;
  const cols = Math.max(...rows.map(r => r.length));
  const norm = rows.map(r => { const c = [...r]; while (c.length < cols) c.push(''); return c; });
  return new Table({
    width: { size: 100, type: WidthType.PERCENTAGE },
    rows: norm.map((row, ri) =>
      new TableRow({
        children: row.map(cell =>
          new TableCell({
            children: makeCellChildren(cell.trim()),
            shading: ri === 0 ? { type: ShadingType.CLEAR, color: 'auto', fill: 'D9D9D9' } : undefined,
          })
        ),
      })
    ),
  });
}

function parseMdTable(lines, start) {
  const rows = []; let i = start;
  while (i < lines.length) {
    const l = lines[i].trim();
    if (!l.startsWith('|')) break;
    if (/^\|[\s\-:|]+\|$/.test(l)) { i++; continue; }
    const cells = l.split('|').slice(1, -1).map(c => c.trim());
    if (cells.length) rows.push(cells);
    i++;
  }
  return { rows, next: i };
}

function parseHtmlTable(lines, start) {
  let i = start; const parts = [];
  while (i < lines.length) {
    parts.push(lines[i]);
    if (lines[i].toLowerCase().includes('</table>')) { i++; break; }
    i++;
  }
  const html = parts.join('\n');
  const rows = [];
  const trRe = /<tr[^>]*>([\s\S]*?)<\/tr>/gi;
  let trM;
  while ((trM = trRe.exec(html)) !== null) {
    const cells = [];
    const cRe = /<t[hd][^>]*>([\s\S]*?)<\/t[hd]>/gi;
    let cM;
    while ((cM = cRe.exec(trM[1])) !== null)
      cells.push(cM[1].replace(/<[^>]+>/g,'').replace(/&lt;/g,'<').replace(/&gt;/g,'>').replace(/&amp;/g,'&').replace(/&nbsp;/g,' ').trim());
    if (cells.length) rows.push(cells);
  }
  return { rows, next: i };
}

// ── Main processor ──────────────────────────────────────────────────────────

async function processMarkdown(content) {
  const lines = content.split('\n');
  const elements = [];
  let i = 0;

  while (i < lines.length) {
    const line = lines[i];
    const trimmed = line.trim();

    if (!trimmed) { elements.push(new Paragraph({ spacing: { before: 60, after: 0 } })); i++; continue; }

    // Headings
    let hm;
    if ((hm = trimmed.match(/^####\s+(.*)/))) { elements.push(headingPara(hm[1], 4)); i++; continue; }
    if ((hm = trimmed.match(/^###\s+(.*)/)))  { elements.push(headingPara(hm[1], 3)); i++; continue; }
    if ((hm = trimmed.match(/^##\s+(.*)/)))   { elements.push(headingPara(hm[1], 2)); i++; continue; }
    if ((hm = trimmed.match(/^#\s+(.*)/)))    { elements.push(headingPara(hm[1], 1)); i++; continue; }

    // PlantUML block → render as image
    if (trimmed === '```plantuml') {
      let j = i + 1;
      while (j < lines.length && lines[j].trim() !== '```') j++;
      const code = lines.slice(i + 1, j).join('\n');
      const tm = code.match(/^title\s+(.+)/m);
      const title = tm ? tm[1].trim() : 'Biểu đồ';
      try {
        process.stdout.write(`  Rendering: ${title.slice(0, 60)}... `);
        const png = await fetchPNG(code);
        console.log(`OK (${Math.round(png.length / 1024)}KB)`);
        elements.push(...imageParas(png, title));
      } catch (e) {
        console.log(`FAILED: ${e.message}`);
        elements.push(bodyPara(`[Biểu đồ: ${title} — không thể render]`));
      }
      i = j + 1; continue;
    }

    // Other code block → monospace text
    if (trimmed.startsWith('```')) {
      let j = i + 1;
      while (j < lines.length && !lines[j].trim().startsWith('```')) j++;
      const codeLines = lines.slice(i + 1, j);
      elements.push(new Paragraph({ spacing: { before: 60, after: 0 } }));
      for (const cl of codeLines) elements.push(codePara(cl));
      elements.push(new Paragraph({ spacing: { before: 60, after: 0 } }));
      i = j + 1; continue;
    }

    // Horizontal rule
    if (/^[-*_]{3,}$/.test(trimmed)) { elements.push(new Paragraph({ spacing: { before: 120, after: 120 } })); i++; continue; }

    // HTML table
    if (trimmed.toLowerCase().startsWith('<table')) {
      const { rows, next } = parseHtmlTable(lines, i);
      if (rows.length) { const t = makeTable(rows); if (t) { elements.push(t); elements.push(new Paragraph({ spacing: { before: 60, after: 60 } })); } }
      i = next; continue;
    }

    // Skip single-line HTML tags
    if (trimmed.startsWith('<') && !trimmed.startsWith('<!--')) { i++; continue; }

    // Markdown table
    if (trimmed.startsWith('|')) {
      const { rows, next } = parseMdTable(lines, i);
      if (rows.length) { const t = makeTable(rows); if (t) { elements.push(t); elements.push(new Paragraph({ spacing: { before: 60, after: 60 } })); } }
      i = next; continue;
    }

    // Bullet list
    let bm;
    if ((bm = line.match(/^(\s*)[*\-]\s+(.*)/))) { elements.push(bulletPara(bm[2], Math.floor(bm[1].length / 2))); i++; continue; }

    // Numbered list
    if ((bm = line.match(/^\d+\.\s+(.*)/))) { elements.push(bulletPara(bm[1], 0)); i++; continue; }

    // Blockquote
    if (trimmed.startsWith('> ')) {
      elements.push(new Paragraph({
        children: [new TextRun({ text: trimmed.slice(2), italics: true, font: FONT, size: BODY_SIZE })],
        indent: { left: convertMillimetersToTwip(10) },
        spacing: { before: 60, after: 0, line: LINE_SPACE, lineRule: 'auto' },
      }));
      i++; continue;
    }

    elements.push(bodyPara(trimmed));
    i++;
  }

  return elements;
}

async function main() {
  console.log('Generating module3_attendance_grades.docx ...\n');
  const content = fs.readFileSync(MD_FILE, 'utf8');
  const children = await processMarkdown(content);

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
  console.log(`Figures: ${figCount}`);
}

main().catch(e => { console.error(e); process.exit(1); });
