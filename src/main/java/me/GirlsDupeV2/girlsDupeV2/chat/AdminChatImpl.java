package me.GirlsDupeV2.girlsDupeV2.chat;

public class AdminChatImpl implements StaffChatType {

    @Override
    public String getCommand() {
        return "adminchat";
    }

    @Override
    public String getToggleCommand() {
        return "actoggle";
    }

    @Override
    public String getLockCommand() {
        return "acdisable";
    }

    @Override
    public String getPrefix() {
        return "adminchat";
    }

    @Override
    public String getPermission() {
        return "staff.adminchat";
    }

    @Override
    public String getType() {
        return "admin";
    }
}