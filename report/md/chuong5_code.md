# 5.1.2. Code

Ứng dụng được cài đặt bằng **Java Swing** theo mẫu kiến trúc **MVC mở rộng với DAO**:
tầng giao diện là các lớp `*Frm` kế thừa `JFrame` và xử lý sự kiện qua `actionPerformed()`;
tầng điều khiển là các lớp `*DAO` kế thừa lớp `DAO` cơ sở, kết nối CSDL PostgreSQL qua JDBC;
tầng thực thể là các POJO ánh xạ tới bảng dữ liệu. Mỗi mục dưới đây trích đoạn mã cốt lõi
(DAO + xử lý sự kiện giao diện) của một module; các lớp dùng chung được trình bày ở mục 5.1.2.11.

---

## 5.1.2.1. Module quản lý người dùng

Xác thực đăng nhập, tìm kiếm và cập nhật thông tin người dùng (`UserDAO` + `LoginFrm`/`UserManageFrm`/`EditUserFrm`).

**UserDAO – truy vấn CSDL:**

```java
public User checkLogin(String username, String password) {
    String sql = "SELECT * FROM tblUser WHERE username = ? AND password = ?";
    try (Connection con = getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, username);
        ps.setString(2, password);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                if ("inactive".equalsIgnoreCase(rs.getString("status"))) {
                    return null; // tài khoản bị vô hiệu hóa
                }
                User u = mapUser(rs);
                RedisUtil.saveSession(u.getId());   // lưu phiên đăng nhập
                return u;
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException("Lỗi checkLogin: " + e.getMessage(), e);
    }
    return null;
}

public List<User> searchUser(String keyword) {
    List<User> result = new ArrayList<>();
    String sql = "SELECT * FROM tblUser " +
                 "WHERE id ILIKE ? OR fullName ILIKE ? OR username ILIKE ? ORDER BY id";
    String like = "%" + (keyword == null ? "" : keyword) + "%";
    try (Connection con = getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, like); ps.setString(2, like); ps.setString(3, like);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) result.add(mapUser(rs));
        }
    } catch (SQLException e) {
        throw new RuntimeException("Lỗi searchUser: " + e.getMessage(), e);
    }
    return result;
}

public boolean updateUser(User u) {
    String sql = "UPDATE tblUser SET fullName = ?, phone = ?, email = ?, status = ?, role = ? WHERE id = ?";
    try (Connection con = getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, u.getFullName()); ps.setString(2, u.getPhone());
        ps.setString(3, u.getEmail());    ps.setString(4, u.getStatus());
        ps.setString(5, u.getRole());     ps.setString(6, u.getId());
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        throw new RuntimeException("Lỗi updateUser: " + e.getMessage(), e);
    }
}
```

**LoginFrm – xử lý sự kiện đăng nhập:**

```java
@Override
public void actionPerformed(ActionEvent e) {
    String username = inUsername.getText().trim();
    String password = new String(inPassword.getPassword());
    User user = userDAO.checkLogin(username, password);
    if (user == null) {
        outError.setText("Thông tin đăng nhập không chính xác hoặc tài khoản bị khóa");
        return;
    }
    dispose();
    switch (user.getRole()) {            // điều hướng theo vai trò
        case "Admin"    -> new AdminHomeFrm(user).setVisible(true);
        case "Lecturer" -> new LecturerHomeFrm(user).setVisible(true);
        default         -> new StudentHomeFrm(user).setVisible(true);
    }
}
```

**EditUserFrm – lưu thay đổi:**

```java
user.setFullName(outinFullName.getText().trim());
user.setPhone(outinPhone.getText().trim());
user.setEmail(outinEmail.getText().trim());
user.setRole((String) inRole.getSelectedItem());
user.setStatus((String) inStatus.getSelectedItem());
if (userDAO.updateUser(user)) {
    JOptionPane.showMessageDialog(this, "Cập nhật thành công");
    if (onSaved != null) onSaved.run();
    dispose();
}
```

