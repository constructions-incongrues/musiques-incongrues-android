package com.headbangers.mi.model;

import java.util.Date;
import java.util.Map;

public class MILinkData {

    private Date contributionDate;
    private String contributorName;
    private Integer discussionId;
    private String discussionTitle;
    private String domainFqdn;
    private String domainParent;
    private String mimeType;
    private String url;

    private String title;
    
    public MILinkData(Map<String, Object> value) {
        this.contributorName = (String)value.get("contributor_name");
        this.discussionId = (Integer)value.get("discussion_id");
        this.discussionTitle = (String)value.get("discussion_name");
        this.domainFqdn = (String)value.get("domain_fqdn");
        this.domainParent = (String)value.get("domain_parent");
        this.mimeType = (String)value.get("mime_type");
        this.url = (String)value.get("url");
        
        this.title = this.url.substring(this.url.lastIndexOf("/") + 1).replaceAll("%20", " ");
    }

    public Date getContributionDate() {
        return contributionDate;
    }

    public String getContributorName() {
        return contributorName;
    }

    public Integer getDiscussionId() {
        return discussionId;
    }

    public String getDiscussionTitle() {
        return discussionTitle;
    }

    public String getDomainFqdn() {
        return domainFqdn;
    }

    public String getDomainParent() {
        return domainParent;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getUrl() {
        return url;
    }
    
    public String getTitle() {
        return title;
    }
    
}
