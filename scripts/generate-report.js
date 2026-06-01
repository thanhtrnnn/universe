/**
 * UniVerse TTCS Report Generator
 * Generates the DOCX report from extracted PDF content
 * Using docx npm package with Vietnamese academic report formatting
 */

const fs = require("fs");
const {
  Document,
  Packer,
  Paragraph,
  TextRun,
  Table,
  TableRow,
  TableCell,
  Header,
  Footer,
  AlignmentType,
  LevelFormat,
  TableOfContents,
  HeadingLevel,
  BorderStyle,
  WidthType,
  ShadingType,
  PageNumber,
  PageBreak,
  TabStopType,
  TabStopPosition,
} = require("docx");

// ── Helpers ──────────────────────────────────────────────────────────────────

const border = { style: BorderStyle.SINGLE, size: 1, color: "000000" };
const borders = { top: border, bottom: border, left: border, right: border };
const cellMargins = { top: 60, bottom: 60, left: 100, right: 100 };

function headerCell(text, width) {
  return new TableCell({
    borders,
    width: { size: width, type: WidthType.DXA },
    shading: { fill: "D9E2F3", type: ShadingType.CLEAR },
    margins: cellMargins,
    children: [
      new Paragraph({
        alignment: AlignmentType.CENTER,
        children: [new TextRun({ text, bold: true, font: "Times New Roman", size: 24 })],
      }),
    ],
  });
}

function cell(text, width, opts = {}) {
  return new TableCell({
    borders,
    width: { size: width, type: WidthType.DXA },
    shading: opts.shading ? { fill: opts.shading, type: ShadingType.CLEAR } : undefined,
    margins: cellMargins,
    children: [
      new Paragraph({
        alignment: opts.center ? AlignmentType.CENTER : AlignmentType.LEFT,
        children: [new TextRun({ text, font: "Times New Roman", size: 24, ...opts.run })],
      }),
    ],
  });
}

function p(text, opts = {}) {
  return new Paragraph({
    spacing: { after: 120, line: 276 },
    alignment: opts.center ? AlignmentType.CENTER : AlignmentType.JUSTIFIED,
    indent: opts.indent !== false ? { firstLine: 720 } : undefined,
    ...opts.paragraph,
    children: [
      new TextRun({
        text,
        font: "Times New Roman",
        size: 26, // 13pt
        ...opts.run,
      }),
    ],
  });
}

function heading1(text) {
  return new Paragraph({
    heading: HeadingLevel.HEADING_1,
    spacing: { before: 360, after: 240 },
    alignment: AlignmentType.CENTER,
    children: [new TextRun({ text, font: "Times New Roman", size: 28, bold: true })],
  });
}

function heading2(text) {
  return new Paragraph({
    heading: HeadingLevel.HEADING_2,
    spacing: { before: 240, after: 180 },
    children: [new TextRun({ text, font: "Times New Roman", size: 26, bold: true })],
  });
}

function heading3(text) {
  return new Paragraph({
    heading: HeadingLevel.HEADING_3,
    spacing: { before: 180, after: 120 },
    children: [new TextRun({ text, font: "Times New Roman", size: 24, bold: true, italics: true })],
  });
}

function bullet(text, ref = "bullets", level = 0) {
  return new Paragraph({
    numbering: { reference: ref, level },
    spacing: { after: 80, line: 276 },
    children: [new TextRun({ text, font: "Times New Roman", size: 26 })],
  });
}

function emptyLine() {
  return new Paragraph({ spacing: { after: 0 }, children: [] });
}

// ── Abbreviation data ────────────────────────────────────────────────────────

const abbreviations = [
  ["API", "Application Programming Interface"],
  ["CRUD", "Create, Read, Update, Delete"],
  ["ERD", "Entity Relationship Diagram"],
  ["GPS", "Global Positioning System"],
  ["JWT", "JSON Web Token"],
  ["NoSQL", "Not Only SQL"],
  ["RBAC", "Role-Based Access Control"],
  ["REST", "Representational State Transfer"],
  ["SPA", "Single Page Application"],
  ["SQL", "Structured Query Language"],
  ["UI/UX", "User Interface / User Experience"],
  ["UML", "Unified Modeling Language"],
  ["QR", "Quick Response"],
];

// ── Build Report ─────────────────────────────────────────────────────────────