---

## 5.1.2.2. Module gửi thông báo

Giảng viên gửi thông báo tới sinh viên một lớp. Thông báo được **publish lên Kafka**
(`NotificationDAO.sendNotification`); `NotificationConsumer` đọc topic và ghi vào `tblNotification`.

**NotificationDAO – publish & insert:**

```java
public boolean sendNotification(Notification n) {
    if (n.getId() == null)     n.setId("NTF-" + System.nanoTime());
    if (n.getSentAt() == null) n.setSentAt(LocalDateTime.now());
    KafkaUtil.publish(GSON.toJson(NotificationPayload.from(n)));   // -> Kafka
    return true;
}

public boolean insert(Notification n) {   // được consumer gọi để ghi DB
    String sql = "INSERT INTO tblNotification (id, title, content, recipientType, sentAt, tblUserid) " +
                 "VALUES (?, ?, ?, ?, ?, ?)";
    try (Connection con = getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, n.getId());    ps.setString(2, n.getTitle());
        ps.setString(3, n.getContent()); ps.setString(4, n.getRecipientType());
        ps.setTimestamp(5, Timestamp.valueOf(n.getSentAt()));
        ps.setString(6, n.getUserId());
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        throw new RuntimeException("Lỗi insert notification: " + e.getMessage(), e);
    }
}
```

**SendNotificationFrm – gửi cho từng sinh viên của lớp:**

```java
ClassSection cls = classList.get(row);
List<String> studentIds = userDAO.findStudentIdsByClassSection(cls.getId());
for (String sid : studentIds) {
    Notification n = new Notification();
    n.setTitle(title); n.setContent(content);
    n.setRecipientType("class:" + cls.getId()); n.setUserId(sid);
    notificationDAO.sendNotification(n);     // publish Kafka
}
JOptionPane.showMessageDialog(this,
        "Đã gửi thông báo tới " + studentIds.size() + " sinh viên (qua Kafka).");
```

---

## 5.1.2.3. Module quản lý lịch học

Tìm lớp học phần và chỉnh sửa lịch học của lớp (`ScheduleDAO` + `ScheduleManageFrm`/`EditScheduleFrm`).

**ScheduleDAO – cập nhật lịch:**

```java
public boolean updateSchedule(Schedule s) {
    String sql = "UPDATE tblSchedule SET dayOfWeek = ?, startPeriod = ?, endPeriod = ?, " +
                 "room = ?, appliedFrom = ?, appliedTo = ? WHERE id = ?";
    try (Connection con = getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, s.getDayOfWeek());
        ps.setInt(2, s.getStartPeriod()); ps.setInt(3, s.getEndPeriod());
        ps.setString(4, s.getRoom());
        ps.setDate(5, s.getAppliedFrom() != null ? Date.valueOf(s.getAppliedFrom()) : null);
        ps.setDate(6, s.getAppliedTo()   != null ? Date.valueOf(s.getAppliedTo())   : null);
        ps.setString(7, s.getId());
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        throw new RuntimeException("Lỗi updateSchedule: " + e.getMessage(), e);
    }
}
```

**EditScheduleFrm – set lại thuộc tính & lưu:**

```java
schedule.setDayOfWeek((String) inDay.getSelectedItem());
schedule.setStartPeriod(Integer.parseInt(inStart.getText().trim()));
schedule.setEndPeriod(Integer.parseInt(inEnd.getText().trim()));
schedule.setRoom(inRoom.getText().trim());
if (scheduleDAO.updateSchedule(schedule)) {
    JOptionPane.showMessageDialog(this, "Cập nhật lịch học thành công");
    dispose();
}
```

---

## 5.1.2.4. Module quản lý học phần

Liệt kê và thêm mới học phần, kiểm tra trùng mã (`CourseDAO` + `CourseFrm`/`CreateCourseFrm`).

