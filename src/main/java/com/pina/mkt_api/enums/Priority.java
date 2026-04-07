package com.pina.mkt_api.enums;

public enum Priority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL;

    public static class User {
        private String name;
        private String email;
        private String password;
        private String phoneNumber;
        private String cpf;
        private String cpnj;
        private String socialReason;
        private String dateOfBirth;

        public User(String name, String email, String password, String phoneNumber, String cpf, String cpnj, String socialReason, String dateOfBirth) {
            this.name = name;
            this.email = email;
            this.password = password;
            this.phoneNumber = phoneNumber;
            this.cpf = cpf;
            this.cpnj = cpnj;
            this.socialReason = socialReason;
            this.dateOfBirth = dateOfBirth;
        }

        public User() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getCpf() {
            return cpf;
        }

        public void setCpf(String cpf) {
            this.cpf = cpf;
        }

        public String getCpnj() {
            return cpnj;
        }

        public void setCpnj(String cpnj) {
            this.cpnj = cpnj;
        }

        public String getSocialReason() {
            return socialReason;
        }

        public void setSocialReason(String socialReason) {
            this.socialReason = socialReason;
        }

        public String getDateOfBirth() {
            return dateOfBirth;
        }

        public void setDateOfBirth(String dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
        }
    }
}
