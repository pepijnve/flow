/*
 * Copyright 2000-2022 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.flow.tests.data.bean;

import java.io.Serializable;
import java.util.Calendar;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class BeanToValidate implements Serializable {

    @NotNull
    @Size(min = 3, max = 16)
    private String firstname;

    @NotNull(message = "Last name must not be empty")
    private String lastname;

    @Min(value = 18, message = "Must be 18 or above")
    @Max(150)
    private int age;

    @Digits(integer = 3, fraction = 2)
    private String decimals;

    @Pattern(regexp = "V*", message = "Must start with letter V")
    @Size(min = 3, max = 6, message = "Must contain 3 - 6 letters")
    private String nickname;

    @Past
    private Calendar dateOfBirth;

    @NotNull
    @Valid
    private Address[] addresses;

    @NotNull
    @Valid
    private Address address;

    private final String readOnlyProperty = "READONLY DATA";

    private String writeOnlyProperty;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDecimals() {
        return decimals;
    }

    public void setDecimals(String decimals) {
        this.decimals = decimals;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Calendar getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Calendar dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Address[] getAddresses() {
        return addresses;
    }

    public void setAddresses(Address[] address) {
        addresses = address;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getReadOnlyProperty() {
        return readOnlyProperty;
    }

    public void setWriteOnlyProperty(String writeOnlyProperty) {
        this.writeOnlyProperty = writeOnlyProperty;
    }
}