**CourseDAO – danh sách, kiểm tra trùng, thêm mới:**

```java
public List<Course> getListCourse() {
    List<Course> list = new ArrayList<>();
    String sql = "SELECT * FROM tblCourse ORDER BY id";
    try (Connection con = getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) list.add(map(rs));
    } catch (SQLException e) {
        throw new RuntimeException("Lỗi getListCourse: " + e.getMessage(), e);
    }
    return list;
}

public boolean existsById(String id) {
    String sql = "SELECT 1 FROM tblCourse WHERE id = ?";
    try (Connection con = getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, id);
        try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
    } catch (SQLException e) {
        throw new RuntimeException("Lỗi existsById: " + e.getMessage(), e);
    }
}

public boolean createCourse(Course c) {
    String sql = "INSERT INTO tblCourse (id, credits, name, department) VALUES (?, ?, ?, ?)";
    try (Connection con = getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, c.getId());  ps.setInt(2, c.getCredits());
        ps.setString(3, c.getName()); ps.setString(4, c.getDepartment());
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        throw new RuntimeException("Lỗi createCourse: " + e.getMessage(), e);
    }
}
```

**CreateCourseFrm – kiểm tra & lưu:**

```java
if (courseDAO.existsById(id)) {
    JOptionPane.showMessageDialog(this, "Mã học phần đã tồn tại.");
    return;
}
Course c = new Course(id, Integer.parseInt(creditsStr), name, inDepartment.getText().trim());
if (courseDAO.createCourse(c)) {
    JOptionPane.showMessageDialog(this, "Thêm học phần thành công");
    if (onCreated != null) onCreated.run();
    dispose();
}
```

---

## 5.1.2.5. Module quản lý lớp học phần

Liệt kê và cập nhật thông tin lớp học phần (`ClassSectionDAO` + `ClassSectionFrm`/`UpdateClassSectionFrm`).

**ClassSectionDAO – danh sách & cập nhật:**

```java
public List<ClassSection> getListClassSection() {
    List<ClassSection> list = new ArrayList<>();
    String sql = "SELECT * FROM tblClassSection ORDER BY id";
    try (Connection con = getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) list.add(map(rs));
    } catch (SQLException e) {
        throw new RuntimeException("Lỗi getListClassSection: " + e.getMessage(), e);
    }
    return list;
}

public boolean updateClassSection(ClassSection c) {
    String sql = "UPDATE tblClassSection SET classId = ?, name = ?, semester = ?, year = ?, " +
                 "maxStudents = ?, status = ?, tblCourseid = ?, tblLecturerid = ? WHERE id = ?";
    try (Connection con = getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, c.getClassId()); ps.setString(2, c.getName());
        ps.setString(3, c.getSemester()); ps.setString(4, c.getYear());
        ps.setInt(5, c.getMaxStudents()); ps.setString(6, c.getStatus());
        ps.setString(7, c.getCourseId()); ps.setString(8, c.getLecturerId());
        ps.setString(9, c.getId());
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        throw new RuntimeException("Lỗi updateClassSection: " + e.getMessage(), e);
    }
}
```

**UpdateClassSectionFrm – tìm học phần & cập nhật:**

```java
cls.setClassId(inClassId.getText().trim());
cls.setName(inName.getText().trim());
cls.setSemester(inSemester.getText().trim());
cls.setYear(inYear.getText().trim());
cls.setMaxStudents(Integer.parseInt(inMaxStudents.getText().trim()));
cls.setStatus((String) inStatus.getSelectedItem());
Course selected = (Course) cbCourse.getSelectedItem();
if (selected != null) cls.setCourseId(selected.getId());
if (classSectionDAO.updateClassSection(cls)) {
    JOptionPane.showMessageDialog(this, "Cập nhật lớp học phần thành công");
    if (onSaved != null) onSaved.run();
    dispose();
}
```

---

## 5.1.2.6. Module đăng ký học phần

