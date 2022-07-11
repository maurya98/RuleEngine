package com.paisabazaar.rulesImpl.loanRuleEngine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetails {
    String firstName;
    String lastName;
    Integer age;
    Long accountNumber;
    Double monthlySalary;
    String bank;
    Integer cibilScore;
    Double requestedLoanAmount;
    String companyCategory;
    
    Double annualIncome;
    boolean bureauExists;
    String cityName;
    int cityId;
    String clickId;
    Long companyId;
    String companyName;
    int currentWorkExperience;
    String customerId;
    String customerName;
    String dateOfBirth;
    String email;
    String employmentTypeId;
    int exitPageNumber;
    String genderId;
    String hasPreviousLoan;
    int hostId;
    boolean isBureauUser;
    boolean isUnsecuredUser;
    Long loanRequestAmount;
    int loanRequestedTenture;
    String mobileNumber;
    //Long monthlySalary;
    String panNumber;
    int pincode;
    int plStep;
    String productIdentifier;
    String productType;
    int residenceTypeId;
    int SalaryBankId;
    String stateName;
    int totalWorkExperience;
    /**
     * annual_income: 720000
bureauExists: false
cityName: "Gurgaon"
city_id: 555
clickId: "307957312"
company_id: 296788
company_name: "TATA CONSULTANCY SERVICES LTD (TCS)"
current_work_experience: 72
customerId: "0004c9df-f294-45f6-9665-67f3e0bf1689"
customer_name: "Ankit kumar"
date_of_birth: "1970-01-01"
email: "asd@gmail.com"
employment_type_id: "1"
exit_page_number: 3
gender_id: "1"
has_previous_loan: "-1"
host_id: 5
isBureauUser: false
isUnsecuredUser: true
loan_requested_amount: 1000000
loan_requested_tenure: 3
mobileNumber: "9111111111"
mobile_number: "9111111111"
monthly_income: 60000
pan_number: "LATPC3341S"
pincode: 122001
pl_step: 1
product_identifier: "pl"
product_type: "4"
residence_type_id: 1
salary_bank_id: "2"
session_id: "fb064e38-e55b-41de-bab6-26b8b7d0a84d"
skip_validation: true
split_validation_criteria: true
stateName: "Delhi-NCR"
total_work_experience: 72
type: "pl"
undefined: true
visitId: "fb064e38-e55b-41de-bab6-26b8b7d0a84d"
visitorId: "08696a99-ba0d-4b98-95a7-528c6facba7b"
     */
}
