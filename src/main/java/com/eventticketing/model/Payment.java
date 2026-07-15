package com.eventticketing.model;

import java.time.LocalDateTime;

public class Payment {
    private int paymentId;
    private int bookingId;
    private double amount;
    private String method;
    private String status; // PENDING, SUCCESS, FAILED
    private LocalDateTime paidAt;
    private String transactionId;

    public Payment() {}

    public Payment(int paymentId, int bookingId, double amount, String method,
                    String status, LocalDateTime paidAt, String transactionId) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.paidAt = paidAt;
        this.transactionId = transactionId;
    }

    /** Simulates a payment gateway call (mirrors <<include>> Process Payment use case). */
    public boolean processPayment() {
        // Simulated success — plug in a real payment gateway SDK here.
        this.status = "SUCCESS";
        this.paidAt = LocalDateTime.now();
        this.transactionId = "TXN-" + System.currentTimeMillis();
        return true;
    }

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
}
