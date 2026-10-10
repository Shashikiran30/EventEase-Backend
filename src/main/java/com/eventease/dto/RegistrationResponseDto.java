package com.eventease.dto;

import com.eventease.entity.EventRegistration;
import java.time.LocalDateTime;

public class RegistrationResponseDto {
    private Long id;
    private Long eventId;
    private String eventTitle;
    private LocalDateTime eventStartDate;
    private LocalDateTime eventEndDate;
    private String venueName;
    private Long userId;
    private String studentName;
    private String studentEmail;
    private String studentRollNo;
    private String studentDepartment;
    private String ticketCode;
    private LocalDateTime registeredAt;
    private String status;

    public RegistrationResponseDto() {}

    public static RegistrationResponseDto fromEntity(EventRegistration reg) {
        if (reg == null) return null;
        RegistrationResponseDto dto = new RegistrationResponseDto();
        dto.setId(reg.getId());
        if (reg.getEvent() != null) {
            dto.setEventId(reg.getEvent().getId());
            dto.setEventTitle(reg.getEvent().getTitle());
            dto.setEventStartDate(reg.getEvent().getStartDate());
            dto.setEventEndDate(reg.getEvent().getEndDate());
            if (reg.getEvent().getVenue() != null) {
                dto.setVenueName(reg.getEvent().getVenue().getName());
            }
        }
        if (reg.getUser() != null) {
            dto.setUserId(reg.getUser().getId());
        }
        dto.setStudentName(reg.getStudentName());
        dto.setStudentEmail(reg.getStudentEmail());
        dto.setStudentRollNo(reg.getStudentRollNo());
        dto.setStudentDepartment(reg.getStudentDepartment());
        dto.setTicketCode(reg.getTicketCode());
        dto.setRegisteredAt(reg.getRegisteredAt());
        dto.setStatus(reg.getStatus());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public LocalDateTime getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(LocalDateTime eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public LocalDateTime getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(LocalDateTime eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStudentRollNo() {
        return studentRollNo;
    }

    public void setStudentRollNo(String studentRollNo) {
        this.studentRollNo = studentRollNo;
    }

    public String getStudentDepartment() {
        return studentDepartment;
    }

    public void setStudentDepartment(String studentDepartment) {
        this.studentDepartment = studentDepartment;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
