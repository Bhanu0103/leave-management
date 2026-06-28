package com.auth_service.dto;

public class KycSubmissionRequest {
    private String panCard;
    private String certificatesLink;

    public KycSubmissionRequest() {}

    public String getPanCard() { return panCard; }
    public void setPanCard(String panCard) { this.panCard = panCard; }

    public String getCertificatesLink() { return certificatesLink; }
    public void setCertificatesLink(String certificatesLink) { this.certificatesLink = certificatesLink; }
}