Sinh viên tìm môn, chọn lớp, kiểm tra sĩ số rồi ghi nhận đăng ký
(`ClassSectionDAO.checkCapacity/registerClassSection` + `CourseRecordDAO.confirmRegistration`).

**ClassSectionDAO – kiểm tra sĩ số:**

```java
public boolean checkCapacity(String classSectionId) {
    String sql = "SELECT cs.maxStudents, " +
                 "(SELECT COUNT(*) FROM tblCourseRecord cr WHERE cr.tblClassSectionid = cs.id) AS enrolled " +
                 "FROM tblClassSection cs WHERE cs.id = ?";
    try (Connection con = getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, classSectionId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("enrolled") < rs.getInt("maxStudents");
        }
    } catch (SQLException e) {
        throw new RuntimeException("Lỗi checkCapacity: " + e.getMessage(), e);
    }
    return false;
}
```

**CourseRecordDAO – ghi nhận đăng ký:**

```java
public boolean confirmRegistration(CourseRecord r) {
    String sql = "INSERT INTO tblCourseRecord (id, enrolledAt, status, tblStudentid, tblClassSectionid) " +
                 "VALUES (?, ?, ?, ?, ?)";
    try (Connection con = getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, r.getId());
        ps.setDate(2, Date.valueOf(r.getEnrolledAt()));
        ps.setString(3, "Registered");
        ps.setString(4, r.getStudentId()); ps.setString(5, r.getClassSectionId());
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        throw new RuntimeException("Lỗi confirmRegistration: " + e.getMessage(), e);
    }
}
```

**ClassSectionRegistrationFrm – luồng đăng ký:**

```java
if (!classSectionDAO.checkCapacity(cls.getId())) {
    JOptionPane.showMessageDialog(this, "Lớp học phần đã đầy, vui lòng chọn lớp khác.");
    return;
}
if (courseRecordDAO.exists(currentUser.getId(), cls.getId())) {
    JOptionPane.showMessageDialog(this, "Bạn đã đăng ký lớp này rồi.");
    return;
}
CourseRecord r = new CourseRecord();
r.setId("CR-" + System.nanoTime());
r.setEnrolledAt(LocalDate.now());
r.setStudentId(currentUser.getId());
r.setClassSectionId(cls.getId());
if (courseRecordDAO.confirmRegistration(r)) {
    classSectionDAO.registerClassSection(cls.getId());
    JOptionPane.showMessageDialog(this, "Đăng ký lớp học phần thành công");
}
```

---

## 5.1.2.7. Module tạo mã QR

Tạo buổi học và sinh mã QR điểm danh (vẽ ảnh QR bằng ZXing), tắt QR khi đóng điểm danh
(`ClassSessionDAO.createSession` + `QRCodeDAO` + `QRCodeFrm`).

**QRCodeDAO – tạo & tắt QR:**

```java
public QRCode generateQr(ClassSession session) {
    LocalDateTime now = LocalDateTime.now();
    QRCode qr = new QRCode("QR-" + System.nanoTime(), now, now.plusMinutes(15),
                           21.0035, 105.8430, 50.0, "active");
    String insertQr = "INSERT INTO tblQRCode (id, createdAt, expiredAt, latitude, longitude, radius, status) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?)";
    String linkSession = "UPDATE tblClassSession SET tblQRCodeid = ?, status = 'open' WHERE id = ?";
    try (Connection con = getConnection()) {
        con.setAutoCommit(false);
        try (PreparedStatement ps = con.prepareStatement(insertQr)) {
            ps.setString(1, qr.getId());
            ps.setTimestamp(2, Timestamp.valueOf(qr.getCreatedAt()));
            ps.setTimestamp(3, Timestamp.valueOf(qr.getExpiredAt()));
            ps.setDouble(4, qr.getLatitude()); ps.setDouble(5, qr.getLongitude());
            ps.setDouble(6, qr.getRadius());   ps.setString(7, qr.getStatus());
            ps.executeUpdate();
        }
        try (PreparedStatement ps = con.prepareStatement(linkSession)) {
            ps.setString(1, qr.getId()); ps.setString(2, session.getId());
            ps.executeUpdate();
        }
        con.commit();
        session.setQrCodeId(qr.getId());
        return qr;
    } catch (SQLException e) {
        throw new RuntimeException("Lỗi generateQr: " + e.getMessage(), e);
    }
}

public boolean deactivateQr(String qrCodeId) {
    String sql = "UPDATE tblQRCode SET status = 'inactive' WHERE id = ?";
    try (Connection con = getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, qrCodeId);
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        throw new RuntimeException("Lỗi deactivateQr: " + e.getMessage(), e);
    }
}
```

