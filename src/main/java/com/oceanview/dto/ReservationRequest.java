package com.oceanview.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservationRequest {

    @NotBlank(message = "Guest name is required")
    private String guestName;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "NIC number is required")
    @Pattern(regexp = "^(\\d{9}[vVxX]|\\d{12})$", message = "NIC must be 10 digits + V/X or 12 digits")
    private String nicNumber;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^[\\d\\s\\-+]{7,15}$", message = "Invalid contact number")
    private String contactNumber;

    @NotBlank(message = "Room type is required")
    private String roomType;

    private String roomId;

    @NotNull(message = "Check-in date is required")
    private LocalDate checkInDate;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime checkInTime;

    @NotNull(message = "Check-out date is required")
    private LocalDate checkOutDate;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime checkOutTime;

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getNicNumber() { return nicNumber; }
    public void setNicNumber(String nicNumber) { this.nicNumber = nicNumber; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
    public LocalTime getCheckInTime() { return checkInTime; }
    public void setCheckInTime(LocalTime checkInTime) { this.checkInTime = checkInTime; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }
    public LocalTime getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(LocalTime checkOutTime) { this.checkOutTime = checkOutTime; }
}
