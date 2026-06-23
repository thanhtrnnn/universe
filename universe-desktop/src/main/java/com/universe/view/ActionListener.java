package com.universe.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * Cầu nối sự kiện theo sơ đồ lớp: giữ tên {@code ActionListener} và
 * {@code actionPerformed(ActionEvent)} kiểu cổ điển, nhưng vẫn cắm thẳng
 * vào JavaFX vì kế thừa {@link EventHandler}<{@link ActionEvent}>.
 *
 * Cách dùng trong Frm:
 * <pre>
 *   public class XxxFrm ... implements ActionListener {
 *       btnSave.setOnAction(this);
 *       public void actionPerformed(ActionEvent e) { ... e.getSource() ... }
 *   }
 * </pre>
 */
public interface ActionListener extends EventHandler<ActionEvent> {

    /** JavaFX gọi handle(); ta chuyển tiếp sang actionPerformed() cho khớp diagram. */
    @Override
    default void handle(ActionEvent e) {
        actionPerformed(e);
    }

    /** Xử lý sự kiện hành động (theo class diagram). */
    void actionPerformed(ActionEvent e);
}