const doc = new Document({
  styles: {
    default: {
      document: {
        run: { font: "Times New Roman", size: 26 },
      },
    },
    paragraphStyles: [
      {
        id: "Heading1",
        name: "Heading 1",
        basedOn: "Normal",
        next: "Normal",
        quickFormat: true,
        run: { size: 28, bold: true, font: "Times New Roman" },
        paragraph: { spacing: { before: 360, after: 240 }, outlineLevel: 0 },
      },
      {
        id: "Heading2",
        name: "Heading 2",
        basedOn: "Normal",
        next: "Normal",
        quickFormat: true,
        run: { size: 26, bold: true, font: "Times New Roman" },
        paragraph: { spacing: { before: 240, after: 180 }, outlineLevel: 1 },
      },
      {
        id: "Heading3",
        name: "Heading 3",
        basedOn: "Normal",
        next: "Normal",
        quickFormat: true,
        run: { size: 24, bold: true, italics: true, font: "Times New Roman" },
        paragraph: { spacing: { before: 180, after: 120 }, outlineLevel: 2 },
      },
    ],
  },
  numbering: {
    config: [
      {
        reference: "bullets",
        levels: [
          {
            level: 0,
            format: LevelFormat.BULLET,
            text: "•",
            alignment: AlignmentType.LEFT,
            style: { paragraph: { indent: { left: 720, hanging: 360 } } },
          },
          {
            level: 1,
            format: LevelFormat.BULLET,
            text: "◦",
            alignment: AlignmentType.LEFT,
            style: { paragraph: { indent: { left: 1440, hanging: 360 } } },
          },
        ],
      },
      {
        reference: "numbers",
        levels: [
          {
            level: 0,
            format: LevelFormat.DECIMAL,
            text: "%1.",
            alignment: AlignmentType.LEFT,
            style: { paragraph: { indent: { left: 720, hanging: 360 } } },
          },
        ],
      },
    ],
  },
  sections: [
    // ════════════════════════════════════════════════════════════════════════
    // COVER PAGE
    // ════════════════════════════════════════════════════════════════════════
    {
      properties: {
        page: {
          size: { width: 11906, height: 16838 }, // A4
          margin: { top: 1440, right: 1440, bottom: 1440, left: 1440 },
        },
      },
      children: [
        emptyLine(),
        p("BỘ THÔNG TIN VÀ TRUYỀN THÔNG", { center: true, indent: false, run: { bold: true, size: 28 } }),
        p("HỌC VIỆN CÔNG NGHỆ BUU CHÍNH VIÊN THÔNG", { center: true, indent: false, run: { bold: true, size: 28 } }),
        emptyLine(),
        emptyLine(),
        emptyLine(),
        p("BÁO CÁO BÀI TẬP LỚN", { center: true, indent: false, run: { bold: true, size: 36 } }),
        p("THỰC TẬP CƠ SỞ", { center: true, indent: false, run: { bold: true, size: 36 } }),
        emptyLine(),
        emptyLine(),
        p("Chủ đề:", { center: true, indent: false }),
        p("UNIVERSE - HỆ SINH THÁI", { center: true, indent: false, run: { bold: true, size: 32 } }),
        p("ĐẠI HỌC SỐ THÔNG MINH", { center: true, indent: false, run: { bold: true, size: 32 } }),
        emptyLine(),
        emptyLine(),
        emptyLine(),
        emptyLine(),
        emptyLine(),
        emptyLine(),
        new Paragraph({
          alignment: AlignmentType.CENTER,
          spacing: { after: 60 },
          children: [new TextRun({ text: "Giảng viên hướng dẫn: ", font: "Times New Roman", size: 26 }), new TextRun({ text: "Đỗ Thị Liên", font: "Times New Roman", size: 26, bold: true })],
        }),
        new Paragraph({
          alignment: AlignmentType.CENTER,
          spacing: { after: 60 },
          children: [new TextRun({ text: "Lớp: ", font: "Times New Roman", size: 26 }), new TextRun({ text: "D23CQCE01-B", font: "Times New Roman", size: 26, bold: true })],
        }),
        new Paragraph({
          alignment: AlignmentType.CENTER,
          spacing: { after: 200 },
          children: [new TextRun({ text: "Nhóm thực hiện: ", font: "Times New Roman", size: 26 }), new TextRun({ text: "HTH", font: "Times New Roman", size: 26, bold: true })],
        }),
        // Team table
        new Table({
          width: { size: 5400, type: WidthType.DXA },
          columnWidths: [3600, 1800],
          alignment: AlignmentType.CENTER,
          rows: [
            ["Trần Xuân Thành", "B23DCAT280"],
            ["Phạm Thị Thiên Hà", "B23DCCN266"],
            ["Nguyễn Bá Hùng", "B23DCAT120"],
          ].map(
            ([name, id]) =>
              new TableRow({
                children: [
                  cell(name, 3600),
                  cell(id, 1800, { center: true }),
                ],
              })
          ),
        }),
      ],
    },

    // ════════════════════════════════════════════════════════════════════════
    // LỜI CẢM ƠN
    // ════════════════════════════════════════════════════════════════════════
    {
      properties: {
        page: {
          size: { width: 11906, height: 16838 },
          margin: { top: 1440, right: 1440, bottom: 1440, left: 1440 },
        },
      },
      headers: {
        default: new Header({
          children: [
            new Paragraph({
              children: [
                new TextRun({ text: "THỰC TẬP CƠ SỞ", font: "Times New Roman", size: 20 }),
                new TextRun({ text: "\tNHÓM UNIVERSE", font: "Times New Roman", size: 20 }),
              ],
              tabStops: [{ type: TabStopType.RIGHT, position: TabStopPosition.MAX }],
            }),
          ],
        }),
      },
      footers: {
        default: new Footer({
          children: [
            new Paragraph({
              alignment: AlignmentType.RIGHT,
              children: [
                new TextRun({ children: [PageNumber.CURRENT], font: "Times New Roman", size: 20 }),
              ],
            }),
          ],
        }),
      },
      children: [
        heading1("LỜI CẢM ƠN"),
        p("Đầu tiên, chúng em xin gửi lời cảm ơn sâu sắc đến Học viện nghệ Bưu chính Viên thông và khoa CNTT1 đã đưa môn học Thực tập cơ sở vào trong chương trình giảng dạy. Đặc biệt, chúng em xin gửi lời cảm ơn sâu sắc đến giảng viên bộ môn Đỗ Thị Liên đã hướng dẫn và truyền đạt những kiến thức quý báu cho chúng em trong suốt thời gian học tập vừa qua."),
        p("Trong thời gian làm việc với cô, chúng em đã được tiếp thu thêm nhiều kiến thức bổ ích, học tập được tinh thần làm việc hiệu quả, nghiêm túc. Đây thực là những điều rất cần thiết cho quá trình học tập và công tác sau này của chúng em. Dưới sự dẫn dắt tận tình của cô, chúng em đã cùng nhau xây dựng và hoàn thiện một tài liệu liên quan đến phần mềm, qua đó áp dụng được gần như toàn bộ kiến thức lý thuyết vào mô hình hóa và triển khai thực tế. Điều này không chỉ nâng cao khả năng vận dụng mà còn giúp chúng em thêm tự tin khi bước ra môi trường làm việc thực tế."),
        p("Một lần nữa, chúng em xin gửi tới cô lời cảm ơn sâu sắc nhất vì tất cả những gì cô đã truyền đạt và dìu dắt chúng em trong suốt học kỳ vừa qua. Kính chúc cô luôn mạnh khỏe, hạnh phúc, gặt hái được nhiều thành công trong sự nghiệp giảng dạy cũng như trong cuộc sống. Mong rằng trong tương lai, chúng em sẽ tiếp tục được học hỏi cô nhiều hơn nữa!"),
        emptyLine(),
        p("Hà Nội, 6 tháng 3 năm 2025", { center: true, indent: false }),
        p("Nhóm sinh viên thực hiện", { center: true, indent: false, run: { bold: true } }),
      ],
    },

    // ════════════════════════════════════════════════════════════════════════
    // MỤC LỤC + DANH MỤC KÝ HIỆU
    // ════════════════════════════════════════════════════════════════════════
    {
      properties: {
        page: {
          size: { width: 11906, height: 16838 },
          margin: { top: 1440, right: 1440, bottom: 1440, left: 1440 },
        },
      },
      headers: {
        default: new Header({
          children: [
            new Paragraph({
              children: [
                new TextRun({ text: "THỰC TẬP CƠ SỞ", font: "Times New Roman", size: 20 }),
                new TextRun({ text: "\tNHÓM UNIVERSE", font: "Times New Roman", size: 20 }),
              ],
              tabStops: [{ type: TabStopType.RIGHT, position: TabStopPosition.MAX }],
            }),
          ],
        }),
      },
      footers: {
        default: new Footer({
          children: [
            new Paragraph({
              alignment: AlignmentType.RIGHT,
              children: [
                new TextRun({ children: [PageNumber.CURRENT], font: "Times New Roman", size: 20 }),
              ],
            }),
          ],
        }),
      },
      children: [
        heading1("MỤC LỤC"),
        new TableOfContents("Table of Contents", {
          hyperlink: true,
          headingStyleRange: "1-3",
        }),
        new Paragraph({ children: [new PageBreak()] }),

        // Danh mục ký hiệu
        heading1("DANH MỤC CÁC KÝ HIỆU VÀ CHỮ VIẾT TẮT"),
        new Table({
          width: { size: 9026, type: WidthType.DXA },
          columnWidths: [2500, 6526],
          rows: [
            new TableRow({
              children: [
                headerCell("Ký hiệu / Viết tắt", 2500),
                headerCell("Ý nghĩa", 6526),
              ],
            }),
            ...abbreviations.map(
              ([abbr, meaning]) =>
                new TableRow({
                  children: [
                    cell(abbr, 2500, { run: { bold: true } }),
                    cell(meaning, 6526),
                  ],
                })
            ),
          ],
        }),
      ],
    },

    // ════════════════════════════════════════════════════════════════════════
    // ĐẶT VẤN ĐỀ + CHƯƠNG 1
    // ════════════════════════════════════════════════════════════════════════
    {
      properties: {
        page: {
          size: { width: 11906, height: 16838 },
          margin: { top: 1440, right: 1440, bottom: 1440, left: 1440 },
        },
      },
      headers: {
        default: new Header({
          children: [
            new Paragraph({
              children: [
                new TextRun({ text: "THỰC TẬP CƠ SỞ", font: "Times New Roman", size: 20 }),
                new TextRun({ text: "\tNHÓM UNIVERSE", font: "Times New Roman", size: 20 }),
              ],
              tabStops: [{ type: TabStopType.RIGHT, position: TabStopPosition.MAX }],
            }),
          ],
        }),
      },
      footers: {
        default: new Footer({
          children: [
            new Paragraph({
              alignment: AlignmentType.RIGHT,
              children: [
                new TextRun({ children: [PageNumber.CURRENT], font: "Times New Roman", size: 20 }),
              ],
            }),
          ],
        }),
      },
      children: [
        // ĐẶT VẤN ĐỀ
        heading1("ĐẶT VẤN ĐỀ"),
        p("Trong bối cảnh chuyển đổi số đang diễn ra mạnh mẽ ở mọi lĩnh vực, giáo dục đại học cũng không nằm ngoài xu hướng đó. Tuy nhiên, thực trạng tại nhiều trường đại học hiện nay vẫn còn tồn tại sự phân mảnh trong quản lý: thông tin học tập nằm rải rác trên nhiều website khác nhau, điểm danh vẫn thực hiện thủ công gây tốn kém thời gian và dễ phát sinh gian lận, thông báo từ nhà trường đến sinh viên thường bị chậm trễ và thiếu kịp thời."),
        p("Sự phân tán này khiến cho sinh viên, giảng viên và nhà trường khó kết nối với nhau một cách hiệu quả, dẫn đến giảm chất lượng quản lý và trải nghiệm học tập."),
        p("Xuất phát từ thực tiễn đó, nhóm đề xuất xây dựng hệ thống UniVerse - một nền tảng All-in-One kết hợp ứng dụng di động (Mobile App) và cổng quản trị web (Web Admin Portal), nhằm kết nối Sinh viên - Giảng viên - Nhà trường theo thời gian thực. Hệ thống giải quyết đồng thời các bài toán: điểm danh thông minh chống gian lận, quản lý học tập toàn diện, và truyền thông nội bộ tức thời."),
        p("Báo cáo này trình bày quá trình phân tích, thiết kế và cài đặt hệ thống UniVerse, bao gồm kiến trúc kỹ thuật, các chức năng chính và kết quả thực nghiệm đạt được."),

        new Paragraph({ children: [new PageBreak()] }),

        // CHƯƠNG 1
        heading1("CHƯƠNG 1: MỞ ĐẦU"),

        heading2("1.1. Tổng quan đề tài"),

        heading3("1.1.1. Bối cảnh và tính cấp thiết"),
        p("Quản lý đại học hiện nay đang đối mặt với nhiều thách thức trong ký nguyên số. Phần lớn các trường đại học tại Việt Nam vẫn đang sử dụng nhiều hệ thống rải rác, thiếu kết nối: cổng thông tin đào tạo, hệ thống điểm danh thủ công, nhóm chat không chính thức, và bảng thông báo vật lý. Điều này dẫn đến:"),
        bullet("Sinh viên phải truy cập nhiều nền tảng khác nhau để xem lịch học, điểm số, thông báo."),
        bullet("Giảng viên tốn nhiều thời gian cho công tác hành chính như điểm danh, nhập điểm, thông báo lớp."),
        bullet("Nhà trường thiếu công cụ tổng hợp dữ liệu và phân tích xu hướng học tập theo thời gian thực."),
        bullet('Tình trạng "điểm danh hộ" trong môi trường đại học vẫn chưa có giải pháp công nghệ hiệu quả.'),
        p("Trước thực trạng đó, việc xây dựng một nền tảng tích hợp, thống nhất và hoạt động theo thời gian thực là nhu cầu cấp thiết và có giá trị thực tiễn cao."),

        heading3("1.1.2. Mục tiêu của đề tài"),
        p("Đề tài UniVerse hướng đến xây dựng một hệ sinh thái đại học số thông minh với các mục tiêu cụ thể:"),
        bullet("Xây dựng ứng dụng di động đa nền tảng phục vụ sinh viên và giảng viên."),
        bullet("Xây dựng cổng quản trị web phục vụ quản trị viên và nhà trường."),
        bullet("Triển khai hệ thống điểm danh thông minh kết hợp QR Code động và định vị GPS để chống gian lận."),
        bullet("Cung cấp các tính năng quản lý học tập toàn diện: đăng ký tín chỉ, xem lịch học, lịch thi, điểm số."),
        bullet("Tích hợp hệ thống thông báo và chat thời gian thực giữa sinh viên và giảng viên."),
        bullet("Cung cấp Dashboard phân tích thống kê cho quản trị viên."),

        heading3("1.1.3. Đối tượng và phạm vi nghiên cứu"),
        p("Đối tượng nghiên cứu: Quy trình quản lý học tập tại các trường đại học, bao gồm quy trình điểm danh, quản lý lịch học, nhập điểm và truyền thông nội bộ."),
        p("Phạm vi nghiên cứu:"),
        bullet("Phạm vi chức năng: Tập trung vào 3 nhóm người dùng chính - Sinh viên, Giảng viên, Quản trị viên với các chức năng tương ứng theo phân tích RBAC."),
        bullet("Phạm vi công nghệ: Ứng dụng di động React Native, Web Portal Next.js, Backend NestJS (Node.js), cơ sở dữ liệu PostgreSQL, MongoDB, Redis, và các công nghệ thời gian thực."),
        bullet("Phạm vi thực nghiệm: Kiểm thử trên môi trường phát triển với Docker."),

        heading2("1.2. Giới thiệu công nghệ sử dụng"),

        heading3("1.2.1. Công nghệ Frontend"),
        p('Mobile App - React Native: React Native là framework phát triển ứng dụng di động đa nền tảng của Meta (Facebook), cho phép xây dựng ứng dụng chạy trên cả iOS và Android từ một bộ mã nguồn duy nhất. Với kiến trúc component-based và hiệu năng tiềm cận native, React Native được lựa chọn cho ứng dụng UniVerse nhằm tối ưu chi phí phát triển và đảm bảo trải nghiệm người dùng mượt mà.'),
        p("Web Portal - Next.js + TailwindCSS: Next.js là framework React hỗ trợ Server-Side Rendering (SSR) và Static Site Generation (SSG), mang lại tốc độ tải trang nhanh và trải nghiệm SPA hiệu quả. TailwindCSS cung cấp hệ thống utility-class giúp xây dựng giao diện nhanh chóng và nhất quán."),

        heading3("1.2.2. Công nghệ Backend"),
        p("NestJS (Node.js): NestJS là framework Node.js được xây dựng theo kiến trúc module hóa, lấy cảm hứng từ Angular. Framework này hỗ trợ TypeScript mạnh mẽ, tích hợp sẵn Dependency Injection, và tuân thủ chuẩn RESTful API. Với kiến trúc rõ ràng và khả năng mở rộng cao, NestJS phù hợp với yêu cầu của một hệ thống enterprise như UniVerse."),

        heading3("1.2.3. Hệ quản trị cơ sở dữ liệu"),
        bullet("PostgreSQL (SQL): Hệ quản trị cơ sở dữ liệu quan hệ mã nguồn mở, được sử dụng để lưu trữ các dữ liệu có cấu trúc và yêu cầu tính toán vẹn cao như thông tin người dùng, điểm số, lịch học. PostgreSQL đảm bảo tính ACID (Atomicity, Consistency, Isolation, Durability) cho các giao dịch quan trọng."),
        bullet("MongoDB (NoSQL): Cơ sở dữ liệu tài liệu được sử dụng để lưu trữ dữ liệu phi cấu trúc như log hoạt động, nội dung chat, và thông báo. MongoDB cung cấp tính linh hoạt cao trong việc thay đổi schema theo yêu cầu phát triển."),
        bullet("Redis: Cơ sở dữ liệu in-memory được sử dụng làm lớp cache nhằm tăng tốc độ truy vấn cho các dữ liệu thường xuyên được truy cập như lịch học, thông tin người dùng đã xác thực."),

        heading3("1.2.4. Các công nghệ nâng cao và DevOps"),
        bullet("Socket.IO / WebSocket: Công nghệ giao tiếp hai chiều thời gian thực, được áp dụng cho tính năng Chat giữa sinh viên-giảng viên và hệ thống thông báo tức thời."),
        bullet("Apache Kafka: Message broker phân tán, xử lý luồng sự kiện (event streaming) với thông lượng cao. Kafka được sử dụng để xử lý việc gửi thông báo diện rộng đến toàn bộ sinh viên trong lớp học."),
        bullet("Docker: Công nghệ đóng gói ứng dụng dạng container, đảm bảo môi trường chạy nhất quán từ development đến production và hỗ trợ khả năng mở rộng theo chiều ngang."),
        bullet("AI Service (Python + OpenAI API): Tích hợp Chatbot trí tuệ nhân tạo sử dụng OpenAI API để hỗ trợ sinh viên trả lời các câu hỏi thường gặp về học tập, thủ tục hành chính một cách tự động."),
      ],
    },

    // ════════════════════════════════════════════════════════════════════════
    // CHƯƠNG 2: PHÂN TÍCH HỆ THỐNG
    // ════════════════════════════════════════════════════════════════════════
    {
      properties: {
        page: {
          size: { width: 11906, height: 16838 },
          margin: { top: 1440, right: 1440, bottom: 1440, left: 1440 },
        },
      },
      headers: {
        default: new Header({
          children: [
            new Paragraph({
              children: [
                new TextRun({ text: "THỰC TẬP CƠ SỞ", font: "Times New Roman", size: 20 }),
                new TextRun({ text: "\tNHÓM UNIVERSE", font: "Times New Roman", size: 20 }),
              ],
              tabStops: [{ type: TabStopType.RIGHT, position: TabStopPosition.MAX }],
            }),
          ],
        }),
      },
      footers: {
        default: new Footer({
          children: [
            new Paragraph({
              alignment: AlignmentType.RIGHT,
              children: [
                new TextRun({ children: [PageNumber.CURRENT], font: "Times New Roman", size: 20 }),
              ],
            }),
          ],
        }),
      },
      children: [
        heading1("CHƯƠNG 2: PHÂN TÍCH HỆ THỐNG"),

        heading2("2.1. Phân tích bài toán và quy trình nghiệp vụ"),

        heading3("2.1.1. Hiện trạng quy trình điểm danh và quản lý lớp học"),
        p('Hiện tại, quy trình điểm danh tại các trường đại học chủ yếu được thực hiện theo một trong hai phương thức: (1) Điểm danh thủ công bằng cách gọi tên hoặc ký tên, hoặc (2) Điểm danh qua thẻ sinh viên. Cả hai phương thức đều tồn tại hạn chế rõ rệt:'),
        bullet("Điểm danh thủ công tiêu tốn 5-10 phút đầu buổi học, ảnh hưởng đến thời lượng giảng dạy."),
        bullet('Dễ xảy ra tình trạng "điểm danh hộ" - sinh viên ký tên hoặc trả lời hộ bạn không có mặt.'),
        bullet("Dữ liệu điểm danh không được cập nhật real-time lên hệ thống, gây khó khăn cho việc quản lý."),
        bullet("Giảng viên và quản trị viên không thể theo dõi tỷ lệ chuyên cần tức thời."),

        heading3("2.1.2. Đề xuất quy trình mới trên ứng dụng UniVerse"),
        p("UniVerse đề xuất quy trình điểm danh mới dựa trên sự kết hợp hai lớp bảo mật: QR Code động (thay đổi sau mỗi 5 giây) và xác minh vị trí GPS (Geo-fencing). Quy trình hoạt động như sau:"),
        bullet("Bước 1: Giảng viên mở chức năng tạo mã điểm danh trên ứng dụng, hệ thống tự động tạo QR Code động gắn với tọa độ GPS của phòng học."),
        bullet("Bước 2: Sinh viên mở ứng dụng UniVerse, quét mã QR được hiển thị trên màn hình giảng viên/máy chiếu."),
        bullet("Bước 3: Hệ thống đồng thời kiểm tra: (a) Mã QR còn hiệu lực (chưa quá 5 giây), và (b) Tọa độ GPS hiện tại của sinh viên nằm trong bán kính cho phép của phòng học."),
        bullet("Bước 4: Nếu cả hai điều kiện thỏa mãn, hệ thống ghi nhận điểm danh thành công và cập nhật real-time lên danh sách lớp."),

        new Paragraph({ children: [new PageBreak()] }),

        heading2("2.2. Phân tích yêu cầu chức năng (Theo User Roles)"),

        heading3("2.2.1. Phần hệ Sinh viên (Student)"),
        bullet("Smart Attendance - Điểm danh thông minh: Sinh viên điểm danh bằng cách quét Dynamic QR Code kết hợp xác minh GPS Geo-fencing, đảm bảo sinh viên thực sự có mặt tại phòng học."),
        bullet("Academic Management - Quản lý học tập: Đăng ký tín chỉ trực tuyến; Xem thông tin cá nhân, lịch học, lịch thi; Theo dõi điểm số và tiến độ hoàn thành tín chỉ (đã hoàn thành / còn thiếu bao nhiêu tín chỉ để tốt nghiệp)."),
        bullet("Social & Communication: Chat trực tiếp với giảng viên; Nhận thông báo từ giảng viên và nhà trường về các thay đổi lịch học, thông báo khẩn."),

        heading3("2.2.2. Phần hệ Giảng viên (Lecturer)"),
        bullet("Class Management - Quản lý lớp học: Xem lịch dạy và danh sách lớp; Tạo mã điểm danh QR động tại đầu mỗi buổi học; Xem danh sách sinh viên vắng/trễ theo thời gian thực."),
        bullet("Grading - Nhập điểm: Nhập điểm thành phần và điểm thi cuối kỳ; Phê duyệt yêu cầu nghỉ phép của sinh viên với lý do chính đáng."),
        bullet("Notification - Thông báo: Gửi thông báo khẩn đến toàn bộ sinh viên trong lớp (thay đổi phòng, hủy buổi học) thông qua hệ thống Kafka đảm bảo độ tin cậy cao."),

        heading3("2.2.3. Phần hệ Quản trị viên (Admin)"),
        bullet("System Management - Quản lý hệ thống: Thực hiện CRUD tài khoản người dùng (sinh viên, giảng viên); Phân quyền theo mô hình RBAC; Xếp thời khóa biểu cho các lớp học."),
        bullet("Analytics Dashboard - Bảng phân tích: Biểu đồ thống kê tỷ lệ chuyên cần theo lớp/khoa/toàn trường; Phân tích xu hướng học tập; Báo cáo định kỳ dưới dạng có thể xuất file."),

        heading2("2.3. Phân tích yêu cầu phi chức năng"),

        heading3("2.3.1. Tính bảo mật và toàn vẹn dữ liệu"),
        p("Hệ thống áp dụng các cơ chế bảo mật đa lớp: xác thực JWT (JSON Web Token), phân quyền RBAC, mã hóa HTTPS cho toàn bộ giao tiếp. Đặc biệt, cơ chế chống gian lận điểm danh được thiết kế với hai lớp kiểm tra độc lập (QR + GPS) đảm bảo tính toàn vẹn của dữ liệu chuyên cần."),

        heading3("2.3.2. Hiệu năng và khả năng mở rộng"),
        p("Kiến trúc tách rời Backend và Frontend, kết hợp Redis cache và Kafka message queue, cho phép hệ thống xử lý đồng thời nhiều yêu cầu mà không gây nghẽn cổ chai. Docker hóa toàn bộ dịch vụ hỗ trợ mở rộng theo chiều ngang khi lượng người dùng tăng."),

        heading3("2.3.3. Trải nghiệm người dùng (UI/UX)"),
        p("Giao diện được thiết kế theo tiêu chuẩn Material Design với hỗ trợ Dark Mode và đa ngôn ngữ (Tiếng Việt / Tiếng Anh). Ứng dụng ưu tiên trải nghiệm Mobile-first với thời gian phản hồi dưới 2 giây cho các thao tác thông thường."),

        heading2("2.4. Biểu đồ Use Case tổng quát và chi tiết"),
        p("[Hình 2.1 - Biểu đồ Use Case tổng quát hệ thống UniVerse]", { run: { italics: true } }),
        p("[Hình 2.2 - Biểu đồ Use Case chi tiết phần hệ Sinh viên]", { run: { italics: true } }),
        p("[Hình 2.3 - Biểu đồ Use Case chi tiết phần hệ Giảng viên]", { run: { italics: true } }),
        p("[Hình 2.4 - Biểu đồ Use Case chi tiết phần hệ Quản trị viên]", { run: { italics: true } }),
        p("(Các biểu đồ UML sẽ được bổ sung vào phần này sau khi hoàn thiện file UML trên GitHub)", { run: { italics: true } }),
      ],
    },

    // ════════════════════════════════════════════════════════════════════════
    // CHƯƠNG 3: THIẾT KẾ HỆ THỐNG
    // ════════════════════════════════════════════════════════════════════════
    {
      properties: {
        page: {
          size: { width: 11906, height: 16838 },
          margin: { top: 1440, right: 1440, bottom: 1440, left: 1440 },
        },
      },
      headers: {
        default: new Header({
          children: [
            new Paragraph({
              children: [
                new TextRun({ text: "THỰC TẬP CƠ SỞ", font: "Times New Roman", size: 20 }),
                new TextRun({ text: "\tNHÓM UNIVERSE", font: "Times New Roman", size: 20 }),
              ],
              tabStops: [{ type: TabStopType.RIGHT, position: TabStopPosition.MAX }],
            }),
          ],
        }),
      },
      footers: {
        default: new Footer({
          children: [
            new Paragraph({
              alignment: AlignmentType.RIGHT,
              children: [
                new TextRun({ children: [PageNumber.CURRENT], font: "Times New Roman", size: 20 }),
              ],
            }),
          ],
        }),
      },
      children: [
        heading1("CHƯƠNG 3: THIẾT KẾ HỆ THỐNG"),

        heading2("3.1. Thiết kế kiến trúc tổng thể (System Architecture)"),

        heading3("3.1.1. Sơ đồ kiến trúc Client - Server"),
        p("Hệ thống UniVerse áp dụng kiến trúc Client-Server với sự phân tách rõ ràng giữa ba tầng: Tầng giao diện (Presentation Layer), Tầng nghiệp vụ (Business Logic Layer) và Tầng dữ liệu (Data Layer)."),
        p("[Hình 3.1 - Sơ đồ kiến trúc tổng thể hệ thống UniVerse]", { run: { italics: true } }),
        bullet("Tầng giao diện: React Native Mobile App (iOS/Android) và Next.js Web Portal."),
        bullet("Tầng nghiệp vụ: NestJS API Server xử lý logic nghiệp vụ, tích hợp Socket.IO cho real-time, Kafka cho message queue, và AI Service (Python) cho chatbot."),
        bullet("Tầng dữ liệu: PostgreSQL (dữ liệu quan hệ), MongoDB (dữ liệu phi cấu trúc), Redis (cache)."),

        heading3("3.1.2. Cơ chế giao tiếp giữa Frontend và Backend"),
        p("Frontend giao tiếp với Backend thông qua hai kênh chính: RESTful API (HTTP/HTTPS) cho các thao tác CRUD thông thường, và WebSocket (Socket.IO) cho các tính năng yêu cầu cập nhật thời gian thực như chat và thông báo tức thời."),
        p("Luồng xác thực sử dụng JWT: Client gửi thông tin đăng nhập → Server xác thực và trả về Access Token + Refresh Token → Client lưu token và đính kèm vào header mỗi request sau đó."),

        heading2("3.2. Thiết kế Cơ sở dữ liệu (Database Design)"),

        heading3("3.2.1. Mô hình quan hệ thực thể (ERD) cho PostgreSQL"),
        p("Cơ sở dữ liệu PostgreSQL bao gồm các bảng chính: User (thông tin người dùng), Role (vai trò), Course (môn học), Class (lớp học), Schedule (thời khóa biểu), Attendance (điểm danh), Grade (điểm số), Enrollment (đăng ký tín chỉ)."),
        p("[Hình 3.2 - Sơ đồ ERD cơ sở dữ liệu PostgreSQL]", { run: { italics: true } }),

        heading3("3.2.2. Thiết kế Schema cho MongoDB (NoSQL)"),
        p("MongoDB lưu trữ 3 collection chính: messages (nội dung chat với cấu trúc conversation/message), notifications (thông báo theo từng user với trạng thái đã đọc/chưa đọc), và activity_logs (log hoạt động hệ thống phục vụ audit trail)."),

        heading2("3.3. Thiết kế giải thuật và xử lý logic đặc thù"),

        heading3("3.3.1. Giải thuật sinh mã QR động và check Geo-fencing"),
        p("Giải thuật sinh QR Code động:"),
        bullet("Server tạo token ngẫu nhiên mới sau mỗi 5 giây, kết hợp với session_id và timestamp."),
        bullet("Token được ký bằng HMAC-SHA256 với secret key của server để chống giả mạo."),
        bullet("QR Code được render từ token và hiển thị trên màn hình giảng viên qua WebSocket."),
        p("Giải thuật kiểm tra Geo-fencing:"),
        bullet("Khi sinh viên quét QR, ứng dụng đồng thời lấy tọa độ GPS hiện tại."),
        bullet("Server tính khoảng cách Haversine giữa tọa độ sinh viên và tọa độ phòng học đã đăng ký."),
        bullet("Nếu khoảng cách ≤ 50m (ngưỡng cấu hình) và token QR hợp lệ → ghi nhận điểm danh thành công."),

        heading3("3.3.2. Luồng xử lý thông báo thời gian thực"),
        p('Khi giảng viên gửi thông báo khẩn: Giảng viên tạo thông báo → API lưu vào MongoDB → Publish event lên Kafka topic "class-notifications" → Kafka Consumer đọc event → Đẩy thông báo qua Socket.IO đến tất cả sinh viên trong lớp đang online → Lưu trạng thái "đã nhận" vào MongoDB.'),

        heading2("3.4. Thiết kế giao diện (UI/UX Design)"),

        heading3("3.4.1. Sitemap và luồng người dùng (User Flow)"),
        p("[Hình 3.3 - Sitemap tổng thể ứng dụng UniVerse]", { run: { italics: true } }),
        p("[Hình 3.4 - User Flow cho quy trình điểm danh của Sinh viên]", { run: { italics: true } }),

        heading3("3.4.2. Thiết kế màn hình ứng dụng Mobile"),
        p("Ứng dụng Mobile bao gồm các màn hình chính: Đăng nhập / Xác thực, Trang chủ (Dashboard cá nhân), Điểm danh QR, Lịch học / Lịch thi, Xem điểm, Chat, Thông báo, và Cài đặt tài khoản."),
        p("[Hình 3.5 - Wireframe các màn hình chính của ứng dụng Mobile (đính kèm link Figma)]", { run: { italics: true } }),

        heading3("3.4.3. Thiết kế màn hình Web Portal (Admin)"),
        p("Web Portal bao gồm: Dashboard tổng quan với biểu đồ thống kê, Quản lý người dùng, Quản lý lớp học / Thời khóa biểu, Báo cáo & Thống kê, và Cài đặt hệ thống."),
        p("[Hình 3.6 - Wireframe các màn hình chính của Web Portal Admin (đính kèm link Figma)]", { run: { italics: true } }),
      ],
    },

    // ════════════════════════════════════════════════════════════════════════
    // CHƯƠNG 4: KẾT QUẢ CÀI ĐẶT VÀ THỰC NGHIỆM
    // ════════════════════════════════════════════════════════════════════════
    {
      properties: {
        page: {
          size: { width: 11906, height: 16838 },
          margin: { top: 1440, right: 1440, bottom: 1440, left: 1440 },
        },
      },
      headers: {
        default: new Header({
          children: [
            new Paragraph({
              children: [
                new TextRun({ text: "THỰC TẬP CƠ SỞ", font: "Times New Roman", size: 20 }),
                new TextRun({ text: "\tNHÓM UNIVERSE", font: "Times New Roman", size: 20 }),
              ],
              tabStops: [{ type: TabStopType.RIGHT, position: TabStopPosition.MAX }],
            }),
          ],
        }),
      },
      footers: {
        default: new Footer({
          children: [
            new Paragraph({
              alignment: AlignmentType.RIGHT,
              children: [
                new TextRun({ children: [PageNumber.CURRENT], font: "Times New Roman", size: 20 }),
              ],
            }),
          ],
        }),
      },
      children: [
        heading1("CHƯƠNG 4: KẾT QUẢ CÀI ĐẶT VÀ THỰC NGHIỆM"),

        heading2("4.1. Môi trường cài đặt và công cụ phát triển"),

        heading3("4.1.1. Cấu hình phần cứng và phần mềm"),
        p("[Bảng 4.1 - Cấu hình môi trường phát triển]", { run: { italics: true } }),
        p("Môi trường phát triển sử dụng hệ điều hành Windows 11 / macOS, Node.js v20 LTS, Python 3.11, Docker Desktop, PostgreSQL 15, MongoDB 7.0, Redis 7.2, và các công cụ hỗ trợ: VS Code, Postman, TablePlus."),

        heading3("4.1.2. Quy trình đóng gói và triển khai với Docker"),
        p("Hệ thống được container hóa bằng Docker với docker-compose.yml định nghĩa các service: api (NestJS), web (Next.js), postgres, mongodb, redis, kafka, zookeeper. Lệnh triển khai: docker-compose up -d sẽ khởi động toàn bộ hệ thống trong môi trường nhất quán."),

        heading2("4.2. Kết quả xây dựng ứng dụng (Kèm hình ảnh demo)"),

        heading3("4.2.1. Demo tính năng Đăng nhập và Phân quyền (RBAC)"),
        p("[Hình 4.1 - Màn hình đăng nhập ứng dụng UniVerse]", { run: { italics: true } }),
        p("[Hình 4.2 - Giao diện sau đăng nhập theo từng vai trò: Sinh viên / Giảng viên / Admin]", { run: { italics: true } }),

        heading3("4.2.2. Demo quy trình Điểm danh bằng QR Code & GPS"),
        p("[Hình 4.3 - Giảng viên tạo mã QR điểm danh động]", { run: { italics: true } }),
        p("[Hình 4.4 - Sinh viên quét mã QR và kết quả xác nhận điểm danh]", { run: { italics: true } }),
        p("[Hình 4.5 - Danh sách điểm danh real-time trên giao diện giảng viên]", { run: { italics: true } }),

        heading3("4.2.3. Demo tính năng Chat"),
        p("[Hình 4.6 - Giao diện chat giữa sinh viên và giảng viên]", { run: { italics: true } }),

        heading3("4.2.4. Demo Dashboard thống kê dành cho Admin"),
        p("[Hình 4.7 - Dashboard Admin với biểu đồ tỷ lệ chuyên cần và thống kê tổng hợp]", { run: { italics: true } }),

        heading2("4.3. Đánh giá và kiểm thử"),

        heading3("4.3.1. Kịch bản kiểm thử (Test Cases)"),
        p("[Bảng 4.2 - Danh sách Test Cases cho tính năng điểm danh QR + GPS]", { run: { italics: true } }),
        p("Nhóm thực hiện kiểm thử các kịch bản: (1) Điểm danh thành công trong phạm vi 50m, (2) Từ chối điểm danh khi ở ngoài phạm vi GPS, (3) Từ chối điểm danh khi QR Code hết hạn, (4) Xử lý đồng thời nhiều sinh viên điểm danh cùng lúc."),

        heading3("4.3.2. Đánh giá hiệu năng hệ thống thực tế"),
        p("Kết quả kiểm thử cho thấy hệ thống xử lý đồng thời tốt trong điều kiện mô phỏng. Thời gian phản hồi API trung bình dưới 200ms với cache Redis. Tính năng điểm danh hoạt động ổn định với độ chính xác GPS đạt yêu cầu trong môi trường có sóng GPS tốt."),
      ],
    },

    // ════════════════════════════════════════════════════════════════════════
    // CHƯƠNG 5: KẾT LUẬN
    // ════════════════════════════════════════════════════════════════════════
    {
      properties: {
        page: {
          size: { width: 11906, height: 16838 },
          margin: { top: 1440, right: 1440, bottom: 1440, left: 1440 },
        },
      },
      headers: {
        default: new Header({
          children: [
            new Paragraph({
              children: [
                new TextRun({ text: "THỰC TẬP CƠ SỞ", font: "Times New Roman", size: 20 }),
                new TextRun({ text: "\tNHÓM UNIVERSE", font: "Times New Roman", size: 20 }),
              ],
              tabStops: [{ type: TabStopType.RIGHT, position: TabStopPosition.MAX }],
            }),
          ],
        }),
      },
      footers: {
        default: new Footer({
          children: [
            new Paragraph({
              alignment: AlignmentType.RIGHT,
              children: [
                new TextRun({ children: [PageNumber.CURRENT], font: "Times New Roman", size: 20 }),
              ],
            }),
          ],
        }),
      },
      children: [
        heading1("CHƯƠNG 5: KẾT LUẬN"),

        heading2("5.1. Các kết quả đạt được"),
        p("Sau quá trình phân tích, thiết kế và cài đặt, nhóm đã hoàn thành các kết quả chính của đề tài UniVerse:"),
        bullet("Xây dựng thành công ứng dụng di động React Native với đầy đủ các chức năng cơ bản cho sinh viên và giảng viên."),
        bullet('Triển khai hệ thống điểm danh thông minh kết hợp QR Code động và GPS Geo-fencing, giải quyết hiệu quả bài toán "điểm danh hộ".'),
        bullet("Xây dựng Web Admin Portal với Dashboard thống kê trực quan."),
        bullet("Tích hợp giao tiếp thời gian thực bằng Socket.IO cho tính năng chat và thông báo."),
        bullet("Đóng gói và triển khai hệ thống bằng Docker, đảm bảo tính nhất quán môi trường."),

        heading2("5.2. Hạn chế của hệ thống"),
        p("Bên cạnh các kết quả đạt được, hệ thống vẫn còn một số hạn chế cần được khắc phục trong tương lai:"),
        bullet("Tính năng GPS phụ thuộc vào chất lượng tín hiệu định vị, có thể không chính xác trong các tòa nhà lớn hoặc khu vực bị che khuất."),
        bullet("Chưa hoàn thiện đầy đủ các tính năng của Campus Wallet (ví điện tử sinh viên)."),
        bullet("Chatbot AI hiện chỉ xử lý được các câu hỏi đơn giản, chưa hỗ trợ ngữ cảnh đa lượt."),
        bullet("Chưa thực hiện kiểm thử tải (load testing) với số lượng người dùng thực tế lớn."),

        heading2("5.3. Hướng phát triển trong tương lai"),
        p("Trong các giai đoạn phát triển tiếp theo, nhóm định hướng mở rộng hệ thống theo các hướng:"),
        bullet("Tích hợp thêm dịch vụ thư viện số: mượn sách, tra cứu tài liệu, gia hạn tài liệu trực tuyến."),
        bullet("Phát triển tính năng Campus Wallet đầy đủ: thanh toán học phí, dịch vụ căng tin, photocopy."),
        bullet("Nâng cấp AI để hỗ trợ học tập cá nhân hóa: gợi ý tài liệu học tập dựa trên lịch sử học của sinh viên."),
        bullet("Triển khai lên môi trường Cloud (AWS/GCP) và thực hiện kiểm thử với người dùng thực tế."),
        bullet("Bổ sung tính năng thông báo lịch thi, nhắc nhở deadline nộp bài qua biometric authentication."),
      ],
    },

    // ════════════════════════════════════════════════════════════════════════
    // TÀI LIỆU THAM KHẢO + PHỤ LỤC
    // ════════════════════════════════════════════════════════════════════════
    {
      properties: {
        page: {
          size: { width: 11906, height: 16838 },
          margin: { top: 1440, right: 1440, bottom: 1440, left: 1440 },
        },
      },
      headers: {
        default: new Header({
          children: [
            new Paragraph({
              children: [
                new TextRun({ text: "THỰC TẬP CƠ SỞ", font: "Times New Roman", size: 20 }),
                new TextRun({ text: "\tNHÓM UNIVERSE", font: "Times New Roman", size: 20 }),
              ],
              tabStops: [{ type: TabStopType.RIGHT, position: TabStopPosition.MAX }],
            }),
          ],
        }),
      },
      footers: {
        default: new Footer({
          children: [
            new Paragraph({
              alignment: AlignmentType.RIGHT,
              children: [
                new TextRun({ children: [PageNumber.CURRENT], font: "Times New Roman", size: 20 }),
              ],
            }),
          ],
        }),
      },
      children: [
        heading1("TÀI LIỆU THAM KHẢO"),
        ...[
          '[1] React Native Documentation. Meta Platforms, Inc. https://reactnative.dev/docs/getting-started',
          '[2] Next.js Documentation. Vercel Inc. https://nextjs.org/docs',
          '[3] NestJS Documentation. Kamil Mysliwiec. https://docs.nestjs.com',
          '[4] PostgreSQL 15 Documentation. The PostgreSQL Global Development Group. https://www.postgresql.org/docs/15/',
          '[5] MongoDB Manual. MongoDB, Inc. https://www.mongodb.com/docs/manual/',
          '[6] Redis Documentation. Redis Ltd. https://redis.io/docs/',
          '[7] Apache Kafka Documentation. Apache Software Foundation. https://kafka.apache.org/documentation/',
          '[8] Socket.IO Documentation. https://socket.io/docs/v4/',
          '[9] Docker Documentation. Docker Inc. https://docs.docker.com/',
          '[10] OpenAI API Reference. OpenAI. https://platform.openai.com/docs/api-reference',
        ].map((ref) => p(ref, { indent: false, run: { size: 24 } })),

        new Paragraph({ children: [new PageBreak()] }),

        heading1("PHỤ LỤC CÀI ĐẶT VÀ TRIỂN KHAI"),

        heading2("A. Thiết lập môi trường"),
        p("Yêu cầu hệ thống: Node.js v20+, Docker Desktop, Git."),
        p("Clone source code:", { indent: false, run: { bold: true } }),
        p("git clone https://github.com/thanhtrnnn/universe.git", { indent: false, run: { size: 22 } }),
        p("cd universe", { indent: false, run: { size: 22 } }),

        heading2("B. Cài đặt triển khai hệ thống"),
        p("Khởi động toàn bộ hệ thống bằng Docker Compose:", { indent: false, run: { bold: true } }),
        p("docker-compose up -d", { indent: false, run: { size: 22 } }),
        p("Sau khi tất cả container khởi động thành công, truy cập:"),
        bullet("Web Admin Portal: http://localhost:3000"),
        bullet("API Server: http://localhost:4000"),
        bullet("API Documentation (Swagger): http://localhost:4000/api"),

        heading2("C. Hình ảnh sản phẩm"),
        p("[Xem các hình ảnh demo sản phẩm tại Chương 4 - Mục 4.2]", { run: { italics: true } }),
        p("[Video demo sản phẩm: đính kèm link YouTube/Drive]", { run: { italics: true } }),
        p("[Link GitHub source code: https://github.com/thanhtrnnn/universe]", { run: { italics: true } }),
        p("[Link Figma thiết kế: https://figma.com/...]", { run: { italics: true } }),
      ],
    },
  ],
});

// ── Generate ─────────────────────────────────────────────────────────────────

const OUTPUT = "[TTCS] Báo cáo - Nhóm HTH.docx";

Packer.toBuffer(doc).then((buffer) => {
  fs.writeFileSync(OUTPUT, buffer);
  console.log(`Generated: ${OUTPUT} (${(buffer.length / 1024).toFixed(1)} KB)`);
});