**QRCodeFrm – tạo buổi học, sinh & vẽ QR:**

```java
ClassSection cls = classList.get(row);
currentSession = classSessionDAO.createSession(cls.getId());
currentQr = qrCodeDAO.generateQr(currentSession);
renderQrImage(currentQr.getId());     // vẽ ảnh QR bằng ZXing

private void renderQrImage(String content) {
    try {
        BitMatrix matrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, 220, 220);
        BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);
        qrImageLabel.setIcon(new ImageIcon(image));
    } catch (Exception ex) {
        qrImageLabel.setText("Lỗi render QR");
    }
}
```

---

## 5.1.2.8. Module quản lý điểm danh

Xem danh sách điểm danh theo buổi và sửa trạng thái thủ công
(`AttendanceDAO` + `AttendanceManageFrm`).

**AttendanceDAO – xem & điểm danh thủ công:**

```java
public List<Attendance> viewAttendance(String classSessionId) {
    List<Attendance> list = new ArrayList<>();
    String sql = "SELECT a.*, u.fullName AS sName FROM tblAttendance a " +
                 "JOIN tblUser u ON u.id = a.tblStudentid " +
                 "WHERE a.tblClassSessionid = ? ORDER BY a.tblStudentid";
    try (Connection con = getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, classSessionId);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Attendance a = map(rs);
                a.setStudentName(rs.getString("sName"));
                list.add(a);
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException("Lỗi viewAttendance: " + e.getMessage(), e);
    }
    return list;
}

public boolean markManualAttendance(Attendance a) {
    // tìm bản ghi đã có -> UPDATE; chưa có -> INSERT (method = 'MANUAL')
    String findSql = "SELECT id FROM tblAttendance WHERE tblClassSessionid = ? AND tblStudentid = ?";
    try (Connection con = getConnection();
         PreparedStatement find = con.prepareStatement(findSql)) {
        find.setString(1, a.getClassSessionId());
        find.setString(2, a.getStudentId());
        String existingId = null;
        try (ResultSet rs = find.executeQuery()) { if (rs.next()) existingId = rs.getString("id"); }
        if (existingId != null) {
            String upd = "UPDATE tblAttendance SET status = ?, method = 'MANUAL', attendedAt = ? WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(upd)) {
                ps.setString(1, a.getStatus());
                ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(3, existingId);
                return ps.executeUpdate() > 0;
            }
        } else {
            String ins = "INSERT INTO tblAttendance (id, attendedAt, method, status, tblClassSessionid, tblStudentid) " +
                         "VALUES (?, ?, 'MANUAL', ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(ins)) {
                ps.setString(1, a.getId() != null ? a.getId() : "ATT-" + System.nanoTime());
                ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(3, a.getStatus());
                ps.setString(4, a.getClassSessionId()); ps.setString(5, a.getStudentId());
                return ps.executeUpdate() > 0;
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException("Lỗi markManualAttendance: " + e.getMessage(), e);
    }
}
```

**AttendanceManageFrm – sửa trạng thái dòng đã chọn:**

