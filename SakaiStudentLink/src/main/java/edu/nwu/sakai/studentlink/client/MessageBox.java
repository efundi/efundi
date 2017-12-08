package edu.nwu.sakai.studentlink.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;

import edu.nwu.sakai.studentlink.resources.SakaiStudentLinkMessages;

@SuppressWarnings("deprecation")
public class MessageBox extends DialogBox implements ClickHandler {

    public MessageBox(String label, String message) {
        setText(label);
        Button closeButton = new Button(SakaiStudentLinkMessages.INSTANCE.close(), this);
        HTML msg = new HTML("<center>" + message + "</center>", true);
        DockPanel dock = new DockPanel();
        dock.setSpacing(4);
        dock.add(closeButton, DockPanel.SOUTH);
        dock.add(msg, DockPanel.NORTH);
        dock.setCellHorizontalAlignment(closeButton, DockPanel.ALIGN_RIGHT);
        dock.setWidth("100%");
        setWidget(dock);
    }

    public void onClick(ClickEvent arg0) {
        hide();
    }
}