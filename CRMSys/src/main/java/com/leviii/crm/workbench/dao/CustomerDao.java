package com.leviii.crm.workbench.dao;

import com.leviii.crm.workbench.domain.Customer;

import java.util.List;

public interface CustomerDao {

    Customer getCustomerByName(String company);

    int save(Customer customer);

    List<String> getCustomerName(String name);
}
