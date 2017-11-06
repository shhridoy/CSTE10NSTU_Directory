package com.cste10nstu.shhridoy.cste10nstu.ListViewData;

/**
 * Created by Dream Land on 11/7/2017.
 */

public class BirthdayListItems {

    private String Name;
    private String dateOfBirth;

    public BirthdayListItems(String name, String dateOfBirth) {
        Name = name;
        this.dateOfBirth = dateOfBirth;
    }

    public String getName() {
        return Name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }
}
