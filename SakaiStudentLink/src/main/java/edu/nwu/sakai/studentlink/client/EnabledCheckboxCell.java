package edu.nwu.sakai.studentlink.client;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class EnabledCheckboxCell extends CheckboxCell {

    private static final SafeHtml IN_CHECKED = SafeHtmlUtils
            .fromSafeConstant("<input type=\"checkbox\" tabindex=\"-1\" checked  />");

    private static final SafeHtml IN_CHECKED_DISABLED = SafeHtmlUtils
            .fromSafeConstant("<input type=\"checkbox\" tabindex=\"-1\" checked disabled />");

    private static final SafeHtml IN_UNCHECKED = SafeHtmlUtils
            .fromSafeConstant("<input type=\"checkbox\" tabindex=\"-1\"  />");

    private static final SafeHtml IN_UNCHECKED_DISABLED = SafeHtmlUtils
            .fromSafeConstant("<input type=\"checkbox\" tabindex=\"-1\" disabled />");

    private boolean disabled = false;

    public void render(Boolean value, Object key, SafeHtmlBuilder sb) {
        // Get the view data.
        Boolean viewData = getViewData(key);
        if (viewData != null && viewData.equals(value)) {
            clearViewData(key);
            viewData = null;
        }
        if (value != null && ((viewData != null) ? viewData : value)) {
            sb.append(isDisabled() ? IN_CHECKED_DISABLED : IN_CHECKED);
        }
        else {
            sb.append(isDisabled() ? IN_UNCHECKED_DISABLED : IN_UNCHECKED);
        }
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
