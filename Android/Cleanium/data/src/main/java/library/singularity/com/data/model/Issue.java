package library.singularity.com.data.model;

import com.parse.ParseObject;

import library.singularity.com.data.utils.BasicValidator;
import library.singularity.com.data.utils.Constants;

public class Issue {
    private String reason;
    private String description;
    private String orderId;

    public Issue() {
        reason = null;
        description = null;
        orderId = null;
    }

    public Issue(ParseObject issue) {
        super();
        if (issue == null) return;

        reason = issue.getString(Constants.PARSE_ISSUE_REASON);
        description = issue.getString(Constants.PARSE_ISSUE_DESCRIPTION);
        orderId = issue.getParseObject(Constants.PARSE_ISSUE_ORDER).getObjectId();
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public boolean isReasonValid() {
        return BasicValidator.isValidString(reason);
    }

    public boolean isDescriptionValid() {
        return BasicValidator.isValidString(description);
    }

    public boolean isOrderIdValid() {
        return BasicValidator.isValidString(orderId);
    }
}