```java
Attendance a = attendanceList.get(row);
String[] options = {"PRESENT", "ABSENT", "LATE"};
String newStatus = (String) JOptionPane.showInputDialog(this,
        "Trạng thái cho " + a.getStudentName(), "Sửa điểm danh",
        JOptionPane.PLAIN_MESSAGE, null, options, a.getStatus());
if (newStatus == null) return;
a.setStatus(newStatus); a.setMethod("MANUAL");
if (attendanceDAO.markManualAttendance(a)) {
    refreshTable();
    JOptionPane.showMessageDialog(this, "Cập nhật điểm danh thành công");
}
```

---

## 5.1.2.9. Module xem điểm

Sinh viên xem danh sách lớp đã đăng ký kèm điểm tổng kết và chi tiết điểm thành phần
(`CourseRecordDAO.viewGrade/viewGradeBySemester` + `ViewGradeFrm`).

**CourseRecordDAO – lấy điểm theo sinh viên:**

```java
public List<CourseRecord> viewGrade(String studentId) {
    List<CourseRecord> list = new ArrayList<>();
    String sql = "SELECT cr.*, cs.name AS csName, cs.classId AS csCode " +
                 "FROM tblCourseRecord cr JOIN tblClassSection cs ON cs.id = cr.tblClassSectionid " +
                 "WHERE cr.tblStudentid = ? ORDER BY cr.id";
    try (Connection con = getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, studentId);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                CourseRecord r = map(rs);
                r.setClassSectionName(rs.getString("csCode") + " - " + rs.getString("csName"));
                list.add(r);
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException("Lỗi viewGrade: " + e.getMessage(), e);
    }
    return list;
}
```

**ViewGradeFrm – hiển thị điểm tổng kết & chi tiết:**

```java
records = courseRecordDAO.viewGrade(currentUser.getId());
for (CourseRecord r : records) {
    Double total = r.getTotalScore();   // TX 20% + GK 30% + CK 50%
    courseModel.addRow(new Object[]{ r.getClassSectionName(), r.getStatus(),
                                     total != null ? total : "Chưa có" });
}
// khi chọn 1 lớp -> hiển thị chi tiết score1/score2/score3/examScore
```

---

## 5.1.2.10. Module nhập / sửa điểm

Giảng viên tìm lớp, tải danh sách sinh viên, nhập điểm (kiểm tra [0,10]) rồi lưu hàng loạt
(`CourseRecordDAO.findByClassSection/enterGrade` + `GradeEntryFrm`).

**CourseRecordDAO – nhập điểm hàng loạt:**

```java
public boolean enterGrade(List<CourseRecord> records) {
    String sql = "UPDATE tblCourseRecord SET score1 = ?, score2 = ?, score3 = ?, examScore = ? WHERE id = ?";
    try (Connection con = getConnection()) {
        con.setAutoCommit(false);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (CourseRecord r : records) {
                setNullableDouble(ps, 1, r.getScore1());
                setNullableDouble(ps, 2, r.getScore2());
                setNullableDouble(ps, 3, r.getScore3());
                setNullableDouble(ps, 4, r.getExamScore());
                ps.setString(5, r.getId());
                ps.addBatch();
            }
            ps.executeBatch();
            con.commit();
            return true;
        } catch (SQLException e) { con.rollback(); throw e; }
    } catch (SQLException e) {
        throw new RuntimeException("Lỗi enterGrade: " + e.getMessage(), e);
    }
}
```

**GradeEntryFrm – kiểm tra & nộp điểm:**

```java
private Double parseScore(Object cell) {           // kiểm tra điểm trong [0,10]
    if (cell == null || cell.toString().trim().isEmpty()) return null;
    double v = Double.parseDouble(cell.toString().trim());
    if (v < 0 || v > 10) throw new IllegalArgumentException("Điểm phải trong khoảng [0, 10]: " + v);
    return v;
}

for (int i = 0; i < records.size(); i++) {
    CourseRecord r = records.get(i);
    r.setScore1(parseScore(model.getValueAt(i, 2)));
    r.setScore2(parseScore(model.getValueAt(i, 3)));
    r.setScore3(parseScore(model.getValueAt(i, 4)));
    r.setExamScore(parseScore(model.getValueAt(i, 5)));
}
if (courseRecordDAO.enterGrade(records))
    JOptionPane.showMessageDialog(this, "Nộp điểm thành công");
```

