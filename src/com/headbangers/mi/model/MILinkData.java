package com.headbangers.mi.model;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

public class MILinkData {

    private String contributionDate;
    private String contributorName;
    private Integer discussionId;
    private String discussionTitle;
    private String domainFqdn;
    private String domainParent;
    private String mimeType;
    private String url;

    private String decodedUrl;

    private String title;

    public MILinkData(Map<String, Object> value) {

        this.contributorName = (String) value.get("contributor_name");
        this.discussionId = (Integer) value.get("discussion_id");
        this.discussionTitle = (String) value.get("discussion_name");
        this.domainFqdn = (String) value.get("domain_fqdn");
        this.domainParent = (String) value.get("domain_parent");
        this.mimeType = (String) value.get("mime_type");
        this.url = (String) value.get("url");

        this.contributionDate = ((String) value.get("contributed_at"))
                .split("T")[0];

        try {
            this.decodedUrl = URLDecoder.decode(this.url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            this.decodedUrl = this.url.replaceAll("%20", " ");
        }

        try{
            this.title = this.decodedUrl.substring(this.url.lastIndexOf("/") + 1);
        } catch (StringIndexOutOfBoundsException e){
            // erreur lors du substring ? 
            this.title = this.decodedUrl;
        }
    }

    public String getContributionDate() {
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