---

## 5.1.2.11. Các thành phần dùng chung

Các lớp hạ tầng được mọi module sử dụng lại: đọc cấu hình, kết nối CSDL, lớp DAO cơ sở,
tiện ích Redis (phiên đăng nhập) và Kafka (thông báo).

**AppConfig – đọc `app.properties`:**

```java
public final class AppConfig {
    private static final Properties PROPS = new Properties();
    static {
        try (InputStream in = AppConfig.class.getClassLoader().getResourceAsStream("app.properties")) {
            PROPS.load(in);
        } catch (IOException e) { throw new ExceptionInInitializerError(e); }
    }
    public static String get(String key) { return PROPS.getProperty(key); }
    public static int getInt(String key, int def) {
        String v = PROPS.getProperty(key);
        return v == null ? def : Integer.parseInt(v.trim());
    }
}
```

**DBConnection – kết nối JDBC:**

```java
public final class DBConnection {
    private static final String URL = AppConfig.get("db.url");
    private static final String USER = AppConfig.get("db.user");
    private static final String PASSWORD = AppConfig.get("db.password");
    static {
        try { Class.forName("org.postgresql.Driver"); }
        catch (ClassNotFoundException e) { throw new ExceptionInInitializerError(e); }
    }
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
```

**DAO – lớp cơ sở:**

```java
public abstract class DAO {
    protected Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }
}
```

**RedisUtil – quản lý phiên đăng nhập:**

```java
public final class RedisUtil {
    private static final JedisPool POOL =
            new JedisPool(AppConfig.get("redis.host", "localhost"),
                          AppConfig.getInt("redis.port", 6379));
    private static final int TTL = AppConfig.getInt("redis.session.ttl", 900);

    public static String saveSession(String userId) {
        String token = UUID.randomUUID().toString();
        try (Jedis jedis = POOL.getResource()) {
            jedis.setex("session:" + userId, TTL, token);
        }
        return token;
    }
    public static void deleteSession(String userId) {
        try (Jedis jedis = POOL.getResource()) { jedis.del("session:" + userId); }
    }
}
```

**KafkaUtil – producer thông báo:**

```java
public final class KafkaUtil {
    private static final String TOPIC = AppConfig.get("kafka.topic.notifications", "universe.notifications");
    private static final Producer<String, String> PRODUCER = createProducer();

    private static Producer<String, String> createProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", AppConfig.get("kafka.bootstrap", "localhost:9092"));
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());
        props.put("acks", "all");
        return new KafkaProducer<>(props);
    }
    public static void publish(String json) {
        PRODUCER.send(new ProducerRecord<>(TOPIC, json));
        PRODUCER.flush();
    }
}
```

**NotificationConsumer – đọc Kafka, ghi `tblNotification`:**

```java
private static void run() {
    Properties props = new Properties();
    props.put("bootstrap.servers", AppConfig.get("kafka.bootstrap", "localhost:9092"));
    props.put("group.id", AppConfig.get("kafka.consumer.group", "universe-notification-consumer"));
    props.put("key.deserializer", StringDeserializer.class.getName());
    props.put("value.deserializer", StringDeserializer.class.getName());
    props.put("auto.offset.reset", "earliest");
    try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
        consumer.subscribe(Collections.singletonList(KafkaTopic()));
        while (running) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
            for (ConsumerRecord<String, String> record : records) {
                Notification n = NotificationDAO.fromJson(record.value());
                NOTIFICATION_DAO.insert(n);     // ghi vào tblNotification
            }
        }
    }
}
```
